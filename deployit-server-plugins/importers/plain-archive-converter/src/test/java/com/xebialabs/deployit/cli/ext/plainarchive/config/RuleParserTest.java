/*
 * @(#)RuleParserTest.java     23 Jul 2011
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
package com.xebialabs.deployit.cli.ext.plainarchive.config;

import static com.qrmedia.commons.reflect.ReflectionUtils.getValue;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.xebialabs.deployit.cli.ext.plainarchive.matcher.ConfigurationItemMatcher;
import com.xebialabs.deployit.cli.ext.plainarchive.matcher.ConfigurationItemMatcher.MatcherFactory;
import com.xebialabs.deployit.cli.ext.plainarchive.matcher.PathMatcher;
import com.xebialabs.deployit.cli.ext.plainarchive.matcher.PathMatcher.PathMatcherFactory;

/**
 * Unit tests for {@link RuleParser}
 */
public class RuleParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void requiresTypeProperty() {
        new RuleParser(ImmutableSet.<MatcherFactory>of()).apply(
                ImmutableMap.<String, String>of());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requiresReturnedCiProperty() {
        new RuleParser(ImmutableSet.<MatcherFactory>of()).apply(
                ImmutableMap.of("type", "some-type"));
    }
    
    @Test
    public void correctlyReturnsRule() throws IllegalAccessException {
        String returnedType = "ConfigurationFiles";
        String path = "some-path";
        ConfigurationItemMatcher matcher = 
            new RuleParser(ImmutableSet.<MatcherFactory>of(new PathMatcherFactory())).apply(
                ImmutableMap.of("type", PathMatcherFactory.MATCHER_TYPE,
                        "returned.ci", returnedType, "path", path));
        assertTrue(format("Expected an instance of %s", PathMatcher.class.getName()), 
                matcher instanceof PathMatcher);
        assertEquals(returnedType, getValue(matcher, "returnedType"));
        assertEquals(path, getValue(matcher, "pathToMatch"));
    }
    
    @Test
    public void supportsCiPropertiesProperty() throws IllegalAccessException {
        String ciProperties = "props";
        ConfigurationItemMatcher matcher = 
            new RuleParser(ImmutableSet.<MatcherFactory>of(new PathMatcherFactory())).apply(
                ImmutableMap.of("type", PathMatcherFactory.MATCHER_TYPE,
                        "returned.ci", "ConfigurationFiles", "path", "some-path",
                        "ci.properties", ciProperties));
        assertEquals(ciProperties, getValue(matcher, "ciProperties"));
    }
}
