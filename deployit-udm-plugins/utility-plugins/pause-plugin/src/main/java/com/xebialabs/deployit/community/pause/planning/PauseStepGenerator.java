package com.xebialabs.deployit.community.pause.planning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Predicate;

import com.xebialabs.deployit.community.pause.step.PauseStep;
import com.xebialabs.deployit.plugin.api.deployment.planning.Contributor;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.Deltas;
import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.udm.Environment;

import static com.google.common.collect.Iterables.all;

public class PauseStepGenerator {

	static final String PAUSABLE_PROPERTY = "pausable";
	static final String PAUSE_ORDER_PROPERTY = "pauseOrder";

	static final Predicate<Delta> NOOP_OPERATION = new Predicate<Delta>() {
		@Override
		public boolean apply(Delta input) {
			return input.getOperation() == Operation.NOOP;
		}
	};

	static final Predicate<Delta> DESTROY_OPERATION = new Predicate<Delta>() {
		@Override
		public boolean apply(Delta input) {
			return input.getOperation() == Operation.DESTROY;
		}
	};

	@Contributor
	public void contribute(Deltas deltas, DeploymentPlanningContext context) {
		if (all(deltas.getDeltas(), NOOP_OPERATION))
			return;

		if (all(deltas.getDeltas(), DESTROY_OPERATION))
			return;

		final Environment environment = context.getDeployedApplication().getEnvironment();
		if (environment.hasProperty(PAUSABLE_PROPERTY) && environment.<Boolean>getProperty(PAUSABLE_PROPERTY)) {
			final int order = environment.<Integer>getProperty(PAUSE_ORDER_PROPERTY);
			logger.debug("new PauseStep order {}", order);
			context.addStep(new PauseStep(order));
		}
	}

	protected static final Logger logger = LoggerFactory.getLogger(PauseStepGenerator.class);

}
