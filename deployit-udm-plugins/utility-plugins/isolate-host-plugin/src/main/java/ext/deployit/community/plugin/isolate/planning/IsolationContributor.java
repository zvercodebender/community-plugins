package ext.deployit.community.plugin.isolate.planning;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import com.xebialabs.deployit.plugin.api.deployment.planning.Contributor;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.Deltas;
import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.generic.step.ScriptExecutionStep;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.deployit.plugin.overthere.HostContainer;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.filter;
import static java.lang.String.format;

public class IsolationContributor {

    @Contributor
    public void addBeforeSteps(Deltas deltas, DeploymentPlanningContext ctx) {
        ctx.addSteps(
                transform(gatherTargets(deltas.getDeltas()), new Function<Host, Step>() {
                    @Override
                    public Step apply(final Host host) {
                        return new ScriptExecutionStep(host.<Integer>getProperty("beforeOrder"), host.<String>getProperty("beforeScript"), host, getFreeMarkerContext(host), format(host.<String>getProperty("beforeDescription"), host));
                    }
                }));
    }


    @Contributor
    public void addAfterSteps(Deltas deltas, DeploymentPlanningContext ctx) {
        ctx.addSteps(
                transform(gatherTargets(deltas.getDeltas()), new Function<Host, Step>() {
                    @Override
                    public Step apply(final Host host) {
                        return new ScriptExecutionStep(host.<Integer>getProperty("afterOrder"), host.<String>getProperty("afterScript"), host, getFreeMarkerContext(host), format(host.<String>getProperty("afterDescription"), host));
                    }
                }));
    }

    private Set<Host> gatherTargets(List<Delta> operations) {
        final Set<Host> targets = Sets.newHashSet();
        for (Delta operation : operations) {
            addTarget(targets, operation.getOperation(), operation.getDeployed());
            addTarget(targets, operation.getOperation(), operation.getPrevious());
        }
        return filter(targets, new Predicate<Host>() {
            @Override
            public boolean apply(final Host input) {
                return input.hasProperty("isolated") && input.<Boolean>getProperty("isolated");
            }
        });
    }

    private void addTarget(Set<Host> targets, final Operation operation, Deployed<?, ?> deployed) {
        if (deployed == null)
            return;

        if (operation == Operation.NOOP)
            return;

        if (deployed.getContainer() instanceof HostContainer) {
            HostContainer hostContainer = (HostContainer) deployed.getContainer();
            targets.add(hostContainer.getHost());
        }
    }

    private Map<String, Object> getFreeMarkerContext(Container host) {
        return ImmutableMap.<String, Object>of("container", host);
    }
}
