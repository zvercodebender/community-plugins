package com.xebialabs.deployit.community.changemgmt.planning;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.xebialabs.deployit.plugin.api.util.Predicates2.extractDeployed;
import static com.xebialabs.deployit.plugin.api.util.Predicates2.instanceOf;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

import java.util.List;

import com.google.common.base.Predicate;
import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentStep;
import com.xebialabs.deployit.plugin.api.deployment.planning.PostPlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.generic.ci.Container;
import com.xebialabs.deployit.plugin.generic.deployed.ExecutedScript;
import com.xebialabs.deployit.plugin.generic.step.ScriptExecutionStep;

public class AddChangeTicketUpdate {
    protected static final Predicate<ConfigurationItem> IS_CHANGE_TICKET = 
        instanceOf(Type.valueOf("chg.ChangeTicket"));

    protected static final String CHANGE_MANAGER_UPDATE_TICKET_PROPERTY = "updateChangeTicketAfterDeployment";
    protected static final String CHANGE_TICKET_UPDATE_SCRIPT_PROPERTY = "updateScript";
    protected static final String CHANGE_TICKET_UPDATE_ORDER_PROPERTY = "updateOrder";

    @PostPlanProcessor
    public static List<DeploymentStep> addUpdateStep(DeltaSpecification specification) {
        List<DeploymentStep> additionalSteps = newArrayList();
        Iterable<Deployed<?, ?>> changeTickets = filter(
                transform(specification.getDeltas(), extractDeployed()), 
                IS_CHANGE_TICKET);

        for (Deployed<?, ?> changeTicket : changeTickets) {
            Container changeManager = (Container) changeTicket.getContainer();
            // not required so can be null
            if (TRUE.equals(changeManager.<Boolean>getProperty(CHANGE_MANAGER_UPDATE_TICKET_PROPERTY))) {
                additionalSteps.add(new ScriptExecutionStep(changeTicket.<Integer>getProperty(CHANGE_TICKET_UPDATE_ORDER_PROPERTY), 
                        changeTicket.<String>getProperty(CHANGE_TICKET_UPDATE_SCRIPT_PROPERTY), 
                        changeManager, ((ExecutedScript<?>) changeTicket).getDeployedAsFreeMarkerContext(), 
                        format("Update %s in %s", changeTicket.getName(), changeManager.getName())));
            }
        }
        return additionalSteps;
    }
}
