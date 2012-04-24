/*
 * @(#)ConfigPrefixStripper.java     22 Oct 2011
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
package com.xebialabs.deployit.server.api.importer.singlefile.config;

import static com.google.common.base.Functions.forMap;
import static com.google.common.collect.Maps.transformValues;

import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class PrefixStripper implements Function<Map<String, String>, Map<String, String>> {
    // must be a regex
    private final String prefixToStripRegex;
    
    public PrefixStripper(String prefixToStrip) {
        prefixToStripRegex = Pattern.quote(prefixToStrip);
    }

    @Override
    public Map<String, String> apply(Map<String, String> input) {
        return Maps2.transformKeys(input, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.replaceFirst(prefixToStripRegex, "");
            }
        });
    }
    
    private static class Maps2 {
        private static @Nonnull <K1, K2, V> Map<K2, V> transformKeys(@Nonnull Map<K1, V> fromMap,
                Function<? super K1, K2> function) {
            // will catch dups
            Map<K2, K1> newKeys = Maps.uniqueIndex(fromMap.keySet(), function);
            // replace each old key with its value using a lookup in the input map
            return transformValues(newKeys, forMap(fromMap));
        }
    }
}
