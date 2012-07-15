package ext.deployit.community.plugin.pause.step;

import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentExecutionContext;
import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentStep;

public class PauseStep implements DeploymentStep {

	private final int order;
	private boolean executedOnce = false;

	public PauseStep(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public String getDescription() {
		return "Pause the plan";
	}

	@Override
	public Result execute(DeploymentExecutionContext ctx) throws Exception {
		if (!executedOnce) {
			executedOnce = true;
			return Result.Fail;
		}
		return Result.Success;
	}
}
