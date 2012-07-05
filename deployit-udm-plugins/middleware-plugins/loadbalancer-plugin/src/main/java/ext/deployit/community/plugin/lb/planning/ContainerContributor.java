/*
 * @(#)ContainerContributor.java     2 Oct 2011
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
package ext.deployit.community.plugin.lb.planning;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static ext.deployit.community.plugin.lb.util.Predicates2.operationIs;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.Deltas;
import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Deployed;

public abstract class ContainerContributor<T> {
    protected final Predicate<Object> isTargetContainerClass;
    protected Set<T> containers;
    
    protected ContainerContributor(Class<T> containerClass) {
        isTargetContainerClass = instanceOf(containerClass);
    }
    
    @SuppressWarnings("unchecked")
    protected void collectContainers(Deltas deltas) {
        Iterable<Delta> nonNoopDeltas = filter(deltas.getDeltas(), not(operationIs(Operation.NOOP)));
        containers = (Set<T>) ImmutableSet.copyOf(filter(transform(nonNoopDeltas, 
                new Function<Delta, Container>() {
                    @Override
                    public Container apply(Delta input) {
                        return getDeployed(input).getContainer();
                    }
                    
                    private Deployed<?, ?> getDeployed(Delta delta) {
                        return (delta.getOperation().equals(Operation.DESTROY) 
                                ? delta.getPrevious()
                                : delta.getDeployed());
                    }
                }), 
                isTargetContainerClass));
    }
}
