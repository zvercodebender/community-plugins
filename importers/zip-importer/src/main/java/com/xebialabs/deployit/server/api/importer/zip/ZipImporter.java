/*
 * @(#)ZipImporter.java     20 Oct 2011
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.xebialabs.deployit.server.api.importer.util.UrlSources.getLocationAsUri;
import static com.xebialabs.deployit.server.api.importer.zip.io.Dirs.listRecursively;
import static com.xebialabs.deployit.server.api.importer.zip.io.Zips.isZip;
import static java.lang.String.format;
import static java.util.Collections.sort;
import static org.apache.commons.io.FilenameUtils.getBaseName;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.xebialabs.deployit.cli.ext.plainarchive.PlainArchiveConverter;
import com.xebialabs.deployit.cli.ext.plainarchive.io.Filenames.VersionedFilename;
import com.xebialabs.deployit.exception.RuntimeIOException;
import com.xebialabs.deployit.server.api.importer.ImportSource;
import com.xebialabs.deployit.server.api.importer.ImportedPackage;
import com.xebialabs.deployit.server.api.importer.ImportingContext;
import com.xebialabs.deployit.server.api.importer.PackageInfo;
import com.xebialabs.deployit.server.api.importer.zip.config.ConfigParser;
import com.xebialabs.deployit.server.api.importer.zip.config.PrefixStripper;
import com.xebialabs.deployit.service.importer.ManifestBasedDarImporter;
import com.xebialabs.deployit.service.importer.source.FileSource;
import com.xebialabs.deployit.service.importer.source.UrlSource;

// this is terrible, of course...extending a class that's not part of the public API
public class ZipImporter extends ManifestBasedDarImporter {
    public static final String ZIP_EXTENSION = "zip";
    
    private static final PlainArchiveConverter CONVERTER = new PlainArchiveConverter(null);
    // make configurable?
    private static final String DEFAULT_APP_VERSION = "1.0";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipImporter.class);

    private static final String CONFIG_FILE_NAME = "zip-importer.properties";
    private static final String CONFIG_PROPERTY_PREFIX = "zip-importer.";
    private static final Map<String, String> CONFIG;

    static {
        Properties configProperties = new Properties();
        try {
            configProperties.load(checkNotNull(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(CONFIG_FILE_NAME), CONFIG_FILE_NAME));
        } catch (Exception exception) {
            LOGGER.error(format("Unable to load configuration file '%s' from classpath", 
                    CONFIG_FILE_NAME), exception);
        }
        CONFIG = new PrefixStripper(CONFIG_PROPERTY_PREFIX)
                 .apply(Maps.fromProperties(configProperties));
    }

    private final boolean scanSubdirectories;
    
    public ZipImporter() {
        this(new ConfigParser(CONFIG)); 
    }
    
    private ZipImporter(ConfigParser configParser) {
        this(configParser.subdirectoryScanningEnabled);
    }

    @VisibleForTesting
    ZipImporter(boolean scanSubdirectories) {
        this.scanSubdirectories = scanSubdirectories;
    }

    @Override
    public List<String> list(File directory) {
        FilenameFilter zipFileFilter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return isZip(name);
                    }
                };
        List<String> zipFiles = 
            scanSubdirectories ? listRecursively(directory, zipFileFilter)
                               : newArrayList(directory.list(zipFileFilter));
        sort(zipFiles);
        LOGGER.debug("Found ZIP files in package directory: {}", zipFiles);
        return zipFiles;
    }

    @Override
    public boolean canHandle(ImportSource source) {
        return isZip(source.getFile());
    }

    @Override
    public PackageInfo preparePackage(ImportSource source, ImportingContext ctx) {
        VersionedFilename nameAndVersion = getNameAndVersion(source);
        try {
            File temporaryDar = CONVERTER.convert(source.getFile().getPath(), 
                    nameAndVersion.name, nameAndVersion.version);
            return super.preparePackage(new FileSource(temporaryDar, true), ctx);
        } catch (IOException exception) {
            throw new RuntimeIOException(format("Unable to import ZIP '%s' due to: %s", source, exception),
                    exception);
        }
    }
    
    @VisibleForTesting
    protected VersionedFilename getNameAndVersion(ImportSource source) {
        String sourceFilename = getBaseName((source instanceof UrlSource)
                 ? getLocationAsUri((UrlSource) source).toString()
                 : source.getFile().getName());
        return VersionedFilename.from(sourceFilename, DEFAULT_APP_VERSION);
    }

    @Override
    public ImportedPackage importEntities(PackageInfo packageInfo, ImportingContext ctx) {
        LOGGER.debug("Delegating to default DAR importer");
        return super.importEntities(packageInfo, ctx);
    }

    @Override
    public void cleanUp(PackageInfo packageInfo, ImportingContext ctx) {
        ImportSource temporaryDar = packageInfo.getSource();
        LOGGER.debug("Attempting to clean up temporary DAR for source '{}'", temporaryDar);
        temporaryDar.cleanUp();
    }
}