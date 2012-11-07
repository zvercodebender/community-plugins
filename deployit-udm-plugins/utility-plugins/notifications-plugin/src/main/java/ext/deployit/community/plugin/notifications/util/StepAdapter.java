package ext.deployit.community.plugin.notifications.util;

import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentExecutionContext;
import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentStep;
import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;
import com.xebialabs.deployit.plugin.api.inspection.InspectionExecutionContext;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;

import java.util.Map;

public class StepAdapter implements Step {
	private com.xebialabs.deployit.plugin.api.execution.Step wrapped;

	public static Step wrapIfNeeded(Object s) {
		if (s instanceof Step) {
			return (Step) s;
		} else if (s instanceof com.xebialabs.deployit.plugin.api.execution.Step) {
			return new StepAdapter((com.xebialabs.deployit.plugin.api.execution.Step) s);
		} else throw new IllegalArgumentException("Argument is not a .flow.Step or deprecated Step, but: " + s);
	}

	private StepAdapter(com.xebialabs.deployit.plugin.api.execution.Step wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public int getOrder() {
		if (wrapped instanceof DeploymentStep) {
			return ((DeploymentStep) wrapped).getOrder();
		}
		return 50;
	}

	@Override
	public String getDescription() {
		return wrapped.getDescription();
	}

	@Override
	public StepExitCode execute(ExecutionContext ctx) throws Exception {
		com.xebialabs.deployit.plugin.api.execution.Step.Result execute = wrapped.execute(new ExecutionContextAdapter(ctx));
		switch (execute) {
		case Success:
		case Warn:
			return StepExitCode.SUCCESS;
		case Fail:
		default:
			return StepExitCode.FAIL;
		}
	}

	private static final class ExecutionContextAdapter implements DeploymentExecutionContext, InspectionExecutionContext {
		private ExecutionContext wrapped;

		private ExecutionContextAdapter(ExecutionContext wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public void logOutput(String output) {
			wrapped.logOutput(output);
		}

		@Override
		public void logError(String error) {
			wrapped.logError(error);
		}

		@Override
		public void logError(String error, Throwable t) {
			wrapped.logError(error, t);
		}

		@Override
		public Object getAttribute(String name) {
			return wrapped.getAttribute(name);
		}

		@Override
		public void setAttribute(String name, Object value) {
			wrapped.setAttribute(name, value);
		}

		@Override
		public void discovered(ConfigurationItem item) {
			wrapped.getInspectionContext().discovered(item);
		}

		@Override
		public Map<String, ConfigurationItem> getDiscovered() {
			return wrapped.getInspectionContext().getDiscovered();
		}

		@Override
		public void inspected(ConfigurationItem item) {
			wrapped.getInspectionContext().inspected(item);
		}

		@Override
		public Map<String, ConfigurationItem> getInspected() {
			return wrapped.getInspectionContext().getInspected();
		}
	}
}
