# JEE Archive Importer #

This document describes the functionality provided by the JEE Archive Importer.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The JEE Archive Importer is a Deployit importer allowing to import plain EAR files as deployment packages.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7
	
# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

The configuration file 'jee-archive-importer.properties' should be created/placed in SERVER_HOME/conf.

# Configuration #

The main task of the importer is to derive, on the basis of the EAR or WAR file, the application name and version for the deployment package that will be created.

The default option (and fallback) is to attempt to extract the application name and version from the archive file name itself. This is controlled by the

<tt>
\# '&lt;ext&gt;' is 'ear' or 'war'<br />
jee-archive-importer.&lt;ext&gt;.nameVersionRegex
</tt>

property - if the name matches, the first matching group becomes the name and the second, if found, the version. If only the name can be matched the

<tt>jee-archive-importer.&lt;ext&gt;.defaultVersion</tt>

property determines the version given to the deployment package. If the property

<tt>jee-archive-importer.&lt;ext&gt;.scanManifest=true</tt>

is set, the importer will - before falling back to the file name - attempt to extract the application name and version from the archive's manifest. The properties

<tt>
jee-archive-importer.&lt;ext&gt;.nameManifestAttribute<br />
jee-archive-importer.&lt;ext&gt;.versionManifestAttribute
</tt>

determine the manifest attributes that should be read to determine the name (resp. version) of the deployment package.

# Usage

Place the EAR file in the importable package directory or import it via the CLI.

# Example #

See [the test](https://github.com/xebialabs/community-plugins/blob/master/importers/jee-archive-importer/src/test/resources/jee-archive-importer.properties) for examples.
