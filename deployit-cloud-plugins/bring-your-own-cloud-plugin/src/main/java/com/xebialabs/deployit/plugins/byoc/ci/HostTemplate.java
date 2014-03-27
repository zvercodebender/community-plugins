package com.xebialabs.deployit.plugins.byoc.ci;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.ControlTask;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.api.udm.Property;
import com.xebialabs.deployit.plugin.cloud.ci.BaseHostTemplate;
import com.xebialabs.deployit.plugin.cloud.ci.CloudEnvironmentParameters;
import com.xebialabs.deployit.plugin.cloud.step.RegisterInstancesStep;
import com.xebialabs.deployit.plugin.cloud.step.WaitForInstancesStep;
import com.xebialabs.deployit.plugin.cloud.util.CiParser;
import com.xebialabs.deployit.plugin.cloud.util.InstanceDescriptorResolver;
import com.xebialabs.deployit.plugin.cloud.util.MarkerChecker;
import com.xebialabs.deployit.plugins.byoc.steps.CreateAndProvisionHostStep;
import com.xebialabs.deployit.plugins.byoc.steps.DestroyHostStep;
import com.xebialabs.deployit.plugins.byoc.steps.FindIpAddressStep;
import com.xebialabs.deployit.plugins.byoc.task.InstanceParameters;
import com.xebialabs.deployit.plugins.byoc.util.ByocCloudId;

@SuppressWarnings("serial")
@Metadata(description = "Provisioned instance template", root = Metadata.ConfigurationItemRoot.CONFIGURATION)
public class HostTemplate extends BaseHostTemplate {

    @Property(description = "The directory on the Deployit server in which commands to create and destroy instances should be invoked")
    public String workingDirectory;

    @Property(description="The command to invoke to create and provision a new host")
    public String createCommand;

    @Property(description="The command to invoke to determine the IP address of the new host. The output of the command will be matched against a regular expression")
    public String findIpAddressCommand;

    @Property(defaultValue = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})", description="The regular expression to apply to the output of 'findIpAddressCommand'. The first matching group of the expression will be the IP address")
    public String findIpAddressRegex;

    @Property(description="The command to invoke to deprovision an existing host")
    public String destroyCommand;

    @Override
    public List<? extends Step> produceCreateSteps(String environmentTemplateName, int instanceSeq) {
        throw new UnsupportedOperationException("byoc.HostTemplate instances can only be members of a byoc.EnvironmentTemplate");
    }

    public List<? extends Step> produceCreateSteps(String environmentTemplateName, int instanceSeq, CloudEnvironmentParameters params) {
        String instanceLabel = environmentTemplateName + " (" + getName() + ") #" + instanceSeq;
        return Arrays.asList(
            new CreateAndProvisionHostStep(this, instanceLabel, instanceSeq, params),
            new FindIpAddressStep(this, instanceLabel, instanceSeq, params));
    }

    @Override
    public List<? extends Step> produceDestroySteps(ConfigurationItem instance) {
        return ImmutableList.of((new DestroyHostStep(this, instance)));
    }

    @Override
    public String getInstanceIpAddress(String cloudId) throws TimeoutException {
        return ByocCloudId.fromCloudId(cloudId).getAddress();
    }
    
    @ControlTask(label = "Instantiate", description = "Create instance from template", parameterType = "byoc.InstanceParameters")
    public List<? extends Step> instantiate(InstanceParameters params) {
        return newArrayList(
                new CreateAndProvisionHostStep(this, params.getInstanceName(), 1, params),
                new FindIpAddressStep(this, params.getInstanceName(), 1, params),
                new WaitForInstancesStep(new MarkerChecker()),
                new RegisterInstancesStep(new InstanceDescriptorResolver(), new CiParser(), params.getHostsLocation())
        );
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getCreateCommand() {
        return createCommand;
    }

    public void setCreateCommand(String createCommand) {
        this.createCommand = createCommand;
    }

    public String getFindIpAddressCommand() {
        return findIpAddressCommand;
    }

    public void setFindIpAddressCommand(String findIpAddressCommand) {
        this.findIpAddressCommand = findIpAddressCommand;
    }

    public String getFindIpAddressRegex() {
        return findIpAddressRegex;
    }

    public void setFindIpAddressRegex(String findIpAddressRegex) {
        this.findIpAddressRegex = findIpAddressRegex;
    }

    public String getDestroyCommand() {
        return destroyCommand;
    }

    public void setDestroyCommand(String destroyCommand) {
        this.destroyCommand = destroyCommand;
    }
}
