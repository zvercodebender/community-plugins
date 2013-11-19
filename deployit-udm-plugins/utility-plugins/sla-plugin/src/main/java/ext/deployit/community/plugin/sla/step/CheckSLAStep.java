package ext.deployit.community.plugin.sla.step;

import java.util.Date;

import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;

import ext.deployit.community.plugin.sla.ci.FallbackMode;

public abstract class CheckSLAStep implements Step {

    static public class CheckEndSLAStep extends CheckSLAStep {
        public CheckEndSLAStep(final Date sla, FallbackMode fallbackMode) {
            super(sla, fallbackMode, "Check the end of the SLA %s");
        }

        @Override
        public boolean isSuccess() {
            return new Date().after(sla);
        }
    }

    static public class CheckBeginSLAStep extends CheckSLAStep {
        public CheckBeginSLAStep(final Date sla, FallbackMode fallbackMode) {
            super(sla, fallbackMode, "Check the begin of the SLA %s");
        }

        @Override
        public boolean isSuccess() {
            return new Date().before(sla);
        }

    }

    private static final long SECONDS_TO_SLEEP = 60 * 10;

    protected final Date sla;
    protected final FallbackMode mode;
    protected final String description;
    protected final int order = 2;

    private CheckSLAStep(final Date sla, FallbackMode fallbackMode, final String description) {
        this.sla = sla;
        this.mode = fallbackMode;
        this.description = description;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String getDescription() {
        return String.format(description, sla);
    }

    abstract public boolean isSuccess();

    @Override
    public StepExitCode execute(final ExecutionContext executionContext) throws Exception {
        if (isSuccess()) {
            executionContext.logOutput(String.format("End of SLA (%s) has been reached, go on ", sla));
            return StepExitCode.SUCCESS;
        }

        executionContext.logOutput(String.format("The SLA (%s) has been not reached", sla));
        switch (mode) {
            case PAUSE:
                executionContext.logOutput("Pause the task");
                return StepExitCode.PAUSE;
            case FAIL:
                executionContext.logOutput("Fail the task");
                return StepExitCode.FAIL;
            case CONTINUE:
                executionContext.logOutput("continue the task");
                return StepExitCode.SUCCESS;
            case WAIT:
                executionContext.logOutput("Wait for " + sla);
                final long waitTimeInSeconds = (sla.getTime() - new Date().getTime()) / 1000L;
                long sleepIntervals = waitTimeInSeconds / SECONDS_TO_SLEEP;
                long remainingSeconds = waitTimeInSeconds % SECONDS_TO_SLEEP;

                try {
                    long secondsCountDown = waitTimeInSeconds;
                    for (int i = 1; i <= sleepIntervals; i++) {
                        executionContext.logOutput("Time remaining " + (secondsCountDown / 60) + " minutes.");
                        Thread.sleep((SECONDS_TO_SLEEP * 1000));
                        secondsCountDown -= SECONDS_TO_SLEEP;
                    }
                    if (remainingSeconds > 0) {
                        executionContext.logOutput("Time remaining " + (remainingSeconds / 60) + " minutes.");
                        Thread.sleep((remainingSeconds * 1000));
                    }
                    executionContext.logOutput("Wait complete.");
                } catch (InterruptedException ignored) {
                    executionContext.logOutput("Wait interupted.");
                    Thread.currentThread().interrupt();
                    return StepExitCode.FAIL;
                }
                return StepExitCode.SUCCESS;

        }
        //should never be there.
        throw new RuntimeException("Invalid Fallback mode " + mode);
    }


}
