# Loadbalancer plugin #

This document describes the functionality provided by the Loadbalancer plugin.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Loadbalancer plugin is a Deployit plugin that provides the framework required to perform HA deployments in an environment that contains a loadbalancer. 

Currently, the loadbalancer plugin provides add and remove scripts for the F5 BIG-IP loadbalancer.

##Features##

* Add a loadbalancer container to your environment
* Specify the scripts to use to add or remove a container from the loadbalancer pool
* Add or remove webservers from the loadbalancer pool during a deployment
* Add or remove appservers from the loadbalancer pool during a deployment

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7+
* **BIG-IP requirements**
	* **API**: pycontrol Python library, version 2.0 or up
	* **SOAP**: suds Python library, version 0.3.9 or up

# Installation

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.

For BIG-IP support, install the pycontrol and suds libraries on the host from which to connect to the BIG-IP loadbalancer.

# Usage #

The plugin provides an lb.BigIpLoadbalancer CI that can be added to your environment. The loadbalancer CI is associated with a host from which the
BIG-IP loadbalancer is reachable. Configure the loadbalancer CI with the BIG-IP's hostname, username and password, as well as the active partition to use. 
The loadbalancer CI has a pool of web servers and app servers that it manages.

When a deployment is performed that installs or de-installs application components to/from one of the web servers or app servers that the loadbalancer
manages, Deployit will include steps to remove the server from the active pool when the installation process starts and return it to the pool when it
is finished.

# Note #

The plugin also contains a base CI type, lb.GenericLoadBalancer, that can be extended to support different loadbalancer middleware.

