# Personal Credentials plugin #

# Overview #

The personal credentials plugin allows you to specify overthere.Host credentials (username & password) that are used only for a particular deployment.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7.3+

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.

# Configuration #

The activation of the personal credential is triggered per environment using the *overrideHostCredentials* property.
Two kinds of configuration are actually supported:
	* unique credentials for all hosts
	* per OS credential
	
## Single credential ##

This configuration allows the user to supply a username and a password on the deployed application level. These credentials will be used each time Deployit needs to create a new remote connection to a host during the execution of the deployment plan.

Enable single personal credentials by adding the following in the synthetic.xml:

```
<?xml version="1.0" encoding="UTF-8"?>
<synthetic xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://www.xebialabs.com/deployit/synthetic"
           xsi:schemaLocation="http://www.xebialabs.com/deployit/synthetic synthetic.xsd">

    <type-modification type="udm.DeployedApplication">
        <property name="username" kind="string" transient="true" required="false" category="Personal Credentials"/>
        <property name="password" password="true" transient="true" required="false" category="Personal Credentials"/>
        <property name="checkConnection" kind="boolean" default="true" required="false" category="Personal Credentials" />
    </type-modification>

    <type-modification type="udm.Environment">
        <property name="overrideHostCredentials" kind="boolean" default="false" category="Personal Credentials"/>
    </type-modification>
</synthetic>
```

## Per OS credentials ##

This configuration allows the user to supply a username and a password on the deployed application level.
These credentials will be used each time Deployit needs to create a new remote connection during the execution of the deployment plan.
If the host operating system is *Windows*, _windowsUsername_ and _windowsPassword_ will be used for username and password.
If the host operating system is *Unix*, _unixUsername_ and _unixPassword_ will be used for username and password.

To enable per OS credentials, modify your synthetic.xml file in the following way:

```
<?xml version="1.0" encoding="UTF-8"?>
<synthetic xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://www.xebialabs.com/deployit/synthetic"
           xsi:schemaLocation="http://www.xebialabs.com/deployit/synthetic synthetic.xsd">

    <type-modification type="udm.DeployedApplication">
        <property name="unixUsername" kind="string" transient="true" required="false" category="Personal Credentials"/>
        <property name="unixPassword" password="true" transient="true" required="false" category="Personal Credentials"/>
        <property name="windowsUsername" kind="string" transient="true" required="false" category="Personal Credentials"/>
        <property name="windowsPassword" password="true" transient="true" required="false" category="Personal Credentials"/>
        <property name="checkConnection" kind="boolean" default="true" required="false" category="Personal Credentials" />
    </type-modification>

    <type-modification type="udm.Environment">
        <property name="overrideHostCredentials" kind="boolean" default="false" category="Personal Credentials"/>
    </type-modification>
</synthetic>
```

The 'checkConnection' property allows to generate CheckConnection Step on all the hosts involved in the personal-credentials process.
Note: the transient attribute equals 'true' implies the values will not be persisted after the deployment. If you want to make it persistent, set the attribute value to 'false'.

