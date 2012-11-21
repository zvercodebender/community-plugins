package ext.deployit.community.plugin.percontainerdict.util;

import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import com.xebialabs.deployit.plugin.api.reflect.Descriptor;
import com.xebialabs.deployit.plugin.api.reflect.PropertyDescriptor;
import com.xebialabs.deployit.plugin.api.reflect.PropertyKind;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Dictionary;

public class ContainerDictionaryGenerator {

    private static final String EMPTY = "<empty>";
    private static final String DEFAULT_SEPARATOR = "_";

    public Dictionary generate(Container container) {
        return getContainerDictionary("container", container);
    }

    private Dictionary getContainerDictionary(String parent, Container container) {
        final Map<String, String> entries = Maps.newHashMap();
        final Dictionary dictionary = new Dictionary();
        dictionary.setEntries(entries);

        if (generateContainerDictionary(container)) {
            LOGGER.debug("generate dictionary for {}", container);
            final String separator = getSeparator(container);

            entries.put(Joiner.on(separator).join(parent, "id"), container.getId());
            entries.put(Joiner.on(separator).join(parent, "name"), container.getName());

            final Descriptor descriptor = container.getType().getDescriptor();
            final Collection<PropertyDescriptor> propertyDescriptors = descriptor.getPropertyDescriptors();

            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.isHidden()) {
                    continue;
                }

                if (propertyDescriptor.isPassword() && excludePasswords(container)) {
                    //TODO: If is Password --> encrypted Dictionary.
                    continue;
                }

                final PropertyKind kind = propertyDescriptor.getKind();
                switch (kind) {
                    case LIST_OF_CI:
                    case LIST_OF_STRING:
                    case MAP_STRING_STRING:
                    case SET_OF_CI:
                    case SET_OF_STRING:
                        continue;
                    case CI:
                        final ConfigurationItem ci = container.getProperty(propertyDescriptor.getName());
                        if (ci != null && ci instanceof Container) {
                            String subParent = Joiner.on(separator).join(parent, propertyDescriptor.getName());
                            entries.putAll(getContainerDictionary(subParent, (Container) ci).getEntries());
                        }
                        break;
                    case BOOLEAN:
                    case STRING:
                    case INTEGER:
                    case ENUM:
                        final String key = Joiner.on(separator).join(parent, propertyDescriptor.getName());
                        final Object value = container.getProperty(propertyDescriptor.getName());
                        final String valueAsString = (value == null ? EMPTY : value.toString());
                        entries.put(key, valueAsString);

                        break;
                }
            }
        }
        return dictionary;
    }

    private String getSeparator(final Container container) {
        if (container.hasProperty("containerDictionaryKeySeparator")) {
            return container.getProperty("containerDictionaryKeySeparator");
        }
        return DEFAULT_SEPARATOR;
    }

    private boolean generateContainerDictionary(final Container container) {
        if (container.hasProperty("generateContainerDictionary")) {
            final Boolean generateContainerDictionary = container.getProperty("generateContainerDictionary");
            return generateContainerDictionary;
        }
        return false;
    }

    private boolean excludePasswords(final Container container) {
        if (container.hasProperty("excludePasswords")) {
            final Boolean excludePasswords = container.getProperty("excludePasswords");
            return excludePasswords;
        }
        return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerDictionaryGenerator.class);
}
