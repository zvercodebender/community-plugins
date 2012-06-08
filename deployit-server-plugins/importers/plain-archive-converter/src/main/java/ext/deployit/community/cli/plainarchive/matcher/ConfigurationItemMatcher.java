/*
 * @(#)CiMatcher.java     22 Jul 2011
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
package ext.deployit.community.cli.plainarchive.matcher;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import de.schlichtherle.truezip.file.TFile;
import ext.deployit.community.cli.plainarchive.base.UriQueryStrings;
import ext.deployit.community.cli.plainarchive.dar.DarManifestBuilder.DarEntry;
import ext.deployit.community.cli.plainarchive.matcher.ConfigurationItemMatcher.MatchResult;

public abstract class ConfigurationItemMatcher implements Function<TFile, MatchResult>{
    private static final String RETURNED_TYPE_PROPERTY = "returned.ci";
    private static final String CI_PROPERTIES_PROPERTY = "ci.properties";
    
    protected final @Nonnull String returnedType;
    protected final @Nullable String ciProperties;
    
    protected ConfigurationItemMatcher(@Nonnull Map<String, String> config) {
        validate(config);
        returnedType = config.get(RETURNED_TYPE_PROPERTY);
        
        // will be null if not present
        ciProperties = config.get(CI_PROPERTIES_PROPERTY);
    }

    protected void validate(@Nonnull Map<String, String> config) {
        checkArgument(config.containsKey(RETURNED_TYPE_PROPERTY), 
                "config property '%s' is required", RETURNED_TYPE_PROPERTY);
    }
    
    @Override
    public @Nonnull MatchResult apply(@Nonnull TFile input) {
        boolean matched = matches(input);
        DarEntry entry = matched 
                         ? new DarEntry(returnedType, getCiProperties(), input.getEnclEntryName())
                         : null;
        return new MatchResult(matched, entry); 
    }
    
    protected abstract boolean matches(@Nonnull TFile input);
    
    protected @Nonnull Map<String, String> getCiProperties() {
        // syntax is propName=value&propName2=value&..
        return ((ciProperties != null) ? UriQueryStrings.toMap(ciProperties) 
                                       : ImmutableMap.<String, String>of());
    }
    
    public static class MatchResult {
        public final boolean matched;
        @Nullable public final DarEntry result; 

        protected MatchResult(boolean matched, @Nullable DarEntry result) {
            this.matched = matched;
            this.result = result;
        }
    }
    
    public static interface MatcherFactory {
        @Nonnull String getMatcherType();
        @Nonnull ConfigurationItemMatcher from(@Nonnull Map<String, String> config);
    }
}
