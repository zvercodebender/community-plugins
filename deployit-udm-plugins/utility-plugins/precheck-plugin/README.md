# Pre-Check plugin #

# Overview #

The Pre-Check plugin is a Deployit plugin that supports pre-check step generation.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 
	
# Configuration #

In order to be able to provide the check script, the container type must define a property `checkScript`, a script path relative to the classpath.
The plugin pre-defines these for `overthere.Host` and `generic.Container`.

To add a check script to addiitonal containers, add the following type modification for the desired containers to `SERVER_HOME/ext/synthetic.xml`:

	<type-modification type="...">
    	<property name="checkScript" kind="string" required="false" category="Pre-check" />
	</type-modification>

# Usage

The plugin provides a way to generate deployment steps that check whether the target container is ready.
The script uses the Freemarker syntax. The `container` CI and the `deployedApplication` CI are available to the check script.
