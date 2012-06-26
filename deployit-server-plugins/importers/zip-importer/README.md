# Zip Importer #

This document describes the functionality provided by the Zip Importer.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Zip Importer is a Deployit server extension that allows importing of plain ZIP files as Deployment ARchives (DAR files). It uses the `plain-archive-converter` to convert the ZIP file into a DAR.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7
	
# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. You will also need the [plain-archive-converter](https://github.com/xebialabs/community-plugins/blob/master/deployit-cli-plugins/plain-archive-converter) in `SERVER_HOME/plugins`.

The configuration file 'plain-archive-converter.properties' should be created/placed in SERVER_HOME/conf.

# Configuration #

See the README for the [plain-archive-converter](https://github.com/xebialabs/community-plugins/blob/master/deployit-cli-plugins/plain-archive-converter) for more information.
