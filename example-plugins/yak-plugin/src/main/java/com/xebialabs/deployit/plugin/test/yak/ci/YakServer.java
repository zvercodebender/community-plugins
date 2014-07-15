package com.xebialabs.deployit.plugin.test.yak.ci;

import com.xebialabs.deployit.plugin.api.deployment.planning.Contributor;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.Deltas;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.api.udm.Property;
import com.xebialabs.deployit.plugin.api.udm.base.BaseContainer;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.deployit.plugin.test.yak.step.StartYakServerStep;
import com.xebialabs.deployit.plugin.test.yak.step.StopYakServerStep;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
* This is our base container, where YakFiles will be deployed. As it is a container you
* it has a host property which is used to describe the parent, which will be an overthere
* host in this case.
*
* The Metadata annotation describes where a YakServer can sit in the repo, here we indicate
* that this can only site under other CIs
*/
@Metadata(root = Metadata.ConfigurationItemRoot.NESTED)
public class YakServer extends BaseContainer {

    // This annotation and member var provide for the YakServer to be a child
    // of a host in the XLD repository.
    @Property(asContainment = true, description = "Host upon which the container resides")
    private Host host;

    /*
    * The restartYakServers() method annotated with @Contributor is invoked when any deployment 
    * takes place (also deployments that may not necessarily contain an instance of the YakServer 
    * class). The method serversRequiringRestart() searches for any YakServer instances that are
    * present in the deployment and that requires a restart. For each of these YakServer instances, 
    * a StartYakServerStep and StopYakServerStep is added to the plan.
    */
    @Contributor
    public static void restartYakServers(Deltas deltas, DeploymentPlanningContext result) {
        for (YakServer yakServer : serversRequiringRestart(deltas.getDeltas())) {
            result.addStep(new StopYakServerStep(yakServer));
            result.addStep(new StartYakServerStep(yakServer));
        }
    }

    private static Set<YakServer> serversRequiringRestart(List<Delta> operations) {
        Set<YakServer> servers = new TreeSet<>();
        for (Delta operation : operations) {
            if (operation.getDeployed() instanceof RestartRequiringDeployedYakFile && operation.getDeployed().getContainer() instanceof YakServer) {
                servers.add((YakServer) operation.getDeployed().getContainer());
            }
        }
        return servers;
    }
}
