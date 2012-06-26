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
package ext.deployit.community.cli.plainarchive.matcher;

import static com.google.common.base.Preconditions.checkState;
import static ext.deployit.community.cli.plainarchive.io.Files2.listFiles;

import java.io.File;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;

import de.schlichtherle.truezip.file.TFile;

public class CarMatcher extends RegexMatcher {
    @VisibleForTesting
    static final String CONTAINS_LIBRARIES_PROPERTY = "containsLibs";

    private static final String LIBRARY_DIR = "WEB-INF/lib";
    private static final String LIBRARY_EXTENSION = "jar";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CarMatcher.class);
    
    private Boolean containsLibraries;
    
    public CarMatcher(@Nonnull Map<String, String> config) {
        super(config);
    }
    
    @Override
    protected boolean matches(TFile input) {
        if (!super.matches(input)) {
            return false;
        }
        
        containsLibraries = containsLibraries(input);
        LOGGER.trace("Checked if CAR '{}' contains libraries (ext '{}') in '{}': {}",
                new Object[] { input, LIBRARY_EXTENSION, LIBRARY_DIR, (containsLibraries ? "yes" : "no") });
        return true;
    }

    private static boolean containsLibraries(TFile car) {
        final String nameSuffixToMatch = "." + LIBRARY_EXTENSION;
        return Iterables.any(listFiles(new TFile(car, LIBRARY_DIR)), 
                new Predicate<File>() {
                    @Override
                    public boolean apply(File input) {
                        return input.getName().endsWith(nameSuffixToMatch);
                    }
                });
    }

    @Override
    protected Map<String, String> getCiProperties() {
        checkState(containsLibraries != null, "'getCiProperties' called before successful match");
        Builder<String, String> ciProperties = ImmutableMap.builder();
        ciProperties.putAll(super.getCiProperties());
        // not null!
        if (containsLibraries) {
            ciProperties.put(CONTAINS_LIBRARIES_PROPERTY, containsLibraries.toString());
        }
        return ciProperties.build();
    }


    @Override
    public String toString() {
        return "CarMatcher []";
    }

    public static class CarMatcherFactory implements MatcherFactory {
        public static final String MATCHER_TYPE = "car";
        
        public String getMatcherType() {
            return MATCHER_TYPE;
        }
        
        public CarMatcher from(Map<String, String> config) {
            return new CarMatcher(config);
        }
    }
}