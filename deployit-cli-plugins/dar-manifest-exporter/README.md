# Manifest Exporter #

This document describes the functionality provided by the Manifest Exporter.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Manifest Exporter is a Deployit CLI extension that can export a MANIFEST.MF for an application in Deployit's repository. This can be combined with artifacts for the application (the exporter cannot export artifacts) to create a new DAR. 
Especially useful for preparing complex configuration for an application using the UI and then exporting the resulting manifest for use by e.g. developers.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `CLI_HOME/plugins` directory.

# Usage

Start the CLI and invoke the `manifestexporter.export` function. The manifest file will be written to `META-INF/MANIFEST.MF` under the target directory specified. Any warnings or errors encountered during export are written to an `export.log` file in the target directory.

# Examples

`manifestexporter.export('Applications/PetClinic-war/1.0', '/my/target/directory')`

will export the manifest for PetClinic-war/1.0 to `/my/target/directory/META-INF/MANIFEST.MF`. Errors or warnings encountered will be listed in `/my/target/directory/export.log`