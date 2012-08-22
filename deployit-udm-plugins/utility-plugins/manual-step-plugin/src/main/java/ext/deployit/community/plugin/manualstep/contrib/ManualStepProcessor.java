package ext.deployit.community.plugin.manualstep.contrib;

import com.xebialabs.deployit.plugin.api.deployment.planning.PostPlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import ext.deployit.community.plugin.manualstep.ci.ContributorType;
import ext.deployit.community.plugin.manualstep.ci.ManualStep;
import ext.deployit.community.plugin.manualstep.ci.ManualSteps;
import ext.deployit.community.plugin.manualstep.step.InstructionStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class ManualStepProcessor {

    @PrePlanProcessor
    public List<Step> triggerManualStepsForPrePlanProcessor(DeltaSpecification deltaSpec) {
        return triggerManualSteps(deltaSpec, ContributorType.PRE_PLAN_PROCESSOR);
    }

    @PostPlanProcessor
    public List<Step> triggerManualStepsForPostPlanProcessor(DeltaSpecification deltaSpec) {
        return triggerManualSteps(deltaSpec, ContributorType.POST_PLAN_PROCESSOR);
    }

    private List<Step> triggerManualSteps(DeltaSpecification deltaSpec, ContributorType contributorType) {
        Map<String, Object> commonVars = newHashMap();
        commonVars.put("deltas", deltaSpec.getDeltas());
        commonVars.put("deployedApplication", deltaSpec.getDeployedApplication());
        commonVars.put("previousDeployedApplication", deltaSpec.getPreviousDeployedApplication());
        commonVars.put("operation", deltaSpec.getOperation());

        Environment environment = deltaSpec.getDeployedApplication().getEnvironment();
        Iterable<ManualStep> manualSteps = ManualSteps.getSteps(environment, contributorType, deltaSpec.getOperation());
        List<Step> steps = newArrayList();
        for (ManualStep manualStep : manualSteps) {
            HashMap<String,Object> vars = newHashMap(commonVars);
            vars.put("step",manualStep);
            InstructionStep step = new InstructionStep(manualStep, vars);
            steps.add(step);
        }

        return steps;
    }
}
