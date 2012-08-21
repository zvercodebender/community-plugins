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
