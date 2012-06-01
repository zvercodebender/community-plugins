## Description

A Deployit 3.7 plugin that supports per-container placeholder values

## Installation

Place the 'per-container-dictionaries-plugin-&lt;version&gt;.jar' file into your SERVER_HOME/plugins directory.

<a name="configuration" />## Configuration

In order to be able to store values for a container, the container type must define a `map_string_string` property called "dictionaryEntries". The plugin pre-defines these for `overthere.Host` and `generic.Container`, to add this property to additional containers add the following type modification for the desired containers to SERVER_HOME/ext/synthetic.xml:

```xml
<type-modification type="...">
    <property name="dictionaryEntries" kind="map_string_string" required="false" label="Entries" category="Dictionary" />
</type-modification>
```

## Usage

The per-container dictionaries plugin supports replacement of placeholders in deployed items with values defined on the target container, as opposed to coming from a dictionary (or the user)

In order to retrieve a value from the deployed's container, the container must define a "dictionaryEntries" property (see [Configuration](#configuration)). If the placeholder's value is set to `&lt;per-container&gt`, the value will be retrieved from the container's dictionary by looking for an entry with the matching key, as for regular environment dictionaries.
