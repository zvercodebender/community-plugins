package com.xebialabs.deployit.plugins.byoc.task;

import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.api.udm.Parameters;
import com.xebialabs.deployit.plugin.api.udm.Property;
import com.xebialabs.deployit.plugin.api.validation.Regex;
import com.xebialabs.deployit.plugins.byoc.ci.HostTemplate;

/**
 * Set of parameters needed to instantiate a single {@link HostTemplate}
 */
@SuppressWarnings("serial")
@Metadata(description = "Parameters for cloud instance instantiation")
public class InstanceParameters extends Parameters {

    @Property(required = true, defaultValue = "Infrastructure", label = "Hosts location", description = "Repository location where all created hosts will appear")
    @Regex(pattern = "Infrastructure/?.*")
    private String hostsLocation;

    @Property(required = false, label = "Instance name", description = "Name of the instance after creation")
    private String instanceName;

    public String getHostsLocation() {
        return hostsLocation;
    }

    public String getInstanceName() {
        return instanceName;
    }
}
