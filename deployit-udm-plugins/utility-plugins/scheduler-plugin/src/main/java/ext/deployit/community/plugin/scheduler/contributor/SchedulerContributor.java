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

import ext.deployit.community.plugin.scheduler.step.WaitUntilDateTimeStep;

import static java.lang.String.format;

public class SchedulerContributor {

    @PrePlanProcessor
    public Step generateWaitStep(DeltaSpecification specification) {
        final DeployedApplication deployedApplication = specification.getDeployedApplication();

        final String date = deployedApplication.getProperty("date");
        final String time = deployedApplication.getProperty("time");
        final String datePattern = deployedApplication.getProperty("datePattern");
        final String timePattern = deployedApplication.getProperty("timePattern");

        if (Strings.isNullOrEmpty(date) && Strings.isNullOrEmpty(time)) {
            return null;
        }
        if (Strings.isNullOrEmpty(date) || Strings.isNullOrEmpty(time)) {
            logger.warn("date {} or time {} is empty", date, time);
            return null;
        }

        final DateFormat dateFormat = new SimpleDateFormat(format("%s %s", datePattern, timePattern));
        try {
            final Date parsedDate = dateFormat.parse(format("%s %s", date, time));
            return new WaitUntilDateTimeStep(parsedDate);
        } catch (ParseException e) {
            throw Throwables.propagate(e);
        }
    }

    protected static final Logger logger = LoggerFactory.getLogger(SchedulerContributor.class);
}
