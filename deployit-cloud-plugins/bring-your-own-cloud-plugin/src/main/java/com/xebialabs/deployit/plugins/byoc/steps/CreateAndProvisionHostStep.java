package com.xebialabs.deployit.plugins.byoc.steps;

import static com.xebialabs.deployit.plugin.generic.freemarker.ConfigurationHolder.resolveExpression;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;
import com.xebialabs.deployit.plugin.api.udm.Parameters;
import com.xebialabs.deployit.plugin.cloud.step.ContextAttribute;
import com.xebialabs.deployit.plugin.cloud.util.ContextHelper;
import com.xebialabs.deployit.plugins.byoc.ci.HostTemplate;
import com.xebialabs.deployit.plugins.byoc.util.CommandRunner;
import com.xebialabs.deployit.plugins.byoc.util.StderrProcessOutputHandler;
import com.xebialabs.deployit.plugins.byoc.util.StdoutProcessOutputHandler;

@SuppressWarnings("serial")
public class CreateAndProvisionHostStep implements Step {

    private final HostTemplate template;
    private final String instanceName;
    private final Map<String, Object> freemarkerContext;

    public CreateAndProvisionHostStep(HostTemplate hostTemplate, String instanceLabel, 
            int instanceSeq, Parameters params) {
        template = hostTemplate;
        instanceName = instanceLabel;
        freemarkerContext = ImmutableMap.<String, Object>of("hostTemplate", template, 
                "sequenceNumber", instanceSeq, "params", params);
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    public String getDescription() {
        return "Create and provision " + instanceName;
    }

    @Override
    public StepExitCode execute(ExecutionContext context) throws Exception {
        try {
            CommandRunner runner = new CommandRunner(template.getWorkingDirectory(), buildCommand(), 
                new StdoutProcessOutputHandler(context), new StderrProcessOutputHandler(context));
            
            int result = runner.run(context);
            if (result == 0) {
                ContextHelper.wrapped(context).safeSet(ContextAttribute.USED_TEMPLATES, 
                    new ArrayList<HostTemplate>(), asList(template));
                
                return StepExitCode.SUCCESS;
            } else {
                context.logError("Creation and provisioning command returned exit code " + result);
                return StepExitCode.FAIL;
            }
        } catch(Throwable t) {
            context.logError("Error running creation and provisioning command: " + t);
            return StepExitCode.FAIL;
        }
    }

    private String[] buildCommand() {
        String createCommand = resolveExpression(template.getCreateCommand(), freemarkerContext);
        return createCommand.split(" ");
    }
}
