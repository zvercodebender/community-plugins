package ext.deployit.community.cli.manifestexport.convert;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static ext.deployit.community.cli.manifestexport.ci.ConfigurationItems.DEPLOYABLE_ARTIFACT_TYPE;
import static ext.deployit.community.cli.manifestexport.ci.ConfigurationItems.EAR_TYPE;
import static ext.deployit.community.cli.manifestexport.ci.ConfigurationItems.EJB_JAR_TYPE;
import static ext.deployit.community.cli.manifestexport.ci.ConfigurationItems.WAR_TYPE;
import static ext.deployit.community.cli.manifestexport.ci.ConfigurationItems.nameFromId;
import static ext.deployit.community.cli.manifestexport.dar.DarManifestBuilder.DarEntry.toCiAttribute;
import static ext.deployit.community.cli.manifestexport.dar.ManifestBuilder.getAttributeNameErrors;
import static java.lang.String.format;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Manifest;

import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry;
import com.xebialabs.deployit.plugin.api.reflect.PropertyDescriptor;
import com.xebialabs.deployit.plugin.api.reflect.PropertyKind;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;

import ext.deployit.community.cli.manifestexport.dar.DarManifestBuilder;
import ext.deployit.community.cli.manifestexport.dar.DarManifestBuilder.DarEntry;
import ext.deployit.community.cli.manifestexport.service.RepositoryHelper;

public class PackageContentAnalyzer {
    // jee-plugin is not available in the CLI
    protected static final String EAR_EXTENSION = ".ear";
    protected static final String WAR_EXTENSION = ".war";
    protected static final String EJB_JAR_EXTENSION = ".jar";

    protected final RepositoryHelper repository;

    public PackageContentAnalyzer(RepositoryHelper repository) {
        this.repository = repository;
    }

    public ManifestAndLogMessages extractFromPackage(
            ConfigurationItem deploymentPackage) {
        DarManifestBuilder manifestBuilder = new DarManifestBuilder();
        List<String> logMessages = convertToManifestEntries(
                deploymentPackage, manifestBuilder);
        return new ManifestAndLogMessages(manifestBuilder.build(), logMessages);
    }

    @SuppressWarnings("unchecked")
    private List<String> convertToManifestEntries(ConfigurationItem deploymentPackage,
            DarManifestBuilder manifestBuilder) {
        List<String> logMessages = newArrayList();
        manifestBuilder.setApplication(
                nameFromId((String) deploymentPackage.getProperty("application")));
        manifestBuilder.setVersion(nameFromId(deploymentPackage.getId()));

        for (String deployableId : (Collection<String>) deploymentPackage.getProperty("deployables")) {
            ConfigurationItem deployable = repository.readExisting(deployableId);
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

    private boolean isArtifact(ConfigurationItem ci) {
        return DescriptorRegistry.getDescriptor(ci.getType()).getInterfaces().contains(DEPLOYABLE_ARTIFACT_TYPE);
    }

    private String getExtension(ConfigurationItem deployable) {
        // some special case handling - not comprehensive by any means!
        Type type = deployable.getType();
        if (type.equals(Type.valueOf(EAR_TYPE))) {
            return EAR_EXTENSION;
        } else if (type.equals(Type.valueOf(WAR_TYPE))) {
            return WAR_EXTENSION;
        } else if (type.equals(Type.valueOf(EJB_JAR_TYPE))) {
            return EJB_JAR_EXTENSION;
        } else {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getPropertyValues(ConfigurationItem ci, List<String> logMessages) {
        Map<String, String> valuesForManifest = newHashMap();
        for (PropertyDescriptor propertyDescriptor 
                : DescriptorRegistry.getDescriptor(ci.getType()).getPropertyDescriptors()) {

            String propertyName = propertyDescriptor.getName();
            Object value = ci.getProperty(propertyName);

            if (value == null) {
                continue;
            }

            PropertyKind kind = propertyDescriptor.getKind();
            switch(kind) {
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
            String propertyName, ConfigurationItem ci, List<String> logMessages) {
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
