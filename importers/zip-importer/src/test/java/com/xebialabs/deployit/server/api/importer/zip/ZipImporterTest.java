/*
 * @(#)PlainArchiveConverterTest.java     23 Jul 2011
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
package com.xebialabs.deployit.server.api.importer.zip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.cli.ext.plainarchive.io.Filenames.VersionedFilename;
import com.xebialabs.deployit.service.importer.source.FileSource;
import com.xebialabs.deployit.service.importer.source.UrlSource;



/**
 * Unit tests for the {@link ZipImporter}
 */
public class ZipImporterTest {
    public static final String PLAIN_ARCHIVE = "src/test/resources/plain-archive.zip";
  
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
  
    @Test
    public void listsZips() {
        assertEquals(ImmutableList.of("plain-archive.zip"), 
                new ZipImporter().list(new File("src/test/resources")));
    }
    
    @Test
    public void handlesZips() {
        assertTrue("Expected ZIPs to be handled", 
                new ZipImporter().canHandle(new FileSource(PLAIN_ARCHIVE, false)));
    }
    
    @Test
    public void ignoresDars() throws IOException {
        assertFalse("Expected DARs to be ignored", 
                new ZipImporter().canHandle(new FileSource(tempFolder.newFile("myApp.dar"), true)));
    }
    
    @Test
    public void extractsAppNameAndVersionFromFilename() {
        VersionedFilename nameAndVersion = 
            ZipImporter.getNameAndVersion(new FileSource(PLAIN_ARCHIVE, false));
        assertEquals("plain", nameAndVersion.name);
        assertEquals("archive", nameAndVersion.version);
    }
    
    @Test
    public void extractsAppNameAndVersionFromUri() throws MalformedURLException {
        VersionedFilename nameAndVersion = ZipImporter.getNameAndVersion(
                new UrlSource(new URL("http://localhost/hosted-plain-archive.zip")));
        assertEquals("hosted-plain", nameAndVersion.name);
        assertEquals("archive", nameAndVersion.version);
    }

}
