package com.xebialabs.deployit.plugin.test.yak.step;

import com.xebialabs.deployit.plugin.test.yak.ci.DeployedYakFile;
import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;
import com.xebialabs.deployit.plugin.test.yak.ci.RestartRequiringDeployedYakFile;

/**
 * The steps in this package are all similar in that they
 * do not to a lot of work, but are here to illustrate step
 * classes.
 */
@SuppressWarnings("serial")
public class UpgradeYakFileOnServerStep implements Step {

    public UpgradeYakFileOnServerStep() {
    }

    public UpgradeYakFileOnServerStep(DeployedYakFile deployedYakFile) {
    }

    public UpgradeYakFileOnServerStep(RestartRequiringDeployedYakFile restartRequiringDeployedYakFile) {
    }

    @Override
    public String getDescription() {
        return "Upgrade";
    }

    @Override
    public StepExitCode execute(ExecutionContext ctx) throws Exception {
        return StepExitCode.SUCCESS;
    }

    @Override
    public int getOrder() {
        return 70;
    }
}
