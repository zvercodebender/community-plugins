# Change Management Plugin #

This document describes the functionality provided by the Change Management plugin.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Change Management plugin supports validation of Change Tickets in an ITIL Change Management system before deployment.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

The Change Management plugin requires the deployment-checklist-plugin (ships with Deployit in the `SERVER_HOME/plugins` directory)
	
# Configuration #

The Change Management plugin uses a hidden deployment checklist property defined on a deployment package (`udm.Version`). Its value is calculated during the planning phase and is not intended to be set by users.

The property needs to be hidden and boolean, and defined/added to your existing deployment checklist properties in your `SERVER_HOME/ext/synthetic.xml` file:

    <type-modification type="udm.Version">
      <!-- will be set automatically during deployment planning, not by users -->
      <property name="satisfiesChangeTicket" kind="boolean" hidden="true" required="false" default="false" category="Deployment Checklist" />
    </type-modification>

The complementary property that determines whether an environment requires change tickets for deployments is configured as follows (again in `SERVER_HOME/ext/synthetic.xml`):

    <type-modification type="udm.Environment">
      <property name="requiresChangeTicket" kind="boolean" required="false" default="false" category="Deployment Checklist" />
    </type-modification>

If the default suffix `ChangeTicket` clashes with an existing release authorization property, it can be changed by modifying the value of `chg.ChangeManager.changeTicketChecklistItemSuffix` in `SERVER_HOME/conf/deployit-defaults.properties`.

# Usage

The change-mgmt-plugin adds the ability to include Change Requests (`chg.ChangeRequests`) in deployment packages, which become Change Tickets (`chg.ChangeTicket`) when deployed to change managers (`chg.ChangeManager`), representing a system that tracks change tickets such as Service Desk, in an environment.

If the deployment checklist item `requiresChangeTicket` is set for an environment, deployments that do *not* create or update a `chg.ChangeTicket` will result in a validation error. Since Change Tickets are validated against `chg.ChangeManager` containers, there needs to be at least one such container in any environment for which `requiresChangeTicket` is set.

It is recommended to include a blank `chg.ChangeRequest` of the same name in each deployment package, and to set or modify, as appropriate, the `requestId` property of the `chg.ChangeTicket` resulting from the request.

# Integration with JIRA

The change-mgmt-plugin contains support for integrating with a JIRA change manager via the JIRA [REST API](http://docs.atlassian.com/jira/REST/latest/). The JIRA change manager (`chg.JiraChangeManager`) must be present in the target environment and configured appropriately for accessing your JIRA installation.

## Prerequisites

The JIRA integration makes use of the Python [requests](http://docs.python-requests.org/en/latest/user/quickstart/) library to access the JIRA REST API. Python and the requests library must be installed on the system that hosts the JIRA change manager.

