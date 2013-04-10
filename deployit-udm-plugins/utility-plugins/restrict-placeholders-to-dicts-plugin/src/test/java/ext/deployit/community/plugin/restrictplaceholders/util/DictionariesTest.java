package ext.deployit.community.plugin.restrictplaceholders.util;

import static ext.deployit.community.plugin.restrictplaceholders.util.Dictionaries.consolidatedDictionary;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.plugin.api.udm.Dictionary;
import com.xebialabs.deployit.plugin.api.udm.Environment;

/**
 * Unit tests for {@link Dictionaries}
 */
public class DictionariesTest {

    @Test
    public void consolidatedDictionariesFlattensAndResolves() {
        Dictionary base = new Dictionary();
        base.getEntries().putAll(
            ImmutableMap.of("FOO", "foo", "BAR", "{{FOO}} and bar"));
        Dictionary override = new Dictionary();
        override.getEntries().putAll(
            ImmutableMap.of("BAZ", "{{FOO}} and {{BAR}} and baz", "FOO", "overridden foo"));
        Environment environment = new Environment();
        // earlier items are "higher up in the stack" than later ones
        environment.getDictionaries().addAll(ImmutableList.of(override, base));
        
        assertEquals(ImmutableMap.of("FOO", "overridden foo", 
                "BAR", "overridden foo and bar", 
                "BAZ", "overridden foo and overridden foo and bar and baz"), 
                consolidatedDictionary(environment));
    }

    @Test
    public void consolidatedDictionariesDoesNotCauseInfiniteLoops() {
        Dictionary dictionary = new Dictionary();
        dictionary.getEntries().put("FOO", "{{FOO}}");
        Environment environment = new Environment();
        environment.getDictionaries().add(dictionary);
        consolidatedDictionary(environment);
    }
}
