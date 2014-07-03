package com.xebialabs.deployit.plugin.test.yak.step;

import com.xebialabs.deployit.plugin.test.yak.ci.YakServer;
import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;

/**
 * The steps in this package are all similar in that they
 * do not to a lot of work, but are here to illustrate step
 * classes.
 */
@SuppressWarnings("serial")
public class StartYakServerStep implements Step {

   private YakServer server;

   public StartYakServerStep(YakServer server) {
     this.server = server;
   }

   @Override
   public String getDescription() {
     return "Starting: " + server;
   }

   @Override
   public StepExitCode execute(ExecutionContext ctx) throws Exception {
     return StepExitCode.SUCCESS;
   }


   public YakServer getServer() {
     return server;
   }

   @Override
   public int getOrder() {
     return 90;
   }
}
