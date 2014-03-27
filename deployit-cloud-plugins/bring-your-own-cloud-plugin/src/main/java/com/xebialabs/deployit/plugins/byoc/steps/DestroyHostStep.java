package com.xebialabs.deployit.plugins.byoc.steps;

import static com.google.common.base.Preconditions.checkState;
import static com.xebialabs.deployit.plugin.generic.freemarker.ConfigurationHolder.resolveExpression;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugins.byoc.ci.HostTemplate;
import com.xebialabs.deployit.plugins.byoc.util.ByocCloudId;
import com.xebialabs.deployit.plugins.byoc.util.CommandRunner;
import com.xebialabs.deployit.plugins.byoc.util.StderrProcessOutputHandler;
import com.xebialabs.deployit.plugins.byoc.util.StdoutProcessOutputHandler;

@SuppressWarnings("serial")
public class DestroyHostStep implements Step {
    private static final String CLOUD_ID_PROPERTY = "cloudId";

    private final HostTemplate template;
    private final String instanceName;
    private final Map<String, Object> freemarkerContext;
    
    public DestroyHostStep(HostTemplate hostTemplate, ConfigurationItem instance) {
        template = hostTemplate;
        instanceName = instance.getName();
        checkState(instance.hasProperty(CLOUD_ID_PROPERTY), "'%s' is not a cloud-generated CI: missing property 'cloudId'", instance.getId());
        ByocCloudId cloudId = ByocCloudId.fromCloudId(instance.<String>getProperty(CLOUD_ID_PROPERTY));
        freemarkerContext = ImmutableMap.<String, Object>of("hostTemplate", template, 
                "sequenceNumber", cloudId.getSequenceNumber(), "host", instance);
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    public String getDescription() {
        return "Destroy instance " + instanceName;
    }

    @Override
    public StepExitCode execute(ExecutionContext context) throws Exception {
        try {
            CommandRunner runner = new CommandRunner(template.getWorkingDirectory(), buildCommand(), 
                new StdoutProcessOutputHandler(context), new StderrProcessOutputHandler(context));
        
            int result = runner.run(context);
            if (result == 0) {
                return StepExitCode.SUCCESS;
            } else {
                context.logError("Deprovisioning command returned exit code " + result);
                return StepExitCode.FAIL;
            }
        } catch(Throwable t) {
            context.logError("Error running deprovisioning command: " + t);
            return StepExitCode.FAIL;
        }
    }

    private String[] buildCommand() {
        String destroyCommand = resolveExpression(template.getDestroyCommand(), freemarkerContext);
        return destroyCommand.split(" ");
    }

}
