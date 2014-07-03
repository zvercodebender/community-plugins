package com.xebialabs.deployit.plugin.test.yak.step;

import com.xebialabs.deployit.plugin.test.yak.ci.DeployedYakFile;
import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;

/**
 * The steps in this package are all similar in that they
 * do not to a lot of work, but are here to illustrate step
 * classes.
 */
@SuppressWarnings("serial")
public class StopDeployedYakFileStep implements Step {

    private final DeployedYakFile yakFile;

    public StopDeployedYakFileStep(DeployedYakFile yakFile) {
        this.yakFile = yakFile;
    }

    @Override
    public String getDescription() {
        return "Stoping: " + yakFile;
    }

    @Override
    public StepExitCode execute(ExecutionContext ctx) throws Exception {
        return StepExitCode.SUCCESS;
    }

    public DeployedYakFile getYakFile() {
        return yakFile;
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
