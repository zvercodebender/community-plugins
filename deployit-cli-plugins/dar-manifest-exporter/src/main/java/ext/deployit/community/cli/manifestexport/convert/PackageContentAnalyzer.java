package ext.deployit.community.cli.manifestexport.convert;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static ext.deployit.community.cli.manifestexport.ci.ConfigurationItems.*;
import static ext.deployit.community.cli.manifestexport.dar.DarManifestBuilder.DarEntry.CI_ATTRIBUTE_PREFIX;
import static java.lang.String.format;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

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

    // see class comment for Attributes.Name
    protected static final int VALID_MANIFEST_ATTRIBUTE_LENGTH = 70;
    protected static final Pattern VALID_MANIFEST_ATTRIBUTE_PATTERN = 
        Pattern.compile("[0-9a-zA-Z_-]+"); 

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
                valuesForManifest.put(propertyName, String.valueOf(value));
                break;
            case CI:
                // assuming the CI is an item in the same package
                valuesForManifest.put(propertyName, nameFromId((String) value));
                break;
            case SET_OF_STRING:
            case LIST_OF_STRING:
                int strCount = 1;
                for (String entry : (Collection<String>) value) {
                    valuesForManifest.put(propertyName + "-EntryValue-" + strCount++, 
                            entry);
                }
                break;
            case SET_OF_CI:
            case LIST_OF_CI:
                int ciCount = 1;
                for (String ciId : (Collection<String>) value) {
                    valuesForManifest.put(propertyName + "-EntryValue-" + ciCount++, 
                            nameFromId(ciId));
                }
                break;
            case MAP_STRING_STRING:
                for (Entry<String, String> entry 
                        : ((Map<String, String>) value).entrySet()) {
                    String key = entry.getKey();
                    if (CI_ATTRIBUTE_PREFIX.length() + key.length() 
                            > VALID_MANIFEST_ATTRIBUTE_LENGTH) {
                        logMessages.add(format("WARNING: Unable to convert entry '%s' of map_string_string property '%s' of '%s': key is too long (max %d characters)", 
                                key, propertyName, ci.getId(), VALID_MANIFEST_ATTRIBUTE_LENGTH - CI_ATTRIBUTE_PREFIX.length()));
                    } else if (!VALID_MANIFEST_ATTRIBUTE_PATTERN.matcher(key).matches()) {
                        logMessages.add(format("WARNING: Unable to convert entry '%s' of map_string_string property '%s' of '%s': key contains invalid characters (only %s allowed)", 
                                key, propertyName, ci.getId(), VALID_MANIFEST_ATTRIBUTE_PATTERN.pattern()));
                    } else {
                        valuesForManifest.put(propertyName + "-" + entry.getKey(),
                                entry.getValue());
                    }
                }
                break;
            }
        }
        return valuesForManifest;
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
