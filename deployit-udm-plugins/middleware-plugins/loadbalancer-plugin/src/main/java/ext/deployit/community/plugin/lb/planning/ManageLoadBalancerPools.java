package ext.deployit.community.plugin.lb.planning;

import static ext.deployit.community.plugin.lb.util.DeploymentGroups.getDeploymentGroup;
import static ext.deployit.community.plugin.lb.util.Environments.getMembersOfType;
import static ext.deployit.community.plugin.lb.util.LoadBalancedContainers.getContainerLoadBalancers;
import static ext.deployit.community.plugin.lb.util.LoadBalancedContainers.getLoadBalancingBounds;
import static java.lang.String.format;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.plugin.api.deployment.planning.Contributor;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Deltas;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import com.xebialabs.deployit.plugin.generic.ci.GenericContainer;
import com.xebialabs.deployit.plugin.generic.step.ScriptExecutionStep;

import ext.deployit.community.plugin.lb.util.LoadBalancedContainers.LoadBalancingBounds;

public class ManageLoadBalancerPools extends ContainerContributor<Container> {
    private static final Type LOADBALANCER_TYPE = Type.valueOf("lb.GenericLoadBalancer");

    private static final String LOADBALANCER_REMOVE_FROM_POOL_SCRIPT_PROPERTY = "removeFromLoadBalancerPoolScript";
    private static final String LOADBALANCER_REMOVE_FROM_POOL_ORDER_PROPERTY = "removeFromLoadBalancerPoolOrder";
    private static final String LOADBALANCER_RETURN_TO_POOL_SCRIPT_PROPERTY = "returnToLoadBalancerPoolScript";
    private static final String LOADBALANCER_RETURN_TO_POOL_ORDER_PROPERTY = "returnToLoadBalancerPoolOrder";

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageLoadBalancerPools.class);

    public ManageLoadBalancerPools() {
        super(Container.class);
    }

    @Contributor
    public void manageContainersInPool(Deltas deltas, DeploymentPlanningContext ctx) {
        collectContainers(deltas);

        Environment targetEnvironment = ctx.getDeployedApplication().getEnvironment();
        Set<GenericContainer> loadBalancers = getMembersOfType(targetEnvironment, LOADBALANCER_TYPE);

        if (loadBalancers.isEmpty()) {
            LOGGER.debug("No load balancers in environment. Nothing to do.");
            return;
        }

        // container -> load balancers managing that server
        Map<Container, List<GenericContainer>> containerLoadBalancers = getContainerLoadBalancers(loadBalancers);

        /*
         * container -> first/last deployment groups of the fronted servers
         * 
         * Deployment groups are identified by the 'deploymentGroup' property which is sorted numerically. So if the
         * deployment groups are
         * 
         * - 3 - 6 - 2
         * 
         * the first group is 2 and the last group is 6. Containers without tags are in the special Integer.MAX_VALUE
         * group that is processed last.
         */
        Map<Container, LoadBalancingBounds> containerBalancingBounds =
            getLoadBalancingBounds(containerLoadBalancers.keySet());

        /*
         * The deployment group that the contributor is currently processing. This can be found by looking at the deltas
         * currently being processed - the group-based orchestrator will only include a delta in the current set if its
         * container is in the current deployment group.
         * 
         * So by looking at the deployment groups of the containers being processed we can find the current group (if
         * the containers have no group, we are in the 'unallocated' group).
         */
        Integer currentDeploymentGroup = getCurrentDeploymentGroup(containers);

        if (currentDeploymentGroup == null) {
            format("Unable to determine deployment group for containers: %s. Will not add steps.", containers);
            return;
        }

        for (Entry<Container, LoadBalancingBounds> balancingBounds : containerBalancingBounds.entrySet()) {
            Container container = balancingBounds.getKey();
            LoadBalancingBounds bounds = balancingBounds.getValue();
            if (bounds.startGroup == currentDeploymentGroup) {
                LOGGER.debug("Adding 'remove from pool' steps for container with start group '{}' (current group: '{}'",
                    bounds.startGroup, currentDeploymentGroup);
                addRemoveFromPoolSteps(ctx, container, containerLoadBalancers.get(container));
            }
            if (bounds.endGroup == currentDeploymentGroup) {
                LOGGER.debug("Adding 'return to pool' steps for container with end group '{}' (current group: '{}'",
                    bounds.endGroup, currentDeploymentGroup);
                addReturnToPoolSteps(ctx, container, containerLoadBalancers.get(container));
            }
        }
    }

    private Integer getCurrentDeploymentGroup(Set<Container> containers) {
        // expecting at least one target container
        for (Container container : containers) {
            return getDeploymentGroup(container);
        }
        return null;
    }

    private static void addRemoveFromPoolSteps(DeploymentPlanningContext ctx, Container container, List<GenericContainer> loadBalancers) {
        for (GenericContainer loadBalancer : loadBalancers) {
            ctx.addStep(new ScriptExecutionStep(loadBalancer.<Integer> getProperty(LOADBALANCER_REMOVE_FROM_POOL_ORDER_PROPERTY),
                loadBalancer.<String> getProperty(LOADBALANCER_REMOVE_FROM_POOL_SCRIPT_PROPERTY),
                loadBalancer, getFreeMarkerContext(container, loadBalancer),
                format("Remove %s from load balancer pool of %s", container, loadBalancer)));
        }
    }

    private static void addReturnToPoolSteps(DeploymentPlanningContext ctx, Container container, List<GenericContainer> loadBalancers) {
        for (GenericContainer loadBalancer : loadBalancers) {
            ctx.addStep(new ScriptExecutionStep(loadBalancer.<Integer> getProperty(LOADBALANCER_RETURN_TO_POOL_ORDER_PROPERTY),
                loadBalancer.<String> getProperty(LOADBALANCER_RETURN_TO_POOL_SCRIPT_PROPERTY),
                loadBalancer, getFreeMarkerContext(container, loadBalancer),
                format("Return %s to load balancer pool of %s", container, loadBalancer)));
        }
    }

    private static Map<String, Object> getFreeMarkerContext(
        Container container, Container loadBalancer) {
        return ImmutableMap.<String, Object> of("container", loadBalancer,
            "poolmember", container);
    }
}