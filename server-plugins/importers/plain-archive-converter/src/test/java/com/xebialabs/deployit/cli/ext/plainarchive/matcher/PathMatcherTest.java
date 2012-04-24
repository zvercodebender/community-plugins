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

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.cli.ext.plainarchive.PlainArchiveConverterTest;
import com.xebialabs.deployit.cli.ext.plainarchive.matcher.PathMatcher.PathMatcherFactory;

import de.schlichtherle.truezip.file.TFile;

/**
 * Unit tests for the {@link PathMatcher}
 */
public class PathMatcherTest {
    private PathMatcher matcher = new PathMatcher(ImmutableMap.of(
            "type", PathMatcherFactory.MATCHER_TYPE,
            "path", "deployables/BEModule-1.0.ear",
            "returned.ci", "jee.Ear",
            "ci.properties", "name=$1"));
    
    @Test
    public void matchesAgainstPathFromTopLevelArchive() {
        // shouldn't match because the path doesn't *start* with "deployables"
        TFile file = tFile("inner-archive.zip/deployables/BEModule-1.0.ear");
        assertFalse("Expected matcher not to match file", matcher.matches(file));
    }
    
    // TFile's can't be mocked and must be in an archive to have an enclEntryName
    private static TFile tFile(String entryName) {
        return new TFile(PlainArchiveConverterTest.TEST_NESTED_ARCHIVE_PATH + "/"+ entryName);
    }
}
