# Single File Importer #

This document describes the functionality provided by the Single File Importer.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Single File Importer is a Deployit "abstract" importer that makes it easy to define importers to create packages from single files.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

The configuration file 'single-file-importer.properties' should be created/placed in `SERVER_HOME/conf`.
	
# Example

See [jee-archive-importer](https://github.com/xebialabs/community-plugins/blob/master/deployit-server-plugins/importers/jee-archive-importer) for examples.
