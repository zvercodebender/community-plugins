# Alias on Host plugin #

# Overview #

The alias on host plugin allows you to define the credentials information in a new type, overthere.Alias, in the Configuration node.
Two new types (overthere.AliasSshHost and overthere.AliasCifsHost) provides a new attributes 'alias' and hide the username and password attributes from overthere.SsHost and overthere.CifsHost.
# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.8+

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.

# Configuration #

The 'checkConnection' property on the udm.DeployedApplication type offers to generate CheckConnection Step on all the alias hosts..

![Configuration] (/img/alias.png)


![Configured Host] (/img/aliasHost.png)


