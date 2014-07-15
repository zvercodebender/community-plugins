package com.xebialabs.deployit.plugin.test.yak.ci;

import com.xebialabs.deployit.plugin.test.yak.step.DeleteYakFileFromServerStep;
import com.xebialabs.deployit.plugin.test.yak.step.DeployYakFileToServerStep;
import com.xebialabs.deployit.plugin.test.yak.step.StartDeployedYakFileStep;
import com.xebialabs.deployit.plugin.test.yak.step.StopDeployedYakFileStep;
import com.xebialabs.deployit.plugin.test.yak.step.UpgradeYakFileOnServerStep;
import com.xebialabs.deployit.plugin.api.deployment.planning.Create;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.planning.Destroy;
import com.xebialabs.deployit.plugin.api.deployment.planning.Modify;
import com.xebialabs.deployit.plugin.api.udm.base.BaseDeployedArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DeployedYakFile represents a YakFile deployed to a YakServer, as reflected
 * in the class definition. The class extends the built-in BaseDeployed class.
 * 
 * This class shows how to use the @Contributor to contribute steps to a deployment
 * that includes a configured instance of the DeployedYakFile. Each annotated method 
 * annotated is invoked when the specified operation is present in the deployment for 
 * the YakFile.
 * 
 * Notice that we have Template this with BaseDeployedArtifact<YakFile, YakServer>
 * to indicate what type of artifact we can deploy to what container here.
 * 
 * So for example, yakfile1 already exists on the target container CI so a 
 * MODIFY delta will be present in the delta specification for this CI, causing the stop, 
 * start and upgrade methods to be invoked on the CI instance. Because yakfile2 is new, 
 * a CREATE delta will be present, causing the start, and deploy method to be invoked 
 * on the CI instance.
 */
public class DeployedYakFile extends BaseDeployedArtifact<YakFile, YakServer> {

    /*
    * Standard SLF4J logger. This logger should be used for any logging from your
    * plugin. 
    */
    private static final Logger logger = LoggerFactory.getLogger(DeployedYakFile.class);
    
   @Modify
   @Destroy
   public void stop(DeploymentPlanningContext result) {
     logger.info("Adding stop artifact");
     result.addStep(new StopDeployedYakFileStep(this));
   }

   @Create
   @Modify
   public void start(DeploymentPlanningContext result) {
     logger.info("Adding start artifact");
     result.addStep(new StartDeployedYakFileStep(this));
   }

   @Create
   public void deploy(DeploymentPlanningContext result) {
     logger.info("Adding deploy step");
     result.addStep(new DeployYakFileToServerStep(this));
   }

   @Modify
   public void upgrade(DeploymentPlanningContext result) {
     logger.info("Adding upgrade step");
     result.addStep(new UpgradeYakFileOnServerStep(this));
   }

   @Destroy
   public void destroy(DeploymentPlanningContext result) {
     logger.info("Adding undeploy step");
     result.addStep(new DeleteYakFileFromServerStep(this));
   }

}
