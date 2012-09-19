package ext.deployit.community.plugin.changemgmt.planning;

import static com.xebialabs.deployit.deployment.planner.DeltaSpecificationBuilder.newSpecification;
import static com.xebialabs.deployit.test.support.TestUtils.createDeployedApplication;
import static com.xebialabs.deployit.test.support.TestUtils.createDeploymentPackage;
import static com.xebialabs.deployit.test.support.TestUtils.createEnvironment;
import static com.xebialabs.deployit.test.support.TestUtils.newInstance;
import static ext.deployit.community.plugin.changemgmt.planning.SetChangeTicketChecklistItem.CHANGE_TICKET_CHECKLIST_ITEM_NAME_PROPERTY;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.xebialabs.deployit.deployment.planner.DeltaSpecificationBuilder;
import com.xebialabs.deployit.plugin.api.boot.PluginBooter;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import com.xebialabs.deployit.plugin.generic.ci.Container;
import com.xebialabs.deployit.plugin.generic.deployed.ExecutedScript;
import com.xebialabs.deployit.plugin.test.yak.ci.YakServer;
import com.xebialabs.deployit.test.support.TestUtils;

import ext.deployit.community.plugin.changemgmt.OverrideTestSynthetics;
import ext.deployit.community.plugin.changemgmt.planning.SetChangeTicketChecklistItem;

/**
 * Unit tests for {@link SetChangeTicketChecklistItem}
 */
public class SetChangeTicketChecklistItemTest {
    private static String changeTicketChecklistItemSuffix;

    @Rule
    public OverrideTestSynthetics syntheticOverride = new OverrideTestSynthetics("src/test/resources");

    @Before
    public void boot() {
        PluginBooter.bootWithoutGlobalContext();
        changeTicketChecklistItemSuffix = TestUtils.<Container>newInstance("chg.ChangeManager")
            .getProperty(CHANGE_TICKET_CHECKLIST_ITEM_NAME_PROPERTY);
    }

    @Test(expected = IllegalStateException.class)
    public void failsIfChecklistPropertyIsNotDefined() {
        Environment env = newEnvironment();
        env.setProperty(format("requires%s", changeTicketChecklistItemSuffix), TRUE);        
        SetChangeTicketChecklistItem.setChecklistItem(newDeltaSpec(env).build());
    }

    @Test(expected = IllegalStateException.class)
    public void failsIfChecklistPropertyIsNotHidden() {
        Environment env = newEnvironment();
        env.setProperty(format("requires%s", changeTicketChecklistItemSuffix), TRUE);        
        SetChangeTicketChecklistItem.setChecklistItem(newDeltaSpec(env).build());
    }

    @Test
    public void ignoresEnvironmentsWithoutChangeTicketChecklistItem() {
        SetChangeTicketChecklistItem.setChecklistItem(newDeltaSpec(newEnvironment()).build());
    }

    @Test
    public void unsetsItemIfNoChangeTicketIsCreatedOrModified() {
        Environment env = newEnvironment();
        env.addMember((Container) newInstance("chg.ChangeManager"));
        env.setProperty(format("requires%s", changeTicketChecklistItemSuffix), TRUE);
        DeltaSpecification deltaSpec = newDeltaSpec(env).build();
        SetChangeTicketChecklistItem.setChecklistItem(deltaSpec);
        assertThat(deltaSpec.getDeployedApplication().getVersion()
            .getProperty(format("satisfies%s", changeTicketChecklistItemSuffix)), 
            Is.<Object>is(FALSE));
    }    

    @Test
    public void setsItemIfChangeTicketIsCreated() {
        Environment env = newEnvironment();
        env.addMember((Container) newInstance("chg.ChangeManager"));
        env.setProperty(format("requires%s", changeTicketChecklistItemSuffix), TRUE);
        DeltaSpecification deltaSpec = newDeltaSpec(env, 
                TestUtils.<ExecutedScript<?>>newInstance("chg.ChangeTicket2")).build();
        SetChangeTicketChecklistItem.setChecklistItem(deltaSpec);
        assertThat(deltaSpec.getDeployedApplication().getVersion()
                .getProperty(format("satisfies%s", changeTicketChecklistItemSuffix)), 
                Is.<Object>is(TRUE));
    }

    @Test
    public void setsItemIfChangeTicketIsOrModified() {
        Environment env = newEnvironment();
        env.addMember((Container) newInstance("chg.ChangeManager"));
        env.setProperty(format("requires%s", changeTicketChecklistItemSuffix), TRUE);        
        DeltaSpecificationBuilder specBuilder = newDeltaSpec(env);
        specBuilder.modify(TestUtils.<ExecutedScript<?>>newInstance("chg.ChangeTicket2"),
            TestUtils.<ExecutedScript<?>>newInstance("chg.ChangeTicket2"));
        DeltaSpecification deltaSpec = specBuilder.build();
        SetChangeTicketChecklistItem.setChecklistItem(deltaSpec);
        assertThat(deltaSpec.getDeployedApplication().getVersion()
                .getProperty(format("satisfies%s", changeTicketChecklistItemSuffix)), 
                Is.<Object>is(TRUE));
    }

    // not great, but where would a user put the change ticket for undeployment?
    @Test
    public void setsItemForUndeployment() {
        Environment env = newEnvironment();
        env.addMember((Container) newInstance("chg.ChangeManager"));
        env.setProperty(format("requires%s", changeTicketChecklistItemSuffix), TRUE);        
        DeltaSpecificationBuilder specBuilder = newSpecification().undeploy(
                createDeployedApplication(createDeploymentPackage(), env));
        specBuilder.destroy(TestUtils.<ExecutedScript<?>>newInstance("chg.ChangeTicket2"));
        DeltaSpecification deltaSpec = specBuilder.build();
        SetChangeTicketChecklistItem.setChecklistItem(deltaSpec);
        assertThat(deltaSpec.getDeployedApplication().getVersion()
                .getProperty(format("satisfies%s", changeTicketChecklistItemSuffix)), 
                Is.<Object>is(TRUE));
    }

    private static Environment newEnvironment() {
        return createEnvironment((YakServer) newInstance("yak.YakServer"));
    }

    private static DeltaSpecificationBuilder newDeltaSpec(Environment env,
            Deployed<?, ?>... newDeployeds) {
        DeltaSpecificationBuilder deltaSpec = 
            newSpecification().initial(createDeployedApplication(createDeploymentPackage(), env));
        deltaSpec.create((Deployed<?, ?>) newInstance("yak.DeployedYakFile"));
        for (Deployed<?, ?> newDeployed : newDeployeds) {
            deltaSpec.create(newDeployed);
        }
        return deltaSpec;
    }
}
