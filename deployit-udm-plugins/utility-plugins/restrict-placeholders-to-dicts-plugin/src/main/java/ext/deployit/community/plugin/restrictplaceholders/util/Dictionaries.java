package ext.deployit.community.plugin.restrictplaceholders.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.reverse;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.plugin.api.udm.Dictionary;
import com.xebialabs.deployit.plugin.api.udm.Environment;

public class Dictionaries {
    private static final int MAX_REPLACEMENT_ITERATIONS = 10000;
    // "{{...}}"
    private static Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");

    public static Map<String, String> consolidatedDictionary(Environment environment) {
        // can't use ImmutableMap.Builder as it does not allow key overrides
        Map<String, String> flattenedDictionary = newHashMap();
        // top-most dictionaries *override* lower dictionaries
        for (Dictionary dictionary : reverse(environment.getDictionaries())) {
            flattenedDictionary.putAll(dictionary.getEntries());
        }
        resolvePlaceholdersInValues(flattenedDictionary);
        return ImmutableMap.copyOf(flattenedDictionary);
    }
    
    // modifies the input map
    private static void resolvePlaceholdersInValues(Map<String, String> entries) {
        // prevent infinite loops
        boolean replacedPlaceholders;
        int numIterations = 0;
        do {
            replacedPlaceholders = resolveFirstPlaceholdersInValues(entries);
            numIterations++;
        } while (replacedPlaceholders && numIterations < MAX_REPLACEMENT_ITERATIONS);
    }
    
    // modifies the input map
    private static boolean resolveFirstPlaceholdersInValues(Map<String, String> entries) {
        boolean foundPlaceholder = false;
        for (Entry<String, String> entry : entries.entrySet()) {
            String value = entry.getValue();
            Matcher placeholderMatches = PLACEHOLDER_PATTERN.matcher(value);
            if (placeholderMatches.find()) {
                foundPlaceholder = true;
                String placeholder = placeholderMatches.group(1);
                checkArgument(entries.containsKey(placeholder), 
                        "Dictionary entry '%s' refers to non-existent placeholder '%s'", value, placeholder);
                /*
                 * For convenience, only replace the first placeholder per entry found.
                 * This leads to multiple passes to resolve a value such as 
                 * "{{FOO}} and {{BAR}}", but is much easier than handling all the matching groups
                 */
                entries.put(entry.getKey(), placeholderMatches.replaceFirst(entries.get(placeholder)));
            }
        }
        return foundPlaceholder;
    }
}