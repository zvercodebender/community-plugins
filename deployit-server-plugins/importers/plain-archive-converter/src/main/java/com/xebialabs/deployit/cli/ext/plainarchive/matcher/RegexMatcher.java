/*
 * @(#)RegexMatcher.java     22 Jul 2011
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.xebialabs.deployit.cli.ext.plainarchive.io.TFiles.getTopLevelEntryName;
import static java.util.regex.Pattern.quote;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.cli.ext.plainarchive.base.UriQueryStrings;

import de.schlichtherle.truezip.file.TFile;

public class RegexMatcher extends ConfigurationItemMatcher {
    private static final String PATTERN_PROPERTY = "pattern";
    private static final Logger LOGGER = LoggerFactory.getLogger(RegexMatcher.class);
    
    protected final @Nonnull Pattern patternToMatch;
    private @Nullable java.util.regex.MatchResult patternMatchResult;
    
    public RegexMatcher(@Nonnull Map<String, String> config) {
        super(config);
        patternToMatch = Pattern.compile(config.get(PATTERN_PROPERTY));
    }
    
    @Override
    protected void validate(Map<String, String> config) {
        super.validate(config);
        checkArgument(config.containsKey(PATTERN_PROPERTY), 
                "config property '%s' is required", PATTERN_PROPERTY);
    }

    @Override
    protected boolean matches(TFile input) {
        String topLevelEntryName = getTopLevelEntryName(input);
        Matcher patternMatcher = patternToMatch.matcher(topLevelEntryName);
        boolean matches = patternMatcher.matches();
        LOGGER.trace("Attempted to match path '{}' against regex '{}': {}",
                new Object[] { topLevelEntryName, patternToMatch, (matches ? "succeeded" : "failed") });
        if (matches) {
            patternMatchResult = patternMatcher.toMatchResult();
        }
        return matches;
    }
    
    @Override
    protected Map<String, String> getCiProperties() {
        checkState(patternMatchResult != null, "'getCiProperties' called before successful match");
        if (ciProperties == null) {
            return ImmutableMap.of();
        }
        
        String resolvedCiProperties = ciProperties.contains("$1")
            ? ciProperties.replaceAll(quote("$1"), patternMatchResult.group(1))
            : ciProperties;
        return UriQueryStrings.toMap(resolvedCiProperties);
    }

    @Override
    public String toString() {
        return "RegexMatcher [patternToMatch=" + patternToMatch + "]";
    }

    public static class RegexMatcherFactory implements MatcherFactory {
        public static final String MATCHER_TYPE = "regex";
        
        public String getMatcherType() {
            return MATCHER_TYPE;
        }
        
        public RegexMatcher from(Map<String, String> config) {
            return new RegexMatcher(config);
        }
    }
}