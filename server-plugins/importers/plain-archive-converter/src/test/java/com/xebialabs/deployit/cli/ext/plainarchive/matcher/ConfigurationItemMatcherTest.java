/*
 * @(#)ConfigurationItemMatcherTest.java     23 Jul 2011
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
package com.xebialabs.deployit.cli.ext.plainarchive.matcher;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.annotation.Nullable;

import org.junit.Test;

import com.qrmedia.commons.reflect.ReflectionUtils;
import com.xebialabs.deployit.cli.ext.plainarchive.PlainArchiveConverterTest;

import de.schlichtherle.truezip.file.TFile;

/**
 * Unit tests for the {@link ConfigurationItemMatcher}
 */
public class ConfigurationItemMatcherTest {
    // TFile's can't be mocked and must be in an archive to have an enclEntryName
    private static final TFile DUMMY_TFILE = new TFile(PlainArchiveConverterTest.TEST_ARCHIVE_PATH + "/dummy");
    
    private static class StubMatcher extends ConfigurationItemMatcher {
        private final boolean matches;
        
        private StubMatcher(@Nullable String ciProperties, boolean matches) {
            super(getConfig(ciProperties));
            this.matches = matches;
        }
        
        private static Map<String, String> getConfig(String ciProperties) {
            Map<String, String> config = newHashMap();
            config.put("type", "stub");
            config.put("returned.ci", "Ear");
            config.put("ci.properties", ciProperties);
            return config;
        }
        
        @Override
        protected boolean matches(TFile input) {
            return matches;
        }
    }

    @Test
    public void correctlyConstructsFailedMatchResult() {
        ConfigurationItemMatcher matcher = new StubMatcher("name=dummy", false);
        assertFalse("Expected match to have failed", 
                matcher.apply(DUMMY_TFILE).matched);
    }
    
    @Test
    public void correctlyConstructsSuccessfulMatchResult() {
        ConfigurationItemMatcher matcher = new StubMatcher("name=dummy", true);
        assertTrue("Expected match to have succeeded", 
                matcher.apply(DUMMY_TFILE).matched);
    }

    @Test
    public void correctlyConstructsDarEntry() {
        ConfigurationItemMatcher matcher = new StubMatcher("name=dummy", true);
        assertNotNull("Expected non-null DarEntry", 
                matcher.apply(DUMMY_TFILE).result);
    }

    @Test
    public void correctlyParsesCiProperties() throws IllegalAccessException {
        ConfigurationItemMatcher matcher = new StubMatcher("name=dummy&foo=bar", true);
        Map<String, String> properties = ReflectionUtils.getValue(
                matcher.apply(DUMMY_TFILE).result, "properties");
        assertEquals(2, properties.size());
        assertEquals("dummy", properties.get("name"));
        assertEquals("bar", properties.get("foo"));
    }
    
    @Test
    public void supportsNullCiProperties() {
        ConfigurationItemMatcher matcher = new StubMatcher(null, true);
        assertNotNull("Expected non-null DarEntry", 
                matcher.apply(DUMMY_TFILE).result);
    }
}
