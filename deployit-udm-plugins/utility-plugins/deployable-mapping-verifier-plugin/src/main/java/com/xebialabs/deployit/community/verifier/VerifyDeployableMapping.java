package com.xebialabs.deployit.community.verifier;

import java.util.List;

import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;

public class VerifyDeployableMapping {
    private final DeploymentMappingVerifier validator = new DeploymentMappingVerifier();

    @PrePlanProcessor
    public List<Step> verify(DeltaSpecification spec) {
        final DeployedApplication deployedApplication = spec.getDeployedApplication();
        List<String> errorMessages = validator.validate(deployedApplication);

        if (!errorMessages.isEmpty()) {
            throw new IllegalArgumentException(buildErrorMessage(deployedApplication, errorMessages));
        }

        return null;
    }

    private String buildErrorMessage(DeployedApplication deployedApplication, List<String> messages) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Cannot deploy '").append(deployedApplication.getName())
            .append("' (version ").append(deployedApplication.getVersion().getVersion())
            .append(") to '").append(deployedApplication.getEnvironment().getName())
            .append("' as the following mapping requirements are not met:");
        for (String message : messages) {
            errorMessage.append("\n- '").append(message).append("'");
        }
        return errorMessage.toString();
    }
}
