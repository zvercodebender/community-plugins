package ext.deployit.community.plugin.scheduler.step;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;

import static java.lang.String.format;

@SuppressWarnings("serial")
public class AtDateWaitStep implements Step {

    private static final long SECONDS_TO_SLEEP = 60 * 10;

    private final Date scheduledDate;
    private final String description;

    public AtDateWaitStep(final Date date) {
        this.scheduledDate = date;
        this.description = format("Execute task at '%s'", scheduledDate);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public StepExitCode execute(final ExecutionContext ctx) throws Exception {
        final long waitTimeInSeconds = (scheduledDate.getTime() - new Date().getTime()) / 1000L;
        if (waitTimeInSeconds < 0) {
            ctx.logOutput(String.format("%s has been already reached", scheduledDate));
            return StepExitCode.SUCCESS;
        }
        long sleepIntervals = waitTimeInSeconds / SECONDS_TO_SLEEP;
        long remainingSeconds = waitTimeInSeconds % SECONDS_TO_SLEEP;

        ctx.logOutput(description);
        try {
            long secondsCountDown = waitTimeInSeconds;
            for (int i = 1; i <= sleepIntervals; i++) {
                ctx.logOutput("Time remaining " + (secondsCountDown / 60) + " minutes.");
                Thread.sleep((SECONDS_TO_SLEEP * 1000));
                secondsCountDown -= SECONDS_TO_SLEEP;
            }
            if (remainingSeconds > 0) {
                ctx.logOutput("Time remaining " + (remainingSeconds / 60) + " minutes.");
                Thread.sleep((remainingSeconds * 1000));
            }
            ctx.logOutput("Wait complete.");
        } catch (InterruptedException ignored) {
            ctx.logOutput("Wait interupted.");
            Thread.currentThread().interrupt();
            return StepExitCode.FAIL;
        }
        return StepExitCode.SUCCESS;

    }

    protected static final Logger logger = LoggerFactory.getLogger(AtDateWaitStep.class);
}
