# SSIS plugin #

This document describes the functionality provided by the SSIS plugin.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #


##Features##

* Deploys SSIS (dtsx packages) to an MSSQLClient container
* Compatible with SQL Server 2005 and up

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.9+
	* requires the database plugin (see `DEPLOYIT_SERVER_HOME/available-plugins`)

# Installation

Place the plugin JAR file into your `DEPLOYIT_SERVER_HOME/plugins` directory.

# Usage #

An SSIS package is bundled in a single dtsx file. The deployable name is mssql.ISPackage and requires a sql.msSqlClient as it's container due to the dependency on MS SQL Server.

The plugin copies the provided dtsx file (the artifact) to the container's server. From there is determines the version of SQL Server and uses that to determine the location of the dtutil utility.
The dtsx package is deployed using the SQL Server dtutil utility to the provided server and path.