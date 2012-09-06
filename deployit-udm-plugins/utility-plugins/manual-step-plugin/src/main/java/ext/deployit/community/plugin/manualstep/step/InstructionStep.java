package ext.deployit.community.plugin.manualstep.step;


import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.flow.StepExitCode;
import com.xebialabs.deployit.plugin.generic.freemarker.ConfigurationHolder;
import ext.deployit.community.plugin.manualstep.ci.ManualStep;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.xebialabs.deployit.plugin.generic.freemarker.ConfigurationHolder.resolveExpression;

public class InstructionStep implements Step {

    private ManualStep stepConfig;
    private Map<String, Object> vars;
    private boolean paused;

    public InstructionStep(ManualStep stepConfig, Map<String, Object> vars) {
        this.stepConfig = stepConfig;
        this.vars = vars;
    }

    @Override
    public int getOrder() {
        return stepConfig.getOrder();
    }

    @Override
    public String getDescription() {
        return resolveExpression(stepConfig.getDescription(),vars);
    }

    @Override
    public StepExitCode execute(ExecutionContext ctx) throws Exception {
        String instructions = renderInstructionsTemplate();
        if (paused) {
            ctx.logOutput(instructions);
            return StepExitCode.SUCCESS;
        }
        mailInstructions(instructions, ctx);
        ctx.logOutput(instructions);
        paused = true;
        return StepExitCode.PAUSE;
    }

    private String renderInstructionsTemplate() throws IOException, TemplateException {
        Configuration cfg = ConfigurationHolder.getConfiguration();
        Template loadedTemplate;
        if (isNullOrEmpty(stepConfig.getInstructions()) && !isNullOrEmpty(stepConfig.getInstructions())) {
            loadedTemplate = cfg.getTemplate(stepConfig.getInstructionsScriptPath());
        } else {
            Preconditions.checkNotNull(Strings.emptyToNull(stepConfig.getInstructions()),"Either instructions or instructionsScriptPath must be specified.");
            loadedTemplate = new Template("name", new StringReader(stepConfig.getInstructions()),cfg);
        }
        StringWriter sw = new StringWriter();
        loadedTemplate.process(vars, sw);
        return sw.toString();
    }

    private void mailInstructions(String instructions, ExecutionContext ctx) {
        if (!stepConfig.getToAddresses().isEmpty() && stepConfig.getMailServer() != null) {
            List<String> resolvedAddresses = resolveExpression(stepConfig.getToAddresses(), vars);
            ctx.logOutput("Mailing instructions to " + Joiner.on(',').join(resolvedAddresses));
            String resolvedFromAddress = resolveExpression(stepConfig.getFromAddress(),vars);
            String resolvedSubject = resolveExpression(stepConfig.getSubject(),vars);
            resolvedSubject = isNullOrEmpty(resolvedSubject) ? getDescription() : resolvedSubject;
            try {
                stepConfig.getMailServer().sendMessage(resolvedSubject, instructions, resolvedAddresses, resolvedFromAddress);
            } catch (MessagingException e) {
                ctx.logError("Failed to send mail.",e);
                ctx.logOutput(Strings.repeat("-",50));
            }
        }
    }
}
