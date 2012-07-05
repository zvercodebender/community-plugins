package ext.deployit.community.cli.manifestexport.dar;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

@ThreadSafe
public class ManifestBuilder {
    // see class comment for Attributes.Name
    protected static final int VALID_ATTRIBUTE_LENGTH = 70;
    protected static final Pattern VALID_ATTRIBUTE_PATTERN = 
        Pattern.compile("[0-9a-zA-Z_-]+");

    private static final String GENERATED_MANIFEST_VERSION = "1.0";

    private final ReentrantLock lock = new ReentrantLock();

    private final ConcurrentMap<String, String> mainAttributes = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<String, Map<String, String>> entryAttributes = new ConcurrentHashMap<String, Map<String, String>>();

    public ManifestBuilder addMainAttribute(@Nonnull String name, @Nonnull String value) {
        Collection<String> attributeNameErrors = getAttributeNameErrors(name);
        if (!attributeNameErrors.isEmpty()) {
            throw new IllegalArgumentException(attributeNameErrors.toString());
        }
        mainAttributes.put(checkNotNull(name), checkNotNull(value));
        return this;
    }

    public ManifestBuilder addEntryAttributes(@Nonnull String entryName, @Nonnull Map<String, String> attributes) {
        Collection<String> attributeErrors = getAttributeErrors(attributes);
        if (!attributeErrors.isEmpty()) {
            throw new IllegalArgumentException(attributeErrors.toString());
        }
        entryAttributes.putIfAbsent(entryName, new ConcurrentHashMap<String, String>(checkNotNull(attributes).size()));
        entryAttributes.get(entryName).putAll(attributes);
        return this;
    }

    /**
     * @param value the attribute name
     * @return a collection of error messages, empty if the attribute name is valid
     */
    public static @Nonnull Collection<String> getAttributeNameErrors(@Nonnull String value) {
        Builder<String> errors = ImmutableList.builder();
        if (value.length() > VALID_ATTRIBUTE_LENGTH) {
            errors.add(format("value '%s' is too long (max %d characters)", 
                    value, VALID_ATTRIBUTE_LENGTH));
        }

        if (!VALID_ATTRIBUTE_PATTERN.matcher(value).matches()) {
            errors.add(format("value '%s' contains invalid characters (only %s allowed)", 
                    value, VALID_ATTRIBUTE_PATTERN.pattern()));
        }

        return errors.build();
    }

    /**
     * @param attributes the attributes to verify
     * @return a collection of error messages, empty if all the attributes are valid
     */
    public static @Nonnull Collection<String> getAttributeErrors(@Nonnull Map<String, String> attributes) {
        Builder<String> errors = ImmutableList.builder();
        for (String attributeName : attributes.keySet()) {
            errors.addAll(getAttributeNameErrors(attributeName));
        }
        return errors.build();
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return (mainAttributes.isEmpty() && entryAttributes.isEmpty());
        } finally {
            lock.unlock();
        }
    }

    public @Nonnull Manifest build() {
        Manifest manifest = new Manifest();
        Attributes manifestMainAttributes = manifest.getMainAttributes();
        manifestMainAttributes.putValue(Name.MANIFEST_VERSION.toString(),
                GENERATED_MANIFEST_VERSION);
        Map<String, Attributes> manifestEntries = manifest.getEntries();
        lock.lock();
        try {
            for (Entry<String, String> mainAttribute : mainAttributes.entrySet()) {
                manifestMainAttributes.putValue(mainAttribute.getKey(), 
                        mainAttribute.getValue());
            }
            for (Entry<String, Map<String, String>> attributesForEntry : entryAttributes.entrySet()) {
                manifestEntries.put(attributesForEntry.getKey(),
                        toAttributes(attributesForEntry.getValue()));
            }
        } finally {
            lock.unlock();
        }
        return manifest;
    }

    private static Attributes toAttributes(Map<String, String> values) {
        Attributes attributes = new Attributes(values.size());
        for (Entry<String, String> value : values.entrySet()) {
            attributes.putValue(value.getKey(), value.getValue());
        }
        return attributes;
    }

}