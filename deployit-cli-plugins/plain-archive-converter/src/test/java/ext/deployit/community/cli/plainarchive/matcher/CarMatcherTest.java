package ext.deployit.community.cli.plainarchive.matcher;
/*
 * @(#)RegexMatcherTest.java     23 Jul 2011
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


import static ext.deployit.community.cli.plainarchive.matcher.CarMatcher.CONTAINS_LIBRARIES_PROPERTY;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.schlichtherle.truezip.file.TFile;
import ext.deployit.community.cli.plainarchive.PlainArchiveConverterTest;
import ext.deployit.community.cli.plainarchive.matcher.CarMatcher.CarMatcherFactory;

/**
 * Unit tests for the {@link CarMatcher}
 */
public class CarMatcherTest {
    private CarMatcher matcher = new CarMatcher(ImmutableMap.of(
            "type", CarMatcherFactory.MATCHER_TYPE,
            "pattern", "deployables/([A-Za-z_]+)(?:-.+)*\\.car",
            "returned.ci", "Car",
            "ci.properties", "name=$1"));
    
    @Test
    public void findsNoLibrariesIfNotPresent() {
        TFile file = tFile("deployables/BEServices-1.0.car");
        // can't parse properties before this
        matcher.matches(file);
        assertTrue("Expected matcher not to find any libraries", 
                !matcher.getCiProperties().containsKey(CONTAINS_LIBRARIES_PROPERTY));
    }
    
    // TFile's can't be mocked and must be in an archive to have an enclEntryName
    private static TFile tFile(String entryName) {
        return new TFile(PlainArchiveConverterTest.TEST_ARCHIVE_PATH + "/"+ entryName);
    }

    @Test
    public void findsLibrariesIfPresent() {
        TFile file = tFile("deployables/BEServices_with_libs-1.0.car");
        // can't parse properties before this
        matcher.matches(file);
        assertEquals("Expected matcher to find libraries", TRUE.toString(), 
                matcher.getCiProperties().get(CONTAINS_LIBRARIES_PROPERTY));
    }
}
