/*
 * @(#)ConfigParser.java     22 Jul 2011
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package ext.deployit.community.cli.plainarchive.config;

import static com.google.common.base.Functions.forMap;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.fromProperties;
import static com.google.common.collect.Maps.transformValues;
import static org.apache.commons.lang.StringUtils.substringAfter;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

import ext.deployit.community.cli.plainarchive.matcher.ConfigurationItemMatcher;


public class ConfigParser implements Supplier<List<ConfigurationItemMatcher>>{
    // only two properties are required by every rule - 'type' and 'returned.ci' 
    private static final int MIN_PROPERTIES_PER_RULE = 2;
    
    private final List<ConfigurationItemMatcher> matchers;
    
    public ConfigParser(@Nonnull Properties config, @Nonnull RuleParser ruleParser) {
        matchers = parseRules(config, ruleParser);
    }

    private static List<ConfigurationItemMatcher> parseRules(Properties config,
            RuleParser parser) {
        List<ConfigurationItemMatcher> matchers = newLinkedList();
        Map<String, String> configProperties = fromProperties(config);
        
        int maxNumRules = config.size() / MIN_PROPERTIES_PER_RULE;
        // rules are numbered beginning with 1
        for (int i = 1; i <= maxNumRules; i++) {
            Map<String, String> nthRuleProperties = new RulePropertiesCollector(i).apply(configProperties);
            if (nthRuleProperties.isEmpty()) {
                // only support consecutive numbering
                break;
            }
            matchers.add(parser.apply(nthRuleProperties));
        }
        return matchers;
    }
    
    // finds the rules for the given index and strips the "rule.N" prefix from the keys
    private static class RulePropertiesCollector implements Function<Map<String, String>, Map<String, String>> {
        private static final String RULE_PROPERTY_PREFIX = "rule.";
        
        private final String indexedRulePrefix;

        private RulePropertiesCollector(int ruleIndex) {
            indexedRulePrefix = RULE_PROPERTY_PREFIX + ruleIndex + '.';
        }
        
        @Override
        public Map<String, String> apply(Map<String, String> from) {
            KeyTransformer<String, String, String> prefixStripper = new KeyTransformer<String, String, String>(
                    new Function<String, String>() {
                        @Override
                        public String apply(String from) {
                            return substringAfter(from, indexedRulePrefix);
                        }
                    });
            // expecting non-null values
            return copyOf(prefixStripper.apply(filterKeys(from, 
                    new Predicate<String>() {
                        @Override
                        public boolean apply(String input) {
                            return input.startsWith(indexedRulePrefix);
                        }
                    })));
        }
    }
    
    // assumes the transform function returns unique values
    private static class KeyTransformer<K1, V, K2> implements Function<Map<K1, V>, Map<K2, V>> {
        private final Function<K1, K2> keyTransform;
        
        private KeyTransformer(Function<K1, K2> keyTransform) {
            this.keyTransform = keyTransform;
        }

        @Override
        public Map<K2, V> apply(Map<K1, V> input) {
            Map<K2, K1> newKeys = Maps.uniqueIndex(input.keySet(), keyTransform);
            return transformValues(newKeys, forMap(input));
        }
    }

    @Override
    public @Nonnull List<ConfigurationItemMatcher> get() {
        return matchers;
    }
    
}
