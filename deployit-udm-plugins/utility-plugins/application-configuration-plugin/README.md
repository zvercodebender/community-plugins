# Application Configuration Plugin #

This document describes the functionality provided by the Application Configuration Plugin.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The application-configuration-plugin is a Deployit plugin that defines the udm.ApplicationConfiguration, allowing to define a kind of dictionary in the deployment package.

##Features##

* udm.ApplicationConfiguration

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7+
	* **Other Deployit Plugins**: None

# Usage #

##  udm.ApplicationConfiguration

All the placeholders' entries in a deployed whose value is <app-conf> will be replaced by the one provided par the udm.Application packaged in the application.
An error is raised if a entry cannot be found in the package.

Sample Manifest:

Manifest-Version: 1.0
Deployit-Package-Format-Version: 1.3
CI-Application: MyApp
CI-Version: 1.0

Name: appConfig
CI-Type: udm.ApplicationConfiguration
CI-entries-INSTANCE_NAME: ABC
CI-entries-otherValue: 42

CI-entries-OTHER_KEY: KEY2
CI-entries-otherValue: value








