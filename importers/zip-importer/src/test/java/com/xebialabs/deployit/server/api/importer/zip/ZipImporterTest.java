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

import org.junit.Before;
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
  
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private final ZipImporter importer = new ZipImporter();
    private FileSource zipSource;
    private FileSource darSource;

    @Before
    public void populateImportDir() throws IOException {
        zipSource = new FileSource(tempFolder.newFile("plain-archive.zip"), false);
        darSource = new FileSource(tempFolder.newFile("myApp.dar"), false);
        new File(tempFolder.newFolder("child"), "plain-archive2.zip").createNewFile();
    }

    @Test
    public void listsZips() {
        assertEquals(ImmutableList.of("child/plain-archive2.zip", "plain-archive.zip"), 
                importer.list(tempFolder.getRoot()));
    }

    @Test
    public void scansOnlyRootDirectoryIfRequested() {
        assertEquals(ImmutableList.of("plain-archive.zip"), 
                new ZipImporter(false).list(tempFolder.getRoot()));
    }

    @Test
    public void handlesZips() {
        assertTrue("Expected ZIPs to be handled", importer.canHandle(zipSource));
    }
    
    @Test
    public void ignoresDars() throws IOException {
        assertFalse("Expected DARs to be ignored", importer.canHandle(darSource));
    }
    
    @Test
    public void extractsAppNameAndVersionFromFilename() {
        VersionedFilename nameAndVersion = importer.getNameAndVersion(zipSource);
        assertEquals("plain", nameAndVersion.name);
        assertEquals("archive", nameAndVersion.version);
    }
    
    @Test
    public void extractsAppNameAndVersionFromUri() throws MalformedURLException {
        VersionedFilename nameAndVersion = importer.getNameAndVersion(
                new UrlSource(new URL("http://localhost/hosted-plain-archive.zip")));
        assertEquals("hosted-plain", nameAndVersion.name);
        assertEquals("archive", nameAndVersion.version);
    }
}
