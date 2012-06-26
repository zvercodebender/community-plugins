# Plain Archive Converter #

This document describes the functionality provided by the Plain Archive Converter.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Plain Archive Converter is a Deployit CLI extension that converts plain ZIP files to Deployit-compatible Deployment ARchives (DARs) using configurable rules.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `CLI_HOME/plugins` directory. You will also need the following files in `CLI_HOME/lib`:

- commons-io-1.4.jar
- guava-r09.jar
- REMOVE google-collections-<version>.jar (superceded by Guava)
- truezip-kernel-<version>.jar
- truezip-file-<version>.jar
- truezip-driver-file-<version>.jar
- truezip-driver-zip-<version>.jar

The configuration file 'plain-archive-converter.properties' should be created/placed in `CLI_HOME/conf`.
	
# Configuration #

The configuration file defines "matching rules" which indicate which entries in the plain ZIP correspond to Configuration Item entries in the DAR's manifest. The general format of a rule specification is:

	# N is the sequence number of the rule in the configuration file - the first is 1 etc.
	rule.N.type=<type of matching rule>
	rule.N.returned.ci=<type of Configuration Item matched by this rule, e.g. 'War'>
	rule.N.ci.properties=<properties of the Configuration Item entry in the DAR, e.g. 'name=myapp'> (optional)
	rule.N.rule-specific-property=some-value
	...

The converter currently supports two rules: 'path' and 'regex'. The format for a 'path' rule is:

	rule.N.type=path
	rule.N.returned.ci=<see above>
	rule.N.path=<path within the archive of the Configuration Item, e.g. 'jee/sample.war'>
	rule.N.ci.properties=<see above> (optional)

The format for a 'regex' rule is:

	rule.N.type=regex
	rule.N.returned.ci=<see above>
	rule.N.pattern=<regex to match the path within the archive of the Configuration Item, e.g. 'jee/([A-Za-z]+).war'. Uses the Java regular expression syntax, so special characters should be escaped using *double* backslashes, e.g. '\\$' for a dollar sign. Parts of the path may be captured as matching groups>
	rule.N.ci.properties=<see above. Matching groups from the path match may be used, e.g. 'name=$1'> (optional)

The syntax for 'ci.properties' resembles a URI query string, also in terms of escaping, i.e. 'propName=value&propName2=value&...'.

# Usage

The CLI object is accessible as 'zipconverter' and supports the following methods:

	zipconverter.convert(sourcePath)
	zipconverter.convert(sourcePath, appName, version)

Attempts to convert the plain ZIP file at 'sourcePath'. The resulting DAR is written to a temporary file which is returned by the method.

	zipconverter.convert(sourcePath, targetPath)
	zipconverter.convert(sourcePath, appName, version, targetPath)

Attempts to convert the plain ZIP file at 'sourcePath'. The resulting DAR is written to a new file at 'targetPath'. The target file, which is returned by the method, should not exist before the method is called.

In both cases, if the 'appName' and 'version' are not specified, they are derived from the name of the input file. If the input file contains a '-', the section after the *last* '-' is treated as the version, otherwise the default version '1.0' is applied.

# Example

In the CLI, the Plain Archive Convertor is used as follows:

	> tempDar = zipconverter.convert('~/my/source/file.zip')
	> print tempDar.path // should print something like '/tmp/file034235524234-dar.zip'
	> dar = zipconverter.convert('~/my/source/file.zip', '~/my/target/file.dar')
	> print dar.path // should print '~/my/target/file.dar'

See [the test](https://github.com/xebialabs/community-plugins/blob/master/deployit-cli-plugins/plain-archive-converter/src/test/resources/plain-archive-converter.properties) for examples.
