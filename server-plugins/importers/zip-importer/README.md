Description
===========

A Deployit Importer plugpoint that allows plain ZIP files to be imported as Deployment Packages.

Installation
============

Place the 'zip-importer-*version*.jar' file into your SERVER_HOME/plugins directory. Requires 'plain-archive-converter-*version*.jar' to be present in SERVER_HOME/plugins - if this is not part of your CLI configuration (see CLI_HOME/plugins) it can be built from [GitHub](https://github.com/demobox/plain-archive-converter).

The configuration file 'plain-archive-converter.properties' should be created/placed in SERVER_HOME/conf.

Configuration
=============

See the README for the plain-archive-converter at https://github.com/demobox/plain-archive-converter for configuration information and examples.
