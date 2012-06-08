/*
 * @(#)ConfigParserTest.java     Jul 31, 2011
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

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.schlichtherle.truezip.file.TFile;
import ext.deployit.community.cli.plainarchive.config.ConfigParser;
import ext.deployit.community.cli.plainarchive.config.RuleParser;
import ext.deployit.community.cli.plainarchive.matcher.ConfigurationItemMatcher;
import ext.deployit.community.cli.plainarchive.matcher.ConfigurationItemMatcher.MatcherFactory;

/**
 * Unit tests for the {@link ConfigParser}
 */
public class ConfigParserTest {

    // matches all rules of type 'stub'
    private static class StubMatcherFactory implements MatcherFactory {
        private static final String RULE_TYPE = "stub";
        
        @Override
        public String getMatcherType() {
            return RULE_TYPE;
        }
        
        @Override
        public ConfigurationItemMatcher from(Map<String, String> config) {
            return new ConfigurationItemMatcher(config) {
                @Override
                protected boolean matches(TFile input) {
                    return false;
                }};
        }
    }
    
    @Test
    public void processesRulesUpToFirstMissingIndex() {
        Properties config = new Properties();
        // 8 properties could be 4 rules - 'returned.ci' and 'type' are the only required values
        config.put("rule.1.type", StubMatcherFactory.RULE_TYPE);
        config.put("rule.1.returned.ci", "Dummy");
        config.put("rule.2.type", StubMatcherFactory.RULE_TYPE);
        config.put("rule.2.returned.ci", "Dummy");
        config.put("rule.3.type", StubMatcherFactory.RULE_TYPE);
        config.put("rule.3.returned.ci", "Dummy");
        config.put("rule.3.other.prop", "Dummy");
        config.put("rule.3.other.prop.2", "Dummy");
        assertEquals(3, new ConfigParser(config, 
                new RuleParser(ImmutableSet.<MatcherFactory>of(new StubMatcherFactory())))
                .get().size());
    }
}
