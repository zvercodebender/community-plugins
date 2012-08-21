package ext.deployit.community.plugin.lb.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.generic.ci.GenericContainer;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static ext.deployit.community.plugin.lb.util.DeploymentGroups.getDeploymentGroup;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.sort;

public class LoadBalancedContainers {
    private static final String WEBSERVER_FRONTED_CONTAINERS_PROPERTY = "frontedServers";
    private static final String CONTAINER_REMOVE_FROM_POOL_PROPERTY = "removeFromLoadBalancerPool";
    private static final String LOADBALANCER_WEBSERVER_POOL_PROPERTY = "webserverPool";
    private static final String LOADBALANCER_APPSERVER_POOL_PROPERTY = "appserverPool";

    public static Map<Container, List<GenericContainer>> getContainerLoadBalancers(Set<GenericContainer> loadBalancers) {
        Map<Container, List<GenericContainer>> loadBalancedContainers = newHashMap();

        for (GenericContainer loadBalancer : loadBalancers) {
            for (Container webserver : loadBalancer.<Set<Container>> getProperty(LOADBALANCER_WEBSERVER_POOL_PROPERTY)) {
                if (TRUE.equals(webserver.getProperty(CONTAINER_REMOVE_FROM_POOL_PROPERTY))) {
                    addContainer(loadBalancedContainers, webserver, loadBalancer);
                }
            }
            for (Container appserver : loadBalancer.<Set<Container>> getProperty(LOADBALANCER_APPSERVER_POOL_PROPERTY)) {
                // may be null if it's not a GenericContainer
                if (TRUE.equals(appserver.getProperty(CONTAINER_REMOVE_FROM_POOL_PROPERTY))) {
                    addContainer(loadBalancedContainers, (Container) appserver, loadBalancer);
                }
            }
        }
        return loadBalancedContainers;
    }

    private static void addContainer(Map<Container, List<GenericContainer>> loadBalancedContainers, Container container, GenericContainer loadBalancer) {
        if (!loadBalancedContainers.containsKey(container)) {
            loadBalancedContainers.put(container, Lists.<GenericContainer> newArrayList());
        }
        loadBalancedContainers.get(container).add(loadBalancer);
    }

    public static Map<Container, LoadBalancingBounds> getLoadBalancingBounds(Set<Container> containers) {
        Builder<Container, LoadBalancingBounds> loadBalancingBounds = ImmutableMap.builder();
        for (Container container : containers) {
            loadBalancingBounds.put(container, new LoadBalancingBounds(container));
        }
        return loadBalancingBounds.build();
    }

    public static class LoadBalancingBounds {
        private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancingBounds.class);

        public final int startGroup;
        public final int endGroup;

        private LoadBalancingBounds(Container container) {
            /*
             * For webservers, the load balancing bounds are determined as follows:
             * 
             * 1) Get the deployment group for each fronted server, putting servers without a group in the 'unallocated'
             * group 2) Sort all the groups found 3) The first group is the one before which the webserver needs to be
             * taken out of the load balancer pool; it can be returned after the last group has been processed
             */
            if (container.hasProperty(WEBSERVER_FRONTED_CONTAINERS_PROPERTY)) {
                List<Integer> deploymentGroups = newArrayList();
                Set<com.xebialabs.deployit.plugin.api.udm.Container> frontedServers = container
                    .<Set<com.xebialabs.deployit.plugin.api.udm.Container>> getProperty(WEBSERVER_FRONTED_CONTAINERS_PROPERTY);
                for (com.xebialabs.deployit.plugin.api.udm.Container frontedServer : frontedServers) {
                    deploymentGroups.add(getDeploymentGroup(frontedServer));
                }
                sort(deploymentGroups);
                startGroup = deploymentGroups.get(0);
                endGroup = deploymentGroups.get(deploymentGroups.size() - 1);
            } else {
                /*
                 * For appservers, the load balancing bounds are just the deployment group of the server.
                 */
                startGroup = getDeploymentGroup(container);
                endGroup = startGroup;
            }
            LOGGER.debug("Determined load balancing bounds for '{}': start '{}', end '{}'",
                new Object[] { container, startGroup, endGroup });
        }
    }
}