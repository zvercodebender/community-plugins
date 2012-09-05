package ext.deployit.community.plugin.personalcredentials.contributor;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.execution.Step;
import com.xebialabs.deployit.plugin.api.reflect.PropertyDescriptor;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.deployit.plugin.overthere.HostContainer;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.transform;

public class IdentityContributor {

    @PrePlanProcessor
    public Step injectPersonalCredentials(DeltaSpecification specification) {
        final List<Delta> deltas = specification.getDeltas();
        final DeployedApplication deployedApplication = specification.getDeployedApplication();
        final Environment environment = deployedApplication.getEnvironment();

        Boolean override = environment.getProperty("overrideHostCredentials");
        if (!override)
            return null;

        final List<String> requiredFieldsForPersonalCredentials = getRequiredFieldsForPersonalCredentials(deployedApplication);
        final Iterable<String> nullCredentialFields = filter(requiredFieldsForPersonalCredentials, new Predicate<String>() {
            @Override
            public boolean apply(@Nullable final String input) {
                return deployedApplication.getProperty(input) == null;
            }
        });

        if (!isEmpty(nullCredentialFields)) {
            throw new RuntimeException(String.format("Missing required fields for personal authentication %s", nullCredentialFields));
        }

        final Set<Host> hosts = ImmutableSet.<Host>builder()
                .addAll(filter(transform(deltas, DEPLOYED_TO_HOST), notNull()))
                .addAll(filter(transform(deltas, PREVIOUS_TO_HOST), notNull()))
                .build();

        logger.debug("Hosts {}", hosts);
        for (Host host : hosts) {
            override(deployedApplication, host);
        }

        return null;
    }

    private List<String> getRequiredFieldsForPersonalCredentials(final DeployedApplication deployedApplication) {
        Boolean perOsCredential = deployedApplication.getEnvironment().getProperty("perOsCredential");
        if (perOsCredential)
            return ImmutableList.of("unixUsername", "unixPassword", "windowsUsername", "windowsPassword");
        else
            return ImmutableList.of("username", "password");
    }

    private void override(DeployedApplication deployedApplication, Host host) {
        Boolean perOsCredential = deployedApplication.getEnvironment().getProperty("perOsCredential");
        if (perOsCredential) {
            switch (host.getOs()) {
                case WINDOWS:
                    logger.debug("IdentityContributor injects credentials in a {} host {}", "WINDOWS", host.getId());
                    host.setProperty("username", deployedApplication.getProperty("windowsUsername"));
                    host.setProperty("password", deployedApplication.getProperty("windowsPassword"));
                    return;
                case UNIX:
                    logger.debug("IdentityContributor injects credentials in a {} host {}", "UNIX", host.getId());
                    host.setProperty("username", deployedApplication.getProperty("unixUsername"));
                    host.setProperty("password", deployedApplication.getProperty("unixPassword"));
                    return;
            }
        } else {
            logger.debug("IdentityContributor injects credentials in a host {} ", host.getId());
            host.setProperty("username", deployedApplication.getProperty("username"));
            host.setProperty("password", deployedApplication.getProperty("password"));
        }
    }

    private static final Function<Delta, Host> DEPLOYED_TO_HOST = new ToHost() {
        public Host apply(Delta input) {
            return toHost(input.getDeployed());
        }
    };

    private static final Function<Delta, Host> PREVIOUS_TO_HOST = new ToHost() {
        public Host apply(Delta input) {
            return toHost(input.getPrevious());
        }
    };

    static abstract class ToHost implements Function<Delta, Host> {
        protected Host toHost(Deployed<?, ?> deployed) {
            if (deployed == null) {
                return null;
            }
            return toHost(deployed.getContainer());
        }

        private Host toHost(final ConfigurationItem item) {
            if (item instanceof Host) {
                return (Host) item;
            }
            if (item instanceof HostContainer) {
                HostContainer hostContainer = (HostContainer) item;
                return hostContainer.getHost();
            }
            final Collection<PropertyDescriptor> propertyDescriptors = item.getType().getDescriptor().getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getReferencedType() == null)
                    continue;
                if (propertyDescriptor.getReferencedType().instanceOf(Type.valueOf(Host.class))
                        || propertyDescriptor.isAsContainment()) {
                    final Host host = toHost((ConfigurationItem) propertyDescriptor.get(item));
                    if (host != null)
                        return host;
                }
            }
            return null;
        }
    }

    protected static final Logger logger = LoggerFactory.getLogger(IdentityContributor.class);
}