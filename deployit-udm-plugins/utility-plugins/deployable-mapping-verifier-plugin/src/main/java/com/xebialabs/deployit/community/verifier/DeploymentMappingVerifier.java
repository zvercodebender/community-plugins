package com.xebialabs.deployit.community.verifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;

import static com.xebialabs.deployit.community.verifier.RequiredInstancesEnforcement.NONE;

public class DeploymentMappingVerifier {
    public static final String REQUIRED_INSTANCES_PER_ENVIRONMENT_PROPERTY = "requiredInstancesPerEnvironment";
    public static final String REQUIRED_INSTANCES_ENFORCEMENT_PROPERTY = "requiredInstancesEnforcement";

    public List<String> validate(DeployedApplication deployedApplication) {
        final List<String> errorMessages = new ArrayList<String>();
        final RequiredInstancesEnforcement requiredInstancesEnforcement =
            deployedApplication.getEnvironment().getProperty(REQUIRED_INSTANCES_ENFORCEMENT_PROPERTY);

        if (!requiredInstancesEnforcement.equals(NONE)) {
            final Set<Deployable> allDeployables = deployedApplication.getVersion().getDeployables();
            final Map<Deployable, Integer> deployedCountsPerDeployable = new HashMap<Deployable, Integer>(allDeployables.size());
            for (final Deployable deployabe : allDeployables) {
                deployedCountsPerDeployable.put(deployabe, 0);
            }

            for (final Deployed<?, ?> deployed : deployedApplication.getDeployeds()) {
                final Deployable deployable = deployed.getDeployable();
                deployedCountsPerDeployable.put(deployable, deployedCountsPerDeployable.get(deployable) + 1);
            }

            for (Map.Entry<Deployable, Integer> entry : deployedCountsPerDeployable.entrySet()) {
                final Deployable deployable = entry.getKey();
                RequiredInstancesPerEnvironment requiredInstancesPerEnvironment = deployable.getProperty(REQUIRED_INSTANCES_PER_ENVIRONMENT_PROPERTY);

                if (!requiredInstancesPerEnvironment.isCompliant(entry.getValue(), requiredInstancesEnforcement)) {
                    errorMessages.add(String.format("Deployable %s has been mapped %d times, but required is %s",
                        deployable.getId(), entry.getValue(), requiredInstancesPerEnvironment));
                }
            }
        }

        return errorMessages;
    }
}