package ext.deployit.community.plugin.manualstep.contrib;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.xebialabs.deployit.plugin.api.deployment.planning.Contributor;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.Deltas;
import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import ext.deployit.community.plugin.manualstep.ci.ContributorType;
import ext.deployit.community.plugin.manualstep.ci.ManualStep;
import ext.deployit.community.plugin.manualstep.ci.ManualSteps;
import ext.deployit.community.plugin.manualstep.step.InstructionStep;

import java.util.Map;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Maps.newHashMap;

public class ManualStepContributor {

    @Contributor
    public void triggerManualSteps(Deltas deltas, DeploymentPlanningContext ctx) {
        Operation operation = determineDeploymentOperation(deltas);
        Map<String, Object> commonVars = newHashMap();
        commonVars.put("deltas", deltas);
        commonVars.put("deployedApplication", ctx.getDeployedApplication());
        commonVars.put("operation", operation);

        Environment environment = ctx.getDeployedApplication().getEnvironment();
        Iterable<ManualStep> manualSteps = ManualSteps.getSteps(environment, ContributorType.EVERY_SUBPLAN, operation);
        for (ManualStep manualStep : manualSteps) {
            Map<String,Object> vars = newHashMap(commonVars);
            vars.put("step",manualStep);
            InstructionStep step = new InstructionStep(manualStep, vars);
            ctx.addStep(step);
        }
    }

    private Operation determineDeploymentOperation(Deltas deltas) {
        Operation operation = Operation.MODIFY;
        int size = deltas.getDeltas().size();
        if(numberOfDeltasForOperation(deltas, Operation.CREATE) == size) {
            operation = Operation.CREATE;
        } else if(numberOfDeltasForOperation(deltas, Operation.DESTROY) == size) {
            operation = Operation.DESTROY;
        }  else if(numberOfDeltasForOperation(deltas, Operation.NOOP) == size) {
            operation = Operation.NOOP;
        }
        return operation;
    }

    private int numberOfDeltasForOperation(Deltas deltas, final Operation operation) {
        return Iterables.size(filter(deltas.getDeltas(), new Predicate<Delta>() {
            @Override
            public boolean apply(Delta input) {
                return input.getOperation() == operation;
            }
        }));

    }
}
