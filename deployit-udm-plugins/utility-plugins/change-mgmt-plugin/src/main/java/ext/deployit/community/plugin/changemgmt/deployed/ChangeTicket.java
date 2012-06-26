package ext.deployit.community.plugin.changemgmt.deployed;

import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.generic.ci.Resource;
import com.xebialabs.deployit.plugin.generic.deployed.ExecutedScript;

@SuppressWarnings("serial")
@Metadata(virtual = true, description = "An Change ticket in a chg.ChangeManager")
public class ChangeTicket extends ExecutedScript<Resource> {
    private static final String UPDATE_SCRIPT_PROPERTY = "updateScript";
    private static final String UPDATE_ORDER_PROPERTY = "updateOrder";

    @Override
    public void executeCreate(DeploymentPlanningContext ctx, Delta d) {
        super.executeCreate(ctx, d);
        addStep(ctx, this.<Integer>getProperty(UPDATE_ORDER_PROPERTY), 
                this.<String>getProperty(UPDATE_SCRIPT_PROPERTY), "Update");
    }
}
