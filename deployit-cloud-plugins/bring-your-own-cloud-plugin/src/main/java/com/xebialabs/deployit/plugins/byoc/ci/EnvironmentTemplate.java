package com.xebialabs.deployit.plugins.byoc.ci;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.xebialabs.deployit.plugin.api.udm.Metadata.ConfigurationItemRoot.CONFIGURATION;
import static com.xebialabs.deployit.plugin.cloud.util.MapsHelper.putOrMerge;
import static java.util.Collections.sort;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.ControlTask;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.cloud.ci.BaseHostTemplate;
import com.xebialabs.deployit.plugin.cloud.ci.CloudEnvironmentParameters;
import com.xebialabs.deployit.plugin.cloud.step.CheckParametersStep;
import com.xebialabs.deployit.plugin.cloud.step.RegisterEnvironmentStep;
import com.xebialabs.deployit.plugin.cloud.step.RegisterInstancesStep;
import com.xebialabs.deployit.plugin.cloud.step.plan.SingleStepFilter;
import com.xebialabs.deployit.plugin.cloud.step.plan.StepOrderComparator;
import com.xebialabs.deployit.plugin.cloud.util.CiParser;
import com.xebialabs.deployit.plugin.cloud.util.InstanceDescriptorResolver;

@SuppressWarnings("serial")
@Metadata(description = "BYOC environment template", root = CONFIGURATION)
public class EnvironmentTemplate extends com.xebialabs.deployit.plugin.cloud.ci.EnvironmentTemplate {
    protected static Type BYOC_HOST_TEMPLATE_TYPE = Type.valueOf(HostTemplate.class);

    // grrr...can't access these in parent
    private static InstanceDescriptorResolver descriptorResolver = new InstanceDescriptorResolver();
    private static CiParser ciParser = new CiParser();

    @Override
    @ControlTask(label = "Instantiate environment", parameterType = "cloud.CloudEnvironmentParameters", description = "Instantiate environment and all hosts, which templates are linked to this environment template")
    public List<? extends Step> instantiate(CloudEnvironmentParameters parameters) {
        // copied from parent
        List<Step> steps = newArrayList();

        steps.add(new CheckParametersStep(parameters));

        // Registry of all added steps per template type
        Map<Type, Collection<? extends Step>> waitStepsAdded = newHashMap();

        if (!getHostTemplates().isEmpty()) {
            int seq = 1;
            List<? extends Step> createSteps;
            for (final BaseHostTemplate template : getHostTemplates()) {
                if (template.getType().instanceOf(BYOC_HOST_TEMPLATE_TYPE)) {
                    createSteps = ((HostTemplate) template).produceCreateSteps(getName(), seq, parameters);
                } else {
                    createSteps = template.produceCreateSteps(getName(), seq);
                }
                Collection<? extends Step> stepsToAdd = filter(
                        createSteps,
                        new SingleStepFilter(waitStepsAdded, template.getType())
                );

                steps.addAll(stepsToAdd);

                putOrMerge(waitStepsAdded, template.getType(), newArrayList(stepsToAdd));

                seq++;
            }

            steps.add(new RegisterInstancesStep(descriptorResolver, ciParser, parameters.getHostsPath()));
        }

        steps.add(new RegisterEnvironmentStep(descriptorResolver, ciParser, this, parameters.getEnvironmentId()));

        sort(steps, new StepOrderComparator());

        return steps;
    }

}
