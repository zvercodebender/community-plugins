/*
 * @(#)Ears.java     19 Oct 2011
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
package com.xebialabs.deployit.server.api.importer.zip.io;

import static com.xebialabs.deployit.server.api.importer.zip.ZipImporter.ZIP_EXTENSION;
import static java.lang.String.format;

import java.io.File;

import javax.annotation.Nonnull;

public class Zips {
    // must be lowercase
    private static final String ZIP_FILENAME_SUFFIX = format(".%s", ZIP_EXTENSION);
    
    public static boolean isZip(@Nonnull File file) {
        return isZip(file.getName());
    }
    
    public static boolean isZip(@Nonnull String filename) {
        return filename.toLowerCase().endsWith(ZIP_FILENAME_SUFFIX);
    }
}
