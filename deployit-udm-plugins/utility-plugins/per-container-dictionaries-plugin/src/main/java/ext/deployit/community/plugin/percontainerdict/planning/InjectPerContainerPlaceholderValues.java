package ext.deployit.community.plugin.percontainerdict.planning;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.Dictionary;
import com.xebialabs.deployit.plugin.api.udm.artifact.DerivedArtifact;

import ext.deployit.community.plugin.percontainerdict.util.ContainerDictionaryGenerator;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.filterValues;
import static com.google.common.collect.Sets.newHashSet;
import static ext.deployit.community.plugin.percontainerdict.util.Predicates2.equalToAny;
import static ext.deployit.community.plugin.percontainerdict.util.Predicates2.extractDeployed;
import static java.lang.String.format;

public class InjectPerContainerPlaceholderValues {
    protected static final String CONTAINER_DICTIONARY_PROPERTY = "dictionary";

    // workaround for http://tech.xebialabs.com/jira/browse/DEPLOYITPB-3414
    protected static final Set<String> PER_CONTAINER_PLACEHOLDER_TOKENS =
            ImmutableSet.of("<per-container>", "&lt;per-container&gt;");

    protected static final List<Step> NO_STEPS = ImmutableList.of();

    private static final Logger LOGGER = LoggerFactory.getLogger(InjectPerContainerPlaceholderValues.class);

    @PrePlanProcessor
    public List<Step> injectPlaceholderValues(DeltaSpecification specification) {
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
            final List<Dictionary> dictionaries = specification.getDeployedApplication().getEnvironment().getDictionaries();
            final Map<String, String> containerPlaceholders = getContainerPlaceholders(dictionaries, artifactAndContainer.getValue());
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
                final String containerValue = containerPlaceholders.get(requiredValue);
                if (PER_CONTAINER_PLACEHOLDER_TOKENS.contains(containerValue)) {
                    //after processing all the dictionaries the values is still "<per-container>"
                    validationErrors.add(format("'%s' requires per-container value for '%s' but no such entry is defined for container '%s'",
                            artifactAndContainer.getKey().getName(), requiredValue, artifactAndContainer.getValue().getName()));
                } else {
                    LOGGER.debug("Setting per-container placeholder value '{}' on '{}' to '{}'",
                            new Object[]{requiredValue, artifactAndContainer.getKey().getName(),
                                    containerValue});
                    artifactPlaceholders.put(requiredValue, containerValue);
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(buildErrorMessage(
                    specification.getDeployedApplication(), validationErrors));
        }
        return NO_STEPS;
    }

    private Map<String, String> getContainerPlaceholders(final List<Dictionary> dictionaries, Container container) {
        if (!container.hasProperty(CONTAINER_DICTIONARY_PROPERTY)) {
            return null;
        }

        Dictionary generatedContainerDictionary = new ContainerDictionaryGenerator().generate(container);
        LOGGER.debug(" generatedContainerDictionary = {}", generatedContainerDictionary.getEntries());

        Dictionary associatedDictionary = container.getProperty(CONTAINER_DICTIONARY_PROPERTY);
        if (associatedDictionary == null)
            associatedDictionary = new Dictionary();
        LOGGER.debug(" associatedDictionary = {}", associatedDictionary.getEntries());

        try {
            final ImmutableList.Builder<Dictionary> dictionaryBuilder = new ImmutableList.Builder<Dictionary>();
            final List<Dictionary> allDictionaries = dictionaryBuilder
                    .add(associatedDictionary)
                    .add(generatedContainerDictionary)
                    .addAll(dictionaries)
                    .build();
            return geEntriesFromTheConsolidatedDictionary(allDictionaries);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private String buildErrorMessage(DeployedApplication deployedApplication,
                                     Set<String> validationErrors) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Cannot deploy '").append(deployedApplication.getName())
                .append("' (version ").append(deployedApplication.getVersion().getVersion())
                .append(") to '").append(deployedApplication.getEnvironment().getName())
                .append("' due to the following errors:");
        for (String validationError : validationErrors) {
            errorMessage.append("\n - ").append(validationError);
        }
        return errorMessage.toString();
    }

    private Map<String, String> geEntriesFromTheConsolidatedDictionary(final List<Dictionary> dictionaries) throws Exception {
        try {
            final String CONSOLIDATE_DICTIONARY_CLASSNAME = "com.xebialabs.deployit.service.replacement.ConsolidatedDictionary";
            final Class<?> consolidatedDictionaryClass = this.getClass().getClassLoader().loadClass(CONSOLIDATE_DICTIONARY_CLASSNAME);
            final Method consolidatedDictionaryClassMethod = consolidatedDictionaryClass.getMethod("create", Collection.class);
            final Object invoke = consolidatedDictionaryClassMethod.invoke(null, dictionaries);
            final Method getEntriesMethod = consolidatedDictionaryClass.getMethod("getEntries");
            final Map<String, String> entries = (Map<String, String>) getEntriesMethod.invoke(invoke);
            return entries;
        } catch (Exception e) {
            if (e.getCause() instanceof IllegalStateException) {
                throw new Exception(format("Cannot compute the entries: %s", e.getCause().getMessage()));
            }
            throw e;
        }
    }
}
