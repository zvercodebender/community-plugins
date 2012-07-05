/*
 * @(#)DeploymentGroups.java     Feb 8, 2012
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

import com.xebialabs.deployit.plugin.api.udm.Container;

public class DeploymentGroups {
    private static final int UNALLOCATED_CONTAINER_GROUP = Integer.MAX_VALUE;
    private static final String DEPLOYMENT_GROUP_PROPERTY = "deploymentGroup";

    public static int getDeploymentGroup(Container frontedServer) {
        Integer deploymentGroup = frontedServer.<Integer>getProperty(DEPLOYMENT_GROUP_PROPERTY);
        return (deploymentGroup != null) ? deploymentGroup.intValue()
                                         : UNALLOCATED_CONTAINER_GROUP;
    }
}
