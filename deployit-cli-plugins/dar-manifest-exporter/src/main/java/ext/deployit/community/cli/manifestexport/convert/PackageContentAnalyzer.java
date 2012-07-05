package ext.deployit.community.cli.manifestexport.convert;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static ext.deployit.community.cli.manifestexport.ci.ConfigurationItems.*;
import static ext.deployit.community.cli.manifestexport.dar.DarManifestBuilder.DarEntry.toCiAttribute;
import static ext.deployit.community.cli.manifestexport.dar.ManifestBuilder.getAttributeNameErrors;
import static java.lang.String.format;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Manifest;

import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.core.api.dto.ConfigurationItemPropertyDescriptorDto;
import com.xebialabs.deployit.core.api.dto.RepositoryObject;

import ext.deployit.community.cli.manifestexport.dar.DarManifestBuilder;
import ext.deployit.community.cli.manifestexport.dar.DarManifestBuilder.DarEntry;
import ext.deployit.community.cli.manifestexport.service.RepositoryHelper;
import ext.deployit.community.cli.manifestexport.service.TypeReflectionHelper;

public class PackageContentAnalyzer {
    // jee-plugin is not available in the CLI
    protected static final String EAR_EXTENSION = ".ear";
    protected static final String WAR_EXTENSION = ".war";
    protected static final String EJB_JAR_EXTENSION = ".jar";

    protected final RepositoryHelper repository;
    protected final TypeReflectionHelper types;

    public PackageContentAnalyzer(RepositoryHelper repository,
            TypeReflectionHelper types) {
        this.repository = repository;
        this.types = types;
    }

    public ManifestAndLogMessages extractFromPackage(
            RepositoryObject deploymentPackage) {
        DarManifestBuilder manifestBuilder = new DarManifestBuilder();
        List<String> logMessages = convertToManifestEntries(
                deploymentPackage, manifestBuilder);
        return new ManifestAndLogMessages(manifestBuilder.build(), logMessages);
    }

    @SuppressWarnings("unchecked")
    private List<String> convertToManifestEntries(RepositoryObject deploymentPackage,
            DarManifestBuilder manifestBuilder) {
        List<String> logMessages = newArrayList();
        Map<String, Object> deploymentPackageProperties = deploymentPackage.getValues();
        manifestBuilder.setApplication(
                nameFromId((String) deploymentPackageProperties.get("application")));
        manifestBuilder.setVersion(nameFromId(deploymentPackage.getId()));

        for (String deployableId : (Collection<String>) deploymentPackageProperties.get("deployables")) {
            RepositoryObject deployable = repository.readExisting(deployableId);
            Map<String, String> attributes = getPropertyValues(deployable, logMessages);
            if (isArtifact(deployable)) {
                // the DAR entry may have a file extension
                attributes.put("name", nameFromId(deployableId));
                logMessages.add(format("INFO: Please add file or folder '<DAR_ROOT>/%s%s' for artifact '%s' to package", 
                        nameFromId(deployableId), getExtension(deployable), deployableId));
            }
            manifestBuilder.addDarEntry(new DarEntry(deployable.getType(),
                    attributes, nameFromId(deployableId) + getExtension(deployable)));
        }
        return logMessages;
    }

    private boolean isArtifact(RepositoryObject ci) {
        return types.getDescriptor(ci).getInterfaces().contains(DEPLOYABLE_ARTIFACT_TYPE);
    }

    private String getExtension(RepositoryObject deployable) {
        // some special case handling - not comprehensive by any means!
        String type = deployable.getType();
        if (type.equals(EAR_TYPE)) {
            return EAR_EXTENSION;
        } else if (type.equals(WAR_TYPE)) {
            return WAR_EXTENSION;
        } else if (type.equals(EJB_JAR_TYPE)) {
            return EJB_JAR_EXTENSION;
        } else {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getPropertyValues(RepositoryObject ci, List<String> logMessages) {
        Map<String, Object> ciValues = ci.getValues();
        Map<String, String> valuesForManifest = newHashMap();
        for (ConfigurationItemPropertyDescriptorDto propertyDescriptor 
                : types.getPropertyDescriptors(ci)) {

            if (!propertyDescriptor.isEditable()) {
                continue;
            }

            String propertyName = propertyDescriptor.getName();
            Object value = ciValues.get(propertyName);

            if (value == null) {
                continue;
            }

            switch (propertyDescriptor.getType()) {
            case BOOLEAN:
            case INTEGER:
            case STRING:
            case ENUM:
                if (validateAttributeName(propertyName, propertyName, ci, logMessages)) {
                    valuesForManifest.put(propertyName, String.valueOf(value));
                }
                break;
            case CI:
                // assuming the CI is an item in the same package
                if (validateAttributeName(propertyName, propertyName, ci, logMessages)) {
                    valuesForManifest.put(propertyName, nameFromId((String) value));
                }
                break;
            case SET_OF_STRING:
            case LIST_OF_STRING:
                int strCount = 1;
                for (String entry : (Collection<String>) value) {
                    String attributeName = propertyName + "-EntryValue-" + strCount++;
                    if (validateAttributeName(attributeName, propertyName, ci, logMessages)) {
                        valuesForManifest.put(attributeName, entry);
                    }
                }
                break;
            case SET_OF_CI:
            case LIST_OF_CI:
                int ciCount = 1;
                for (String ciId : (Collection<String>) value) {
                    String attributeName = propertyName + "-EntryValue-" + ciCount++;
                    if (validateAttributeName(attributeName, propertyName, ci, logMessages)) {
                        valuesForManifest.put(attributeName, nameFromId(ciId));
                    }
                }
                break;
            case MAP_STRING_STRING:
                for (Entry<String, String> entry 
                        : ((Map<String, String>) value).entrySet()) {
                    String attributeName = propertyName + "-" + entry.getKey();
                    if (validateAttributeName(attributeName, propertyName, ci, logMessages)) {
                        valuesForManifest.put(attributeName, entry.getValue());
                    }
                }
                break;
            }
        }
        return valuesForManifest;
    }

    private static boolean validateAttributeName(String attributeName,
            String propertyName, RepositoryObject ci, List<String> logMessages) {
        Collection<String> errors = getAttributeNameErrors(toCiAttribute(attributeName));
        if (errors.isEmpty()) {
            return true;
        } else {
            logMessages.add(format("WARNING: Unable to convert entry '%s' of property '%s' of '%s' due to: %s", 
                    attributeName, propertyName, ci.getId(), errors));
            return false;
        }
    }

    public static class ManifestAndLogMessages {
        public final Manifest manifest;
        public final List<String> messages;

        private ManifestAndLogMessages(Manifest manifest, List<String> messages) {
            this.manifest = manifest;
            this.messages = ImmutableList.copyOf(messages);
        }
    }
}
