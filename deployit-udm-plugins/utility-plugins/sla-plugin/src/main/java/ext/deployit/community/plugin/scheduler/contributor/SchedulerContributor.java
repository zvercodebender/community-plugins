package ext.deployit.community.plugin.scheduler.contributor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;

import ext.deployit.community.plugin.scheduler.step.AtDateWaitStep;

import static java.lang.String.format;

public class SchedulerContributor {

    @PrePlanProcessor
    public Step injectPersonalCredentials(DeltaSpecification specification) {
        final DeployedApplication deployedApplication = specification.getDeployedApplication();

        final String slaStartsAtTime = deployedApplication.getProperty("slaStartsAtTime");
        final String slaEndsAtTime = deployedApplication.getProperty("slaEndsAtTime");

        final String timePattern = deployedApplication.getProperty("timePattern");

        if (Strings.isNullOrEmpty(slaStartsAtTime) && Strings.isNullOrEmpty(slaStartsAtTime)) {
            return null;
        }
        if (Strings.isNullOrEmpty(slaStartsAtTime) || Strings.isNullOrEmpty(slaStartsAtTime)) {
            logger.warn("slaStartsAtTime {} or slaStartsAtTime {} is empty", slaStartsAtTime, slaStartsAtTime);
            return null;
        }

        final DateFormat dateFormat = new SimpleDateFormat(format("%s",timePattern));
        try {
            final Date start = dateFormat.parse(format("%s", slaStartsAtTime));
            final Date end = dateFormat.parse(format("%s", slaEndsAtTime));
            return new AtDateWaitStep(parsed);
        } catch (ParseException e) {
            throw Throwables.propagate(e);
        }
    }

    protected static final Logger logger = LoggerFactory.getLogger(SchedulerContributor.class);
}
