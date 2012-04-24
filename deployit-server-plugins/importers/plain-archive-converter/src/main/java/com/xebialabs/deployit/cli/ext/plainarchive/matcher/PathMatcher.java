/*
 * @(#)PathMatcher.java     23 Jul 2011
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
import static com.xebialabs.deployit.cli.ext.plainarchive.io.TFiles.getTopLevelEntryName;

import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schlichtherle.truezip.file.TFile;

public class PathMatcher extends ConfigurationItemMatcher {
    private static final String PATH_PROPERTY = "path";
    private static final Logger LOGGER = LoggerFactory.getLogger(PathMatcher.class);
    
    protected final @Nonnull String pathToMatch;
    
    public PathMatcher(@Nonnull Map<String, String> config) {
        super(config);
        pathToMatch = config.get(PATH_PROPERTY);
    }
    
    @Override
    protected void validate(Map<String, String> config) {
        super.validate(config);
        checkArgument(config.containsKey(PATH_PROPERTY), 
                "config property '%s' is required", PATH_PROPERTY);
    }

    @Override
    protected boolean matches(TFile input) {
        String topLevelEntryName = getTopLevelEntryName(input);
        boolean matches = topLevelEntryName.equals(pathToMatch);
        LOGGER.trace("Attempted to match path '{}' against literal '{}': {}",
                new Object[] { topLevelEntryName, pathToMatch, (matches ? "succeeded" : "failed") });
        return matches;
    }
    
    public static class PathMatcherFactory implements MatcherFactory {
        public static final String MATCHER_TYPE = "path";
        
        public String getMatcherType() {
            return MATCHER_TYPE;
        }
        
        public PathMatcher from(Map<String, String> config) {
            return new PathMatcher(config);
        }
    }
}
