/*
 * @(#)UriQueryString.java     4 Aug 2011
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
package com.xebialabs.deployit.cli.ext.plainarchive.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @author aphillips
 * @since 4 Aug 2011
 *
 */
public class UriQueryStrings {
    private static final char ATTRIBUTES_SEPARATOR = '&';
    // used in regex
    private static final String ATTRIBUTE_KEY_VALUE_SEPARATOR = Pattern.quote("=");
    
    public static @Nonnull Map<String, String> toMap(@Nonnull String queryString) {
        final Builder<String, String> inProgress = ImmutableMap.builder();
        for (String keyValue : Splitter.on(ATTRIBUTES_SEPARATOR).split(queryString)) {
            String[] keyAndValue = keyValue.split(ATTRIBUTE_KEY_VALUE_SEPARATOR);
            checkArgument(keyAndValue.length == 2, "Invalid query string format. Expected 'key=value&key2=value&...' but found section '%s'", 
                    keyValue);
            inProgress.put(keyAndValue[0], keyAndValue[1]);
        }
        return inProgress.build();
    }
}
