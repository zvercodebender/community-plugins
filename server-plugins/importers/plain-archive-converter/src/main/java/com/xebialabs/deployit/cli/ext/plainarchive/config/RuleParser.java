/*
 * @(#)RuleParser.java     22 Jul 2011
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
package com.xebialabs.deployit.cli.ext.plainarchive.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.xebialabs.deployit.cli.ext.plainarchive.matcher.ConfigurationItemMatcher;
import com.xebialabs.deployit.cli.ext.plainarchive.matcher.ConfigurationItemMatcher.MatcherFactory;

/**
 * @author aphillips
 * @since 22 Jul 2011
 *
 */
public class RuleParser implements Function<Map<String, String>, ConfigurationItemMatcher>{
    private static final String TYPE_PROPERTY = "type";
    
    private final Map<String, MatcherFactory> matcherFactories;
    
    public RuleParser(Collection<MatcherFactory> matcherFactories) {
        this.matcherFactories = Maps.uniqueIndex(matcherFactories, 
                new Function<MatcherFactory, String>() {
                    @Override
                    public String apply(MatcherFactory input) {
                        return input.getMatcherType();
                    }
                });
    }

    @Override
    public @Nonnull ConfigurationItemMatcher apply(@Nonnull Map<String, String> config) {
        String matcherType = config.get(TYPE_PROPERTY);
        checkArgument(matcherType != null, "config property '%s' is required", TYPE_PROPERTY);
        MatcherFactory matcherFactory = matcherFactories.get(matcherType);
        checkArgument(matcherFactory != null, "unknown rule type '%s'", matcherType);
        return matcherFactory.from(config);
    }

}
