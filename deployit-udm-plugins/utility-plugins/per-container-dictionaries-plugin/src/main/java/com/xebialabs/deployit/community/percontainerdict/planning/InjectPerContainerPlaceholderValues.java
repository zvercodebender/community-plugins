package com.xebialabs.deployit.community.percontainerdict.planning;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.filterValues;
import static com.google.common.collect.Sets.newHashSet;
import static com.xebialabs.deployit.plugin.api.util.Predicates2.equalToAny;
import static com.xebialabs.deployit.plugin.api.util.Predicates2.extractDeployed;
import static java.lang.String.format;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentStep;
import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.artifact.DerivedArtifact;

public class InjectPerContainerPlaceholderValues {
    protected static final String CONTAINER_DICTIONARY_PROPERTY = "dictionaryEntries";
    // workaround for http://tech.xebialabs.com/jira/browse/DEPLOYITPB-3414
    protected static final Set<String> PER_CONTAINER_PLACEHOLDER_TOKENS = 
        ImmutableSet.of("<per-container>", "&lt;per-container&gt;");
    protected static final List<DeploymentStep> NO_STEPS = ImmutableList.of();

    private static final Logger LOGGER = LoggerFactory.getLogger(InjectPerContainerPlaceholderValues.class);

    @PrePlanProcessor
    public static List<DeploymentStep> injectPlaceholderValues(DeltaSpecification specification) {
        Iterable<Deployed<?, ?>> derivedArtifactDeployeds = filter(
                transform(specification.getDeltas(), extractDeployed()), 
                instanceOf(DerivedArtifact.class));

        Builder<DerivedArtifact<?>, Container> artifactsWithPerContainerPlaceholders =
            ImmutableMap.builder();
        for (Deployed<?, ?> deployed : derivedArtifactDeployeds) {
            DerivedArtifact<?> derivedArtifact = (DerivedArtifact<?>) deployed;
            if (any(derivedArtifact.getPlaceholders().values(), equalToAny(PER_CONTAINER_PLACEHOLDER_TOKENS))) {
                artifactsWithPerContainerPlaceholders.put(derivedArtifact, deployed.getContainer());
            }
        }

        Map<DerivedArtifact<?>, Container> artifactsAndContainers = artifactsWithPerContainerPlaceholders.build();
        if (artifactsAndContainers.isEmpty()) {
            LOGGER.debug("No derived artifacts with per-container placeholders found for {}", specification.getDeployedApplication());
            return NO_STEPS;
        }

        Set<String> validationErrors = newHashSet();

        for (Entry<DerivedArtifact<?>, Container> artifactAndContainer 
                : artifactsAndContainers.entrySet()) {
            Map<String, String> containerPlaceholders = getContainerPlaceholders(artifactAndContainer.getValue());
            if (containerPlaceholders == null) {
                validationErrors.add(format("'%s' requires per-container placeholders but container '%s' does not define the required '%s' property", 
                        artifactAndContainer.getKey().getName(), artifactAndContainer.getValue().getName(),
                        CONTAINER_DICTIONARY_PROPERTY));
                continue;
            }

            Map<String, String> artifactPlaceholders = artifactAndContainer.getKey().getPlaceholders();
            Set<String> perContainerPlaceholders = filterValues(artifactPlaceholders, 
                    equalToAny(PER_CONTAINER_PLACEHOLDER_TOKENS)).keySet();
            for (String requiredValue : perContainerPlaceholders) {
                if (!containerPlaceholders.containsKey(requiredValue)) {
                    validationErrors.add(format("'%s' requires per-container value for '%s' but no such entry is defined for container '%s'",
                            artifactAndContainer.getKey().getName(), requiredValue, artifactAndContainer.getValue().getName()));
                } else {
                    LOGGER.debug("Setting per-container placeholder value '{}' on '{}' to '{}'",
                            new Object[] { requiredValue, artifactAndContainer.getKey().getName(),
                                           containerPlaceholders.get(requiredValue) });
                    artifactPlaceholders.put(requiredValue, 
                            containerPlaceholders.get(requiredValue));
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(buildErrorMessage(
                    specification.getDeployedApplication(), validationErrors));
        }
        return NO_STEPS;
    }

    private static Map<String, String> getContainerPlaceholders(Container value) {
        return (value.hasProperty(CONTAINER_DICTIONARY_PROPERTY) 
                ? value.<Map<String, String>>getProperty(CONTAINER_DICTIONARY_PROPERTY)
                : null);
    }

    private static String buildErrorMessage(DeployedApplication deployedApplication,
            Set<String> validationErrors) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Cannot deploy '").append(deployedApplication.getName())
        .append("' (version ").append(deployedApplication.getVersion().getVersion())
        .append(") to '").append(deployedApplication.getEnvironment().getName())
        .append("' due to the following errors:");
        for (String validationError : validationErrors) {
            errorMessage.append("\n- ").append(validationError);
        }
        return errorMessage.toString();
    }
}
