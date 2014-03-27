package com.xebialabs.deployit.plugins.byoc.steps;

import static com.xebialabs.deployit.plugin.generic.freemarker.ConfigurationHolder.resolveExpression;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.MULTILINE;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;
import com.xebialabs.deployit.plugin.api.udm.Parameters;
import com.xebialabs.deployit.plugin.cloud.step.ContextAttribute;
import com.xebialabs.deployit.plugin.cloud.util.ContextHelper;
import com.xebialabs.deployit.plugins.byoc.ci.HostTemplate;
import com.xebialabs.deployit.plugins.byoc.util.ByocCloudId;
import com.xebialabs.deployit.plugins.byoc.util.CapturingStdoutProcessOutputHandler;
import com.xebialabs.deployit.plugins.byoc.util.CommandRunner;
import com.xebialabs.deployit.plugins.byoc.util.StderrProcessOutputHandler;

@SuppressWarnings("serial")
public class FindIpAddressStep implements Step {

    private final HostTemplate template;
    private final String instanceName;
    private final int sequenceNumber;
    private final Map<String, Object> freemarkerContext;

    public FindIpAddressStep(HostTemplate hostTemplate, String instanceLabel, int instanceSeq, Parameters params) {
        template = hostTemplate;
        instanceName = instanceLabel;
        sequenceNumber = instanceSeq;
        freemarkerContext = ImmutableMap.<String, Object>of("hostTemplate", template, 
                "sequenceNumber", instanceSeq, "params", params);
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    public String getDescription() {
        return "Determine IP address for instance " + instanceName;
    }

    @Override
    public StepExitCode execute(ExecutionContext context) throws Exception {
        try {
            CapturingStdoutProcessOutputHandler outputGobbler = new CapturingStdoutProcessOutputHandler(context);
            CommandRunner runner = new CommandRunner(template.getWorkingDirectory(), buildCommand(), 
                outputGobbler, new StderrProcessOutputHandler(context));
        
            int result = runner.run(context);
            // urgh! Need to the give the gobbler some time...
            Thread.sleep(1000);
            if (result == 0) {
                String ipAddress = findIpAddress(outputGobbler.getOutput());
                context.logOutput("Found IP address " + ipAddress);
                ByocCloudId cloudId = new ByocCloudId(ipAddress, sequenceNumber);
                ContextHelper.wrapped(context).safeSet(ContextAttribute.CREATED_INSTANCES, 
                    new ArrayList<String>(), asList(cloudId.toString()));
                
                return StepExitCode.SUCCESS;
            } else {
                context.logError("IP address discovery command returned exit code " + result);
                return StepExitCode.FAIL;
            }
        } catch(Throwable t) {
            context.logError("Error running IP address discovery command: " + t);
            return StepExitCode.FAIL;
        }
    }

    private String[] buildCommand() {
        String findIpAddressCommand = resolveExpression(template.getFindIpAddressCommand(), freemarkerContext);
        return findIpAddressCommand.split(" ");
    }

    protected String findIpAddress(String output) {
        Pattern p = Pattern.compile(template.getFindIpAddressRegex(), MULTILINE);
        Matcher matcher = p.matcher(output);
        if (matcher.find())
            return matcher.group(1);
        else throw new IllegalStateException(format("Unable to determine IP address from output '%s' using pattern '%s'", output, p.pattern()));
    }

}
