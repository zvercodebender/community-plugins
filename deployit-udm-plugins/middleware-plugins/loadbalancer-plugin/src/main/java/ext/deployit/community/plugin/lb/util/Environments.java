/*
 * @(#)Environments.java     Feb 8, 2012
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
package ext.deployit.community.plugin.lb.util;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;
import static ext.deployit.community.plugin.lb.util.Predicates2.instanceOf;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Environment;

public class Environments {
    
    public static <T> Set<T> getMembersOfType(Environment environment, Type type) {
        return copyOf(transform(Sets.filter(environment.getMembers(), instanceOf(type)),
                new Function<com.xebialabs.deployit.plugin.api.udm.Container, T>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public T apply(com.xebialabs.deployit.plugin.api.udm.Container input) {
                        return (T) input;
                    }
                })
            );
    }
}
