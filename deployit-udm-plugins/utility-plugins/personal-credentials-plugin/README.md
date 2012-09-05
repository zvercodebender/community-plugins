# Personal Credentials plugin #

# Overview #

The personal credentials plugin allows you to specify overthere.Host credentials (username & password) that would be used only for a deployed application.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7.3+

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.

# Configuration #
The activation of the personal credential is trigggered per environment using the *overrideHostCredentials* propery.
Two kind of configuration are actually supported
	* unique credential for all hosts
	* per Os credential
	
## Single credential ##
This configuration allows the user to supply a username and a password on the deployed application level. This credential will be used each time Deployit needs to create a new remote connection to a a host during the execution of the deployment plan.

The synthetic.xml file need to be modified by adding this:

```
<?xml version="1.0" encoding="UTF-8"?>
<synthetic xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://www.xebialabs.com/deployit/synthetic"
           xsi:schemaLocation="http://www.xebialabs.com/deployit/synthetic synthetic.xsd">

    <type-modification type="udm.DeployedApplication">
        <property name="username" kind="string" transient="true" required="false" category="Personal Credentials"/>
        <property name="password" password="true" transient="true" required="false" category="Personal Credentials"/>
    </type-modification>

    <type-modification type="udm.Environment">
        <property name="overrideHostCredentials" kind="boolean" default="false" category="Personal Credentials"/>
        <property name="perOsCredential" kind="boolean" default="false" category="Personal Credentials" description="use different credentials for Unix and Windows Hosts." hidden="true"/>
    </type-modification>
</synthetic>
```

## Per OS credential ##
This configuration allows the user to supply a username and a password on the deployed application level. These credentials will be used each time Deployit needs to create a new remote connection during the execution of the deployment plan. If the host operating system is *Windows*, windowsUsername and windowsPassword will be used for username and password.
If the host operating system is *Unix*, unixUsername and unixPassword will be used for username and password.

The synthetic.xml file need to be modified by adding this:

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
    </type-modification>

    <type-modification type="udm.Environment">
        <property name="overrideHostCredentials" kind="boolean" default="false" category="Personal Credentials"/>
        <property name="perOsCredential" kind="boolean" default="true" category="Personal Credentials" description="use different credentials for Unix and Windows Hosts." hidden="true"/>
    </type-modification>
</synthetic>
```

Note: the transient attribute equals 'true' implies the values will not be saved during 2 deployment. If you want to make it persitent, set the attribute value to 'false'








