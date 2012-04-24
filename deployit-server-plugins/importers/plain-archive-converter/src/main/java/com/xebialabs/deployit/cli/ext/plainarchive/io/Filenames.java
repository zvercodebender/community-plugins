/*
 * @(#)Filenames.java     1 Aug 2011
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
package com.xebialabs.deployit.cli.ext.plainarchive.io;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;

import javax.annotation.Nonnull;

/**
 * @author aphillips
 * @since 1 Aug 2011
 *
 */
public class Filenames {

    public static class VersionedFilename {
        private static final String NAME_VERSION_SEPARATOR = "-";
        
        public final String name;
        public final String version;
        
        private VersionedFilename(String name, String version) {
            this.name = name;
            this.version = version;
        }
        
        // uses the part after the last '-' for the version, if present, else the default 
        public static @Nonnull VersionedFilename from(@Nonnull String filename, 
                @Nonnull String defaultVersion) {
            String version = substringAfterLast(filename, NAME_VERSION_SEPARATOR);
            if (isNotEmpty(version)) {
                return new VersionedFilename(
                        substringBeforeLast(filename, NAME_VERSION_SEPARATOR), version);
            } else {
                return new VersionedFilename(filename, defaultVersion);
            }
        }
    }
}
