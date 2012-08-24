package ext.deployit.community.plugin.workdir.planning;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static java.lang.Boolean.TRUE;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.xebialabs.deployit.plugin.api.deployment.planning.Contributor;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Deltas;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.generic.deployed.AbstractDeployed;
import com.xebialabs.deployit.plugin.generic.step.ArtifactDeleteStep;
import com.xebialabs.deployit.plugin.overthere.HostContainer;

public class ClearWorkDirectoriesContributor extends SingleTypeContributor<AbstractDeployed<?>> {
    protected static final String CLEAR_WORK_DIRECTORIES_PROPERTY = "clearWorkDirectories";
    protected static final String CLEAR_WORK_DIRECTORIES_ORDER_PROPERTY = "clearWorkDirectoriesOrder";
    protected static final String WORK_DIRECTORIES_PROPERTY = "workDirectories";

    public ClearWorkDirectoriesContributor() {
        super(Type.valueOf(AbstractDeployed.class));
    }

    @Contributor
    public void clearDirectories(Deltas deltas, DeploymentPlanningContext ctx) {
        filterDeltas(deltas.getDeltas());

        Iterable<AbstractDeployed<?>> deployedsModifiedOrDeleted = concat(deployedsRemoved,
                transform(deployedsModified, new Function<TypedDelta, AbstractDeployed<?>>() {
                        @Override
                        public AbstractDeployed<?> apply(TypedDelta input) {
                            return input.getPrevious();
                        }
                    }));
        Iterable<AbstractDeployed<?>> oldDeployedsWithWorkDirectories = filter(deployedsModifiedOrDeleted, 
                new Predicate<AbstractDeployed<?>>() {
                    @Override
                    public boolean apply(AbstractDeployed<?> input) {
                        return input.hasProperty(CLEAR_WORK_DIRECTORIES_PROPERTY);
                    }
                });
        for (AbstractDeployed<?> oldDeployed : oldDeployedsWithWorkDirectories) {
            // may be null
            if (TRUE.equals(oldDeployed.getProperty(CLEAR_WORK_DIRECTORIES_PROPERTY))) {
                List<String> workDirectories = oldDeployed.getProperty(WORK_DIRECTORIES_PROPERTY);
                if (workDirectories != null) {
                    for (String directory : workDirectories) {
                        // the cast shouldn't be necessary, really...no idea why Eclipse needs it
                        ctx.addStep(new ArtifactDeleteStep(oldDeployed.<Integer>getProperty(CLEAR_WORK_DIRECTORIES_ORDER_PROPERTY), 
                                (HostContainer) oldDeployed.getContainer(), directory));
                    }
                }
            }
        }
    }
}