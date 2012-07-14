# Notifications plugin #

# Overview #

The Notifications plugin is a Deployit plugin that supports notifications as part of a deployment. Currently, only notifications via email are supported.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

Also requires 'simple-java-mail-1.8.jar' in the `SERVER_HOME/lib` directory. This library is available from [Maven Central](http://search.maven.org/#search|ga|1|simple-java-mail-1.8).

# Usage

The notification plugin supports sending arbitrary emails as part of the deployment sequence via `notify.EmailDraft` and `notify.TemplateEmailDraft` Deployables in packages. For template emails, a FreeMarker template with the name of the Deployed needs to be available in `SERVER_HOME/ext/notify/email/<type-name>.ftl` (e.g. `notify.BusinessApprovalRequest.ftl`).

Two additional standard options provided by the plugin are sending of "deployment start" and/or "deployment end" notifications. These are activated by checking the property `sendDeploymentStartNotification` (resp. `sendDeploymentEndNotification`) on the target environment.

# Configuration #

The plugin supports one or multiple SMTP server settings, which are specified by creating `notify.MailServer` configuration items under the "Infrastructure" root.

In order to send emails during deployments to an environment, precisely *one* `notify.MailServer` needs to be added to the environment. The settings for any start/end notification emails that need to be sent are specified by `notify.DeploymentStartNotificationPrototype` (respectively, `notify.DeploymentEndNotificationPrototype`) items created under the `notify.MailServer` included in the environment.

The `From`, `To`, `Cc` and `Bcc` properties expect email addresses to be specified as "address" or "name <address>". `To`, `Cc` and `Bcc` accept comma-separated lists of email addresses.