# Manual Step Plugin #

# Overview #

In some situations, it may not be possible to fully automate a deployment process. Manual processes have to be carried out at certain steps in the deployment. The Manual Step plugin allows for the insertion of manual steps into the deployment plan by means of configuration.  The manual step will pause the deployment, displaying instructions to the user, explaining what should take place at that moment in time. The instructions can also be automatically mailed to a responsible party.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.8+

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

# Configuring a Manual Step #

## SMTP Server ##

First, you will need to define an [SMTPServer](#mail.SMTPServer) under the *Infrastructure* root.

	mailServer = factory.configurationItem("Infrastructure/MailServer","mail.SMTPServer")
	mailServer.host = "smtp.mycompany.com"
	mailServer.username = "mymailuser"
	mailServer.password = "secret"
	mailServer.fromAddress="noreply@mycompany.com"
	repositort.create(mailServer)
	
The SMTPServer uses Java Mail to send emails. You can specify additional Java Mail properties in the *smtpProperties* attribute. Refer to http://javamail.kenai.com/nonav/javadocs/com/sun/mail/smtp/package-summary.html for a list of all properties.

## Manual Step Configuration Item ##

Manuals steps are associated with an environment. Depending on the type of deployment (initial deployment, undeployment, update or an unchanged deployment) taking place to the environment, an assoicated manual step will be inserted into the resulting step list.

Under the *Configuration* root, define a [ManualStep](#manualstep.ManualStep) configuration item. 

	step = factory.configurationItem("Configuration/NotifyStartOfDeployment", "manualstep.ManualStep")
	step.mailServer = "Infrastructure/MailServer"
	step.contributorType = "ONCE_AT_THE_START"
	step.description = "Inform monitoring department of start of ${deployedApplication.version.application.name} deployment."
	step.inlineScript="Turn monitoring for application ${deployedApplication.version.application.name}"
	
	#Optional sending of mail
	step.toAddresses = ["monitor@mycompany.com"]
	step.subject = "Deployment starting."
	repository.create(step)

The above step will be inserted for every deployment. Setting the *operation* property can control the step insertion depending on the deployment type.

* **CREATE**: An initial deployment.
* **MODIFY**: An update deployment.
* **DESTROY**: An undeployment.
* **NOOP**: An unchanged deployment.

The *description*, *subject*, *toAddresses*, *fromAddress* properties can contain FreeMarker template scripting. The following Deployit objects are available to the template :

* **deltas**: The delta specifications.
* **deployedApplication**: The entire deployed application containing application and environment configuration items.
* **previousDeployedApplication**: Only available when ContributorType is ONCE_AT_THE_START or ONCE_AT_THE_END.
* **operation**: The deployment operation.
* **step**: Contains a ManualStep object.


## Grouping Manual Steps and associating with an Environment ##

Once you have defined manual steps, you can group the steps using the [ManualSteps](#manualstep.ManualSteps) configuration item and then associate the [ManualSteps](#manualstep.ManualSteps) with an Environment via the *manualSteps* property.

	myManualSteps = factory.configurationItem("Configuration/OpsDepartmentNotifications","manualstep.ManualSteps")
	myManualSteps.steps = ["Configurations/NotifyStartOfDeployment"]
	repository.create(myManualSteps)
	env = repository.read("Environments/Dev")
	env.manualSteps = ["Configuration/OpsDepartmentNotifications"]
	repository.update(env)
	



