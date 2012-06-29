package com.xebialabs.deployit.community.openshift.deployed;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.generic.deployed.ExecutedScriptWithDerivedArtifact;
import com.xebialabs.deployit.plugin.jee.artifact.War;

@SuppressWarnings("serial")
@Metadata(virtual = true)
public class DeployedWar0 extends ExecutedScriptWithDerivedArtifact<War> {
    // also used as a semaphore...bit nasty
    protected Map<String, Object> freeMarkerContextWithPrevious;

    @Override
    public void executeModify(DeploymentPlanningContext ctx, Delta d) {
        freeMarkerContextWithPrevious = ImmutableMap.<String, Object>of("previous", 
                d.getPrevious(), "deployed", d.getDeployed());
        super.executeModify(ctx, d);
        freeMarkerContextWithPrevious = null;
    }

    @Override
    public Map<String, Object> getDeployedAsFreeMarkerContext() {
        return ((freeMarkerContextWithPrevious != null) 
                ? freeMarkerContextWithPrevious
                : super.getDeployedAsFreeMarkerContext());
    }

}
