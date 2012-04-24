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
package com.xebialabs.deployit.cli.ext.plainarchive.matcher;

import static com.xebialabs.deployit.cli.ext.plainarchive.PlainArchiveConverterTest.TEST_ARCHIVE_PATH;
import static com.xebialabs.deployit.cli.ext.plainarchive.PlainArchiveConverterTest.TEST_NESTED_ARCHIVE_PATH;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.cli.ext.plainarchive.matcher.RegexMatcher.RegexMatcherFactory;

import de.schlichtherle.truezip.file.TFile;

/**
 * Unit tests for the {@link RegexMatcher}
 */
public class RegexMatcherTest {
    private RegexMatcher matcher = new RegexMatcher(ImmutableMap.of(
            "type", RegexMatcherFactory.MATCHER_TYPE,
            "pattern", "deployables/([A-Za-z]+)(?:-.+)*\\.ear",
            "returned.ci", "jee.Ear",
            "ci.properties", "name=$1"));
    
    @Test
    public void correctlyMatchesAgainstPattern() {
        TFile file = tFile("deployables/petclinic-1.0.ear");
        assertTrue("Expected matcher to match file", matcher.matches(file));
    }
    
    @Test
    public void matchesAgainstPathFromTopLevelArchive() {
        // shouldn't match because the path doesn't *start* with "deployables"
        TFile file = tFile(TEST_NESTED_ARCHIVE_PATH, "inner-archive.zip/deployables/BEModule-1.0.ear");
        assertFalse("Expected matcher not to match file", matcher.matches(file));
    }
    
    @Test
    public void supportsMatchGroupReplacement() {
        String earName = "petclinic";
        TFile file = tFile(format("deployables/%s.ear", earName));
        // sets the match result
        matcher.matches(file);
        assertEquals(earName, matcher.getCiProperties().get("name"));
    }
    
    @Test
    public void supportsNullCiProperties() {
        RegexMatcher matcher = new RegexMatcher(ImmutableMap.of(
                "type", RegexMatcherFactory.MATCHER_TYPE,
                "pattern", "deployables/([A-Za-z]+)(?:\\-.)*\\.ear",
                "returned.ci", "Ear"));
        assertNotNull("Expected non-null DarEntry", 
                matcher.apply(tFile("deployables/petclinic.ear")).result);
    }
    
    // TFile's can't be mocked and must be in an archive to have an enclEntryName
    private static TFile tFile(String entryName) {
        return tFile(TEST_ARCHIVE_PATH, entryName);
    }
    
    private static TFile tFile(String archivePath, String entryName) {
        return new TFile(archivePath + "/" + entryName);
    }
}
