package ext.deployit.community.plugin.manualstep.ci;


import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.api.udm.Property;
import com.xebialabs.deployit.plugin.api.udm.base.BaseConfigurationItem;
import com.xebialabs.deployit.plugin.mail.SMTPServer;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Metadata(root = Metadata.ConfigurationItemRoot.CONFIGURATION, description = "Manual Step configuration.")
public class ManualStep extends BaseConfigurationItem {

    @Property(description = "The type of contributor or processor used to insert the manual step")
    private ContributorType contributorType;

    @Property(required = false, description = "The operation that should trigger the insertion of the step. When empty, will trigger for all deployment operations.")
    private Operation operation;

    @Property(defaultValue = "1", description = "The order the step will appear in the step list.")
    private int order;

    @Property(description = "The description for the step in the step list.")
    private String description;

    @Property(required = false, description = "The inline template used to generate the instructions. When empty, scriptPath must be entered.")
    private String inlineScript;

    @Property(required = false, description = "The classpath to the template used to generate the instructions.")
    private String scriptPath;

    @Property(required = false, description = "Mail addresses of recepients.", category = "Mail")
    private List<String> toAddresses = newArrayList();

    @Property(required = false, description = "Mail subject", category = "Mail")
    private String subject;

    @Property(required = false, description = "From mail address. Defaults to SMTPServer fromAddress.", category = "Mail")
    private String fromAddress;

    @Property(required = false, description = "The mail server used to send the email.")
    private SMTPServer mailServer;


    public ContributorType getContributorType() {
        return contributorType;
    }

    public void setContributorType(ContributorType contributorType) {
        this.contributorType = contributorType;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getInlineScript() {
        return inlineScript;
    }

    public void setInlineScript(String inlineScript) {
        this.inlineScript = inlineScript;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(List<String> toAddresses) {
        this.toAddresses = toAddresses;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public SMTPServer getMailServer() {
        return mailServer;
    }

    public void setMailServer(SMTPServer mailServer) {
        this.mailServer = mailServer;
    }
}
