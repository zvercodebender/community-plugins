package ext.deployit.community.plugin.precheck.planning;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.generic.step.ScriptExecutionStep;
import com.xebialabs.deployit.plugin.overthere.HostContainer;

import ext.deployit.community.plugin.precheck.util.Strings;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.xebialabs.deployit.plugin.api.deployment.specification.Operation.DESTROY;


public class PrecheckStepsGenerator {

	protected static final String CHECK_SCRIPT_PROPERTY = "checkScript";

	static final Predicate<Container> IS_PRECHECKED_CONTAINER = new Predicate<Container>() {
		@Override
		public boolean apply(Container ci) {
			return ci != null && ci.hasProperty(CHECK_SCRIPT_PROPERTY) &&
					Strings.isNotEmpty(ci.<String>getProperty(CHECK_SCRIPT_PROPERTY));
		}
	};
	static final Function<Delta, Container> TO_CONTAINER = new Function<Delta, Container>() {
		@Override
		public Container apply(Delta input) {
			return (input.getOperation().equals(DESTROY)
					? null
					: input.getDeployed().getContainer());
		}
	};

	@PrePlanProcessor
	public static List<Step> generate(DeltaSpecification specification) {
		Map<String, Object> freeMarkerContext = Maps.newHashMap();
		freeMarkerContext.put("deployedApplication", specification.getDeployedApplication());

		Iterable<Container> preCheckedContainers = Sets.newHashSet(filter(
				transform(specification.getDeltas(), TO_CONTAINER),
				IS_PRECHECKED_CONTAINER));

		ImmutableList.Builder<Step> steps = ImmutableList.builder();
		for (Container container : preCheckedContainers) {
			freeMarkerContext.put("container", container);
			if (container instanceof HostContainer) {
				final String scriptTemplate = container.getProperty(CHECK_SCRIPT_PROPERTY);
				logger.info("add script execution step {} for  {}", scriptTemplate, container);
				HostContainer hostContainer = (HostContainer) container;
				steps.add(new ScriptExecutionStep(1, scriptTemplate, hostContainer, freeMarkerContext, "Pre-Check on " + container.getId()));
			} else {
				logger.warn("the non HostContainer {} is not supported yet !", container);
			}
		}
		return steps.build();
	}

	protected static final Logger logger = LoggerFactory.getLogger(PrecheckStepsGenerator.class);

}
