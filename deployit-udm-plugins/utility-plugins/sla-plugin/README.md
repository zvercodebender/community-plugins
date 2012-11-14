# SLA plugin #

# Overview #

The SLA plugin allows to avoid to trigger a deployment task during the SLA contracted by the target environment.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.8

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

A new category is added to the udm.Enviroment CI: 'SLA'. It offers a place to define the range in which all the deployment should not be executed. (begin/end)
The format used to defined the date and the time are configurable using the datePattern and timePattern hidden properties.
