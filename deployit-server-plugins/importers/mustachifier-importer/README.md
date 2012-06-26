# Mustachifier Importer #

This document describes the functionality provided by the Mustachifier Importer.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Mustachifier Importer is a Deployit server extension that transforms archive files during 'import phase' to Deployit-compatible Deployment ARchives (DARs) using configurable transforms.
Especially useful for converting placeholders in configuration files to Deployit's '{{...}}' format.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7
	
# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. You will also need the following files in `SERVER_HOME/lib`:

- mustachifier-&lt;version&gt;.jar

The configuration file 'mustachifier.properties' should be created/placed in `SERVER_HOME/conf`.

# Configuration #

The configuration file defines "transforms" which indicate which entries in the source DAR should be transformed. The general format of a transform specification is:

	# N is the sequence number of the rule in the configuration file - the first is 1 etc.
	transform.N.type=<type of the transform>
	transform.N.ci.type=<Configuration Item type to which the transform is applicable>
	transform.N.ci.path.pattern=<regex to match the path within the DAR of Configuration Items to which the transform is applicable. Optional - if no pattern is specified, the transform applies to *all* Configuration Items of the specified type. Uses the Java regular expression syntax, so special characters should be escaped using *double* backslashes, e.g. '\\$' for a dollar sign>
	transform.N.transform-specific-property=some-value
	...

The extension currently supports two transforms: 'string-replace' and 'regex-replace'. The format for a 'string-replace' transform is:

	transform.N.type=string-replace
	transform.N.ci.type=<see above>
	transform.N.ci.path.pattern=<see above> (optional)
	transform.N.encoding=<encoding of the source file(s) matched, e.g. 'ISO-8859-1'>
	transform.N.find=<the string to find, e.g. 'foo'>
	transform.N.replacement=<the string to use as a replacement, e.g. 'bar'>
	transform.N.textFileNamesRegex=<Regular expression that matches file names of text files, default is .+\.(cfg | conf | config | ini | properties | props | txt | xml )>

The format for a 'regex-replace' transform is:

	transform.N.type=regex-replace
	transform.N.ci.type=<see above>
	transform.N.ci.path.pattern=<see above> (optional)
	transform.N.encoding=<encoding of the source file(s) matched, e.g. 'ISO-8859-1'>
	transform.N.pattern=<the pattern to find, e.g. '\\$(\\{[^\\}]+\\})'. Uses the Java regular expression syntax, so special characters should be escaped using *double* backslashes, e.g. '\\$' for a dollar sign. Parts of the pattern may be captured as matching groups>
	transform.N.replacement=<the string to use as a replacement, e.g. '\\{$1\\}'. Matching groups from the pattern match may be used. Again, uses the Java regular expression syntax>
	transform.N.textFileNamesRegex=<Regular expression that matches file names of text files, default is .+\.(cfg | conf | config | ini | properties | props | txt | xml )>

# Usage

Place the archive to be imported in the importable package directory and import it using the GUI or CLI.

# Examples

See [the test](https://github.com/xebialabs/community-plugins/blob/master/deployit-cli-plugins/mustachifier/src/test/resources/mustachifier.properties) for examples.
