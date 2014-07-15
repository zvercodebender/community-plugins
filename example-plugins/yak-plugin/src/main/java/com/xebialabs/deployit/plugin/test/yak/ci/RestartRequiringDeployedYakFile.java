package com.xebialabs.deployit.plugin.test.yak.ci;

import com.xebialabs.deployit.plugin.api.deployment.planning.Create;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.planning.Destroy;
import com.xebialabs.deployit.plugin.api.deployment.planning.Modify;
import com.xebialabs.deployit.plugin.api.udm.base.BaseDeployed;
import com.xebialabs.deployit.plugin.test.yak.step.DeleteYakFileFromServerStep;
import com.xebialabs.deployit.plugin.test.yak.step.DeployYakFileToServerStep;
import com.xebialabs.deployit.plugin.test.yak.step.UpgradeYakFileOnServerStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class RestartRequiringDeployedYakFile extends BaseDeployed<RestartRequiringYakFile, YakServer> {

    private static final Logger logger = LoggerFactory.getLogger(RestartRequiringDeployedYakFile.class);

    @Create
    public void deploy(DeploymentPlanningContext result) {
        logger.info("Adding step");
        result.addStep(new DeployYakFileToServerStep(this));
    }

    @Modify
    public void upgrade(DeploymentPlanningContext result) {
        logger.info("Adding upgrade step");
        result.addStep(new UpgradeYakFileOnServerStep(this));
    }

    @Destroy
    public void destroy(DeploymentPlanningContext result) {
        logger.info("Adding undeploy step");
        result.addStep(new DeleteYakFileFromServerStep(this));
    }

}
