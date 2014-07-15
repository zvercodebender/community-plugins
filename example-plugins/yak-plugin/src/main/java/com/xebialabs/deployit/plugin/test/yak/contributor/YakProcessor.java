package com.xebialabs.deployit.plugin.test.yak.contributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.plugin.api.deployment.planning.PostPlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.test.yak.step.FirstStep;
import com.xebialabs.deployit.plugin.test.yak.step.PostNotifyStep;
import com.xebialabs.deployit.plugin.test.yak.step.PreNotifyStep;

/**
 * This class contains contributors which only fire when specific
 * conditions are meet. Note that these methods will be invoked
 * for all plans, but steps will only be added to the deployment
 * plan when the appropriate conditions are meet
 */
public class YakProcessor {
    private static final Logger logger = LoggerFactory.getLogger(YakProcessor.class);

    @PrePlanProcessor
    public static Step preProcessBefore(DeltaSpecification spec) {
        logger.info("Should go first");
        if (spec.getDeployedApplication().getName().contains("before")) {
            return new FirstStep();
        }
        return null;
    }

    @PrePlanProcessor
    public static Step preprocess(DeltaSpecification spec) {
        logger.info("Preprocessing " + spec);
        DeployedApplication deployedApplication = spec.getDeployedApplication();
        if (deployedApplication.getName().contains("notify")) {
            return new PreNotifyStep();
        }
        return null;
    }

    @PostPlanProcessor
    public static Step postprocess(DeltaSpecification spec) {
        logger.info("Postprocessing  " + spec);
        DeployedApplication deployedApplication = spec.getDeployedApplication();
        if (deployedApplication.getName().contains("notify")) {
            return new PostNotifyStep();
        }
        return null;
    }
}
