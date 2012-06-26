# Deployment Test 2 plugin #

# Overview #

The Deployment Tests 2 plugin is a plugin that supports post-deployment tests.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 
	
On Windows hosts, the plugin will by default use a version of `wget` included in the plugin. If you wish to use a _different_ `wget` that is _already present_ on the path of your target systems you can simply prevent the included version from being uploaded by modifying `SERVER_HOME/conf/deployit-defaults.properties` as follows:

	# Classpath Resources
	# tests2.ExecutedHttpRequestTest.classpathResources=tests2/runtime/wget.exe

to

	# Classpath Resources
	tests2.ExecutedHttpRequestTest.classpathResources=
