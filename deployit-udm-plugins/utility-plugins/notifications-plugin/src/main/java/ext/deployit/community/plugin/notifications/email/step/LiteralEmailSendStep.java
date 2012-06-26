package ext.deployit.community.plugin.notifications.email.step;

import java.util.List;

import ext.deployit.community.plugin.notifications.email.ci.MailServer;

@SuppressWarnings("serial")
public class LiteralEmailSendStep extends EmailSendStep {
    private final String body;
    
    public LiteralEmailSendStep(int order, String description, MailServer mailServer, String fromAddress, 
            List<String> toAddresses, List<String> ccAddresses, List<String> bccAddresses, 
            String subject, String body) {
        super(order, description, mailServer, fromAddress, toAddresses, ccAddresses, 
                bccAddresses, subject);
        this.body = body;
    }

    @Override
    protected String getBody() {
        return body;
    }

}
