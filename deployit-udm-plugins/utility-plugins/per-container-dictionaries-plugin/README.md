# Per-Container Dictionary plugin

# Overview #

The Per-Container Dictionary plugin is a Deployit  plugin that supports per-container placeholder values.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 
	
# Configuration<a name="configuration"/>

In order to be able to store values for a container, the container type must define a property "dictionary" that refers to a `udm.Dictionary`. The plugin pre-defines these for `overthere.Host` and `generic.Container`, to add this property to additional containers add the following type modification for the desired containers to SERVER_HOME/ext/synthetic.xml:

	<type-modification type="...">
    	<property name="dictionary" kind="ci" referenced-type="udm.Dictionary" required="false" category="Dictionary" />
	</type-modification>

# Usage

The per-container dictionaries plugin supports replacement of placeholders in deployed artifacts with values defined on the target container, as opposed to coming from a dictionary (or the user).

The per-container-dictionaries-plugin _only_ works for placeholders in files. Technically speaking, the plugin looks for 'DerivedArtificats'. These could be EAR files, WAR files, but _not_ resources like datasources or queues. Furthermore, only keys _inside_ the files are replaced. The plugin does not support placeholder replacement of CI-level properties.

In order to retrieve a value from the deployed's container, the container must define a `dictionary` property (see [Configuration](#configuration)). If the placeholder's value is set to `<per-container>`, the value will be retrieved from the container's dictionary by looking for an entry with the matching key, as for regular environment dictionaries.

So the way this works is:

1. Extend the CI type of the container in synthetic.xml (see above).

2. Define the placeholders in your artifact. For example, the EAR file. The placeholders refer to values inside the artifact.

3. Define a dictionary for your environment, say ENV_DICT, that contains the key you just defined and the value '<per-container>'. Link this dictionary to your environment.

4. Define a dictionary for your container, say CONTAINER_DICT, that contains the key you just defined and the value you want it to be on that containers. Link this dictionary to your container.

5. Placeholder values are resolved when the plan is generated. So from the UI's point of view, this is done when you press Next and are taken to the Step list screen before deployment.