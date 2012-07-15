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
package ext.deployit.community.cli.plainarchive;

import static ext.deployit.community.cli.plainarchive.PlainArchiveConverter.DEFAULT_APP_VERSION;
import static ext.deployit.community.cli.plainarchive.dar.DarManifestBuilder.APPLICATION_ATTRIBUTE_NAME;
import static ext.deployit.community.cli.plainarchive.dar.DarManifestBuilder.VERSION_ATTRIBUTE_NAME;
import static ext.deployit.community.cli.plainarchive.dar.DarWriter.DAR_EXTENSION;
import static ext.deployit.community.cli.plainarchive.dar.DarWriter.MANIFEST_PATH;
import static ext.deployit.community.cli.plainarchive.io.Files2.deleteOnExit;
import static ext.deployit.community.cli.plainarchive.io.Files2.getTempFilePath;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;

import org.junit.After;
import org.junit.Test;

import com.xebialabs.deployit.cli.api.ProxiesInstance;

import de.schlichtherle.truezip.file.TFile;
import ext.deployit.community.cli.plainarchive.dar.DarReader;

/**
 * Unit tests for the {@link PlainArchiveConverter}
 */
public class PlainArchiveConverterTest {
    public static final String TEST_ARCHIVE_PATH = "src/test/resources/plain-archive.zip";
    public static final String TEST_NESTED_ARCHIVE_PATH = "src/test/resources/plain-outer-archive.zip";
    private static final String TEST_ARCHIVE_WITH_MANIFEST_PATH = 
        "src/test/resources/archive-with-manifest.zip";
    private static final String TEST_ARCHIVE_WITH_VERSION_PATH = 
        "src/test/resources/archive-with-version-2.0.zip";
    // no hyphens in the name!
    private static final String TEST_ARCHIVE_WITHOUT_VERSION_PATH = 
        "src/test/resources/archive_without_version.zip";
    
    private PlainArchiveConverter converter = new PlainArchiveConverter((ProxiesInstance) null);
    private File result;
    
    @Test(expected = IllegalArgumentException.class)
    public void requiresSourceArchiveToExist() throws IOException {
        converter.convert("non-existent/path");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requiresTargetToNotExist() throws IOException {
        File existingTarget = File.createTempFile("existing", ".dar");
        existingTarget.deleteOnExit();
        converter.convert(TEST_ARCHIVE_PATH, existingTarget.getPath());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requiresSourceArchiveWithoutManifest() throws IOException {
        converter.convert(TEST_ARCHIVE_WITH_MANIFEST_PATH);
    }

    @Test
    public void addsManifest() throws IOException {
        result = converter.convert(TEST_ARCHIVE_PATH);
        assertTrue(format("Expected DAR '%s' to contain a manifest at '%s'", result, MANIFEST_PATH), 
            new TFile(result, MANIFEST_PATH).exists());
    }

    @Test
    public void canCreateDars() throws IOException {
        result = converter.convert(TEST_ARCHIVE_PATH, getTempFilePath("result", ".dar"));
        assertTrue(format("Expected DAR '%s' to contain a manifest at '%s'", result, MANIFEST_PATH), 
                new TFile(result, MANIFEST_PATH).exists());
    }
    
    @Test
    public void supportsSpecifiedTargetPath() throws IOException {
        result = converter.convert(TEST_ARCHIVE_PATH, 
                getTempFilePath("result", ".dar"));
        assertTrue(format("Expected DAR '%s' to contain a manifest at '%s'", result, MANIFEST_PATH), 
                new TFile(result, MANIFEST_PATH).exists());
    }
    
    @Test
    public void derivesNameAndVersionFromSourcePath() throws IOException {
        result = converter.convert(TEST_ARCHIVE_WITH_VERSION_PATH, 
                getTempFilePath("result", ".dar"));
        Attributes manifestAttributes = DarReader.getManifest(result).getMainAttributes();
        assertEquals("archive-with-version", manifestAttributes.getValue(APPLICATION_ATTRIBUTE_NAME));
        assertEquals("2.0", manifestAttributes.getValue(VERSION_ATTRIBUTE_NAME));
    }
    
    @Test
    public void usesDefaultVersionForUnversionedSourceName() throws IOException {
        result = converter.convert(TEST_ARCHIVE_WITHOUT_VERSION_PATH, 
                getTempFilePath("result", ".dar"));
        Attributes manifestAttributes = DarReader.getManifest(result).getMainAttributes();
        assertEquals("archive_without_version", manifestAttributes.getValue(APPLICATION_ATTRIBUTE_NAME));
        assertEquals(DEFAULT_APP_VERSION, manifestAttributes.getValue(VERSION_ATTRIBUTE_NAME));
    }
    
    @Test
    public void addsDarExtensionToTargetFileIfRequired() throws IOException {
        String resultPath = getTempFilePath("result", ".zip");
        result = converter.convert(TEST_ARCHIVE_PATH, resultPath);
        assertEquals(resultPath + "." + DAR_EXTENSION, result.getPath());
    }
    
    @Test
    public void preservesTargetNameWithDarExtension() throws IOException {
        String resultPath = getTempFilePath("result", ".dar");
        result = converter.convert(TEST_ARCHIVE_PATH, resultPath);
        assertEquals(resultPath, result.getPath());        
    }
    
    @After
    public void removeResult() {
        deleteOnExit(result);
    }
}
