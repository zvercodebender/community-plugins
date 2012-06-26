# Composite Application Importer #

This document describes the functionality provided by the Composite Application Importer.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Composite Application Importer is a Deployit importer allowing to define and create composite package based on a text descriptor. Note that
the members of the composite package must already be present in the Deployit repository and are not imported.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 
	
# Configuration #

Create a file with `.cad` extension (Composite Application Descriptor). It follows the Java property file syntax. The file should contain at least an application and a version.
Each package is defined using the following syntax:

	package.X.name=ApplicationName
	package.X.version=ApplicationVersion

where X is a number, starting by 1

# Usage

Put the CAD file in the importable package directory or import it using the CLI.

# Example

The following file defines the `PetCompositeApp/3.4` application based on 2 deployment packages, `PetClinic-ear/1.0` and `PetClinic-ear/2.0`:

	application=PetCompositeApp
	version=3.4

	package.1.name=PetClinic-ear
	package.1.version=1.0

	package.2.name=PetClinic-ear
	package.2.version=2.0
