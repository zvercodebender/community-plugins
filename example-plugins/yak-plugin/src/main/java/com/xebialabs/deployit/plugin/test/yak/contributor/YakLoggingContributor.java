package com.xebialabs.deployit.plugin.test.yak.contributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.plugin.api.deployment.planning.PostPlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;

/**
 * This class contains 4 contributors. These methods will ba called at
 * planning time. This particular implementation doesn't do anything
 * beyond that as each method does not return a step
 */
@SuppressWarnings("unused")
public class YakLoggingContributor {

    private static final Logger logger = LoggerFactory.getLogger(YakLoggingContributor.class);

    @PrePlanProcessor(order = 50)
    public static Step aPreProcessOrder5O(DeltaSpecification spec) {
        logger.info("this is preprocessor with order 50");
        return null;
    }

    @PrePlanProcessor(order = 0)
    public static Step zPreProcessOrderO(DeltaSpecification spec) {
        logger.info("this is preprocessor with order 0");
        return null;
    }

    @PostPlanProcessor(order = 50)
    public static Step zPostProcessOrder5O(DeltaSpecification spec) {
        logger.info("this is postprocessor with order 50");
        return null;
    }

    @PostPlanProcessor(order = 0)
    public static Step aPostProcessOrderO(DeltaSpecification spec) {
        logger.info("this is postprocessor with order 0");
        return null;
    }
}
