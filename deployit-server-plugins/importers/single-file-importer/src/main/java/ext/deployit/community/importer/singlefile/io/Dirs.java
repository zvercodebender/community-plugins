/*
 * @(#)Dirs.java     Apr 12, 2012
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
package ext.deployit.community.importer.singlefile.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import com.google.common.collect.Lists;

public class Dirs {

    public static List<String> listRecursively(File directory, FilenameFilter filter) {
        return listRecursively(directory, Lists.<String>newArrayList(), "", filter);
    }

    private static List<String> listRecursively(File directory, List<String> partialResult, 
            String pathPrefix, FilenameFilter filter) {
        File[] entries = directory.listFiles();

        for (File entry : entries) {
            if (entry.isFile() && filter.accept(directory, entry.getName())) {
                partialResult.add(pathPrefix + entry.getName());
            } else if (entry.isDirectory()) {
                listRecursively(entry, partialResult, pathPrefix + entry.getName() + "/", filter);
            }
        }
        return partialResult;
    }
}
