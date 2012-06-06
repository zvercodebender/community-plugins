## Description

A Deployit 3.7 plugin that supports pre-check step generation.

## Installation

Place the 'pre-check-plugin-&lt;version&gt;.jar' file into your SERVER_HOME/plugins directory.

## Configuration

In order to be able to provide the check script , the container type must define a property "checkScript", a script path relative to the classpath.
The plugin pre-defines these for `overthere.Host` and `generic.Container`,

In a next version, you could add this property to additional containers add the following type modification for the desired containers to SERVER_HOME/ext/synthetic.xml:

```xml
<type-modification type="...">
    <property name="checkScript" kind="string" required="false" category="Pre-check" />
</type-modification>
```

## Usage

The pre-check plugin provides a way to generate pre-steps allowing to check the target container is ready.
The script uses the freemarker syntax. The 'container' CI and the 'deployedApplication' CI are injected.
