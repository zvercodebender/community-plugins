# Notifications plugin #

# Overview #

The Notifications plugin is a Deployit plugin that supports notifications as part of a deployment. Currently, only notifications via email are supported.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

Also requires 'simple-java-mail-1.8.jar' in the `SERVER_HOME/lib` directory. This library is available from [Maven Central](http://search.maven.org/#search|ga|1|simple-java-mail-1.8).

## Upgrading to 3.7.0-3 ##

If upgrading to 3.7.0-3 from an older version, please add the following temporary type definition to `SERVER_HOME/ext/synthetic.xml`:

```xml
<type type="notify.SentTemplateEmail2" extends="notify.SentTemplateEmail" />
```

When starting the server after installing the upgrade, you will be prompted to run an upgrader. This will log a message indicating whether the type definition added in the previous step is required and needs to be retained (e.g. if the type is in use due to local customizations) or if it can be removed.

# Usage

The notification plugin supports sending arbitrary emails as part of the deployment sequence via `notify.EmailDraft` and `notify.TemplateEmailDraft` Deployables in packages. For template emails, a FreeMarker template with the name of the Deployed needs to be available in `SERVER_HOME/ext/notify/email/<type-name>.ftl` (e.g. `notify.BusinessApprovalRequest.ftl`).

Two additional standard options provided by the plugin are sending of "deployment start" and/or "deployment end" notifications. These are activated by checking the property `sendDeploymentStartNotification` (resp. `sendDeploymentEndNotification`) on the target environment.

# Configuration #

The plugin supports one or multiple SMTP server settings, which are specified by creating `notify.MailServer` configuration items under the "Infrastructure" root.

In order to send emails during deployments to an environment, precisely *one* `notify.MailServer` needs to be added to the environment. The settings for any start/end notification emails that need to be sent are specified by `notify.DeploymentStartNotificationPrototype` (respectively, `notify.DeploymentEndNotificationPrototype`) items created under the `notify.MailServer` included in the environment.

The `From`, `To`, `Cc` and `Bcc` properties expect email addresses to be specified as _address_ or _name &lt;address&gt;_. `To`, `Cc` and `Bcc` accept comma-separated lists of email addresses.