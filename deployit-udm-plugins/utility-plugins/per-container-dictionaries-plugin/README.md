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

The per-container dictionaries plugin supports replacement of placeholders in deployed items with values defined on the target container, as opposed to coming from a dictionary (or the user)

In order to retrieve a value from the deployed's container, the container must define a `dictionaryEntries` property (see [Configuration](#configuration)). If the placeholder's value is set to `<per-container>`, the value will be retrieved from the container's dictionary by looking for an entry with the matching key, as for regular environment dictionaries.
