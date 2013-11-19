package ext.deployit.community.plugin.sla.contributor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import com.xebialabs.deployit.plugin.api.deployment.planning.PostPlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.planning.PrePlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.Environment;

import ext.deployit.community.plugin.sla.ci.FallbackMode;
import ext.deployit.community.plugin.sla.step.CheckSLAStep;

import static java.lang.String.format;

public class SLAContributor {

    @PrePlanProcessor
    public Step verifyEndSLA(DeltaSpecification specification) {
        final Environment environment = specification.getDeployedApplication().getEnvironment();
        final String slaEndsAtTime = environment.getProperty("slaEndsAtTime");

        if (Strings.isNullOrEmpty(slaEndsAtTime)) {
            return null;
        }

        final DateFormat dateFormat = new SimpleDateFormat(format("%s", environment.getProperty("timePattern")));
        try {
            Calendar upper = translate(slaEndsAtTime, dateFormat);
            return new CheckSLAStep.CheckEndSLAStep(upper.getTime(), environment.<FallbackMode>getProperty("endFallBackMode"));
        } catch (ParseException e) {
            throw Throwables.propagate(e);
        }
    }


    @PostPlanProcessor
    public Step verifyBeginSLA(DeltaSpecification specification) {
        final Environment environment = specification.getDeployedApplication().getEnvironment();
        final String slaStartsAtTime = environment.getProperty("slaStartsAtTime");

        if (Strings.isNullOrEmpty(slaStartsAtTime)) {
            return null;
        }

        final DateFormat dateFormat = new SimpleDateFormat(format("%s", environment.getProperty("timePattern")));
        try {
            Calendar lower = translate(slaStartsAtTime, dateFormat);
            return new CheckSLAStep.CheckBeginSLAStep(lower.getTime(), environment.<FallbackMode>getProperty("startFallBackMode"));
        } catch (ParseException e) {
            throw Throwables.propagate(e);
        }
    }


    protected static final Logger logger = LoggerFactory.getLogger(SLAContributor.class);

    private Calendar translate(final String slaTime, final DateFormat dateFormat) throws ParseException {
        Calendar slaCalendar = Calendar.getInstance();
        slaCalendar.setTime(dateFormat.parse(slaTime));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, slaCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, slaCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}
