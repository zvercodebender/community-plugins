/*
 * @(#)DarManifestBuilder.java     21 Jul 2011
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
package com.xebialabs.deployit.cli.ext.plainarchive.dar;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.xebialabs.deployit.cli.ext.plainarchive.collect.Maps2.transformKeys;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;


public class DarManifestBuilder extends ManifestBuilder {
    @VisibleForTesting
    public static final String APPLICATION_ATTRIBUTE_NAME = "Ci-Application";
    @VisibleForTesting
    public static final String VERSION_ATTRIBUTE_NAME = "Ci-Version";
    private static final String PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME = "Deployit-Package-Format-Version";
    private static final String PACKAGE_FORMAT_VERSION_NUMBER = "1.3";

    public DarManifestBuilder() {
        addMainAttribute(PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME, 
                PACKAGE_FORMAT_VERSION_NUMBER);
    }
    
    public DarManifestBuilder setApplication(@Nonnull String applicationName) {
        addMainAttribute(APPLICATION_ATTRIBUTE_NAME, checkNotNull(applicationName, "applicationName"));
        return this;
    }
    
    public DarManifestBuilder setVersion(@Nonnull String version) {
        addMainAttribute(VERSION_ATTRIBUTE_NAME, checkNotNull(version, "version"));
        return this;
    }
    
    public DarManifestBuilder addDarEntry(@Nonnull DarEntry entry) {
        entry.addToManifest(this);
        return this;
    }    
    
    public DarManifestBuilder addDarEntries(@Nonnull Iterable<DarEntry> entries) {
        for (DarEntry entry : entries) {
            addDarEntry(entry);
        }
        return this;
    }    
    
    public static class DarEntry {
        // null object to indicate "no result"
        public static final DarEntry NULL = new DarEntry(EMPTY, ImmutableMap.<String, String>of(), EMPTY);
        
        private static final String CI_ATTRIBUTE_PREFIX = "Ci-";
        private static final String TYPE_ATTRIBUTE_NAME = CI_ATTRIBUTE_PREFIX + "type";

        private final String type;
        private final Map<String, String> properties;
        private final String jarEntryPath;

        public DarEntry(@Nonnull String type, @Nonnull Map<String, String> properties, 
                @Nonnull String jarEntryPath) {
            this.type = checkNotNull(type, "type");
            this.properties = copyOf(checkNotNull(properties, "properties"));
            this.jarEntryPath = checkNotNull(jarEntryPath, "jarEntryPath");
        }
        
        private void addToManifest(@Nonnull ManifestBuilder builder) {
            // type and all the properties
            Map<String, String> attributes = Maps.newHashMapWithExpectedSize(
                    1 + properties.size());
            attributes.put(TYPE_ATTRIBUTE_NAME, type);
            attributes.putAll(transformKeys(properties, new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        return CI_ATTRIBUTE_PREFIX + input;
                    }
                }));
            builder.addEntryAttributes(jarEntryPath, attributes);
        }

        @Override
        public String toString() {
            return "DarEntry [type=" + type + ", properties=" + properties
                    + ", jarEntryPath=" + jarEntryPath + "]";
        }
    }   
}