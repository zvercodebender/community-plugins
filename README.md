# Deployit Community Plugins #

Welcome to the Deployit community plugin repository on github!

Here you will find plugins that can be used to add capabilities to your existing [Deployit](http://www.xebialabs.com/tour) installation. The source code
for the plugins is also provided if you are curious to see how a plugin works or want to improve it.

_Note: the plugins here are provided on an as-is basis as a service to Deployit users. Support for these plugins is provided through
XebiaLabs' professional services department._

# Finding a plugin

All plugins are located in directories inside the main community-plugins repository. Browse the main directory to find
the list of plugins and navigate into a plugin directory to find the plugin's documentation and source code.

## deployit-udm-plugins

This directory contains Deployit plugins that are based on the Unified Deployment Model.

## deployit-server-plugins

This directory contains Deployit server plugins that influence server behavior.

## deployit-cli-plugins

This directory contains Deployit CLI plugins that influence CLI behavior.

## tool-deployit-plugins

This directory contains plugins for external tools that interact with Deployit.

## example-plugins

This directory contains example plugins which can be used for tutorials and also a starting off point for new plugins

# Downloading a plugin

You can find compiled, ready-to-install binaries in the [_Downloads_](http://tech.xebialabs.com/download/community-archive/) location. If you want, you can clone
or fork the repository and compile the plugin from the source code.

# Installing a plugin

Installing a plugin is as simple as copying the plugin jar file into the _plugins_ directory in your Deployit server
installation directory. See Deployit's *System Administration Manual* for more information.

# Building community plugins
Here are the steps you need to build plugins under the community plugins repository
 
1.  Checkout the community repo [https://github.com/xebialabs/community-plugins](https://github.com/xebialabs/community-plugins)
2.  Make sure you have java 1.7 and maven 
3.  Make sure maven is also using java 1.7 ( since otherwise it would have issues while building) 
4.  Export an environment variable called **DEPLOYIT_HOME** and point it the deployit home directory on your machine
5.  Export an environment variable called **DEPLOYIT_CLI_HOME** and point it the CLI home directory on your machine
6.  You would also need to ensure that you have weblogic plugin in your **DEPLOYIT_HOME/plugins** since one of the liferay plugin requires that. ( Otherwise you may just skip that plugin from build)
7.  Now once you do all the above, you can run mvn package or mvn clean install to build all the plugins. 
8.  You will find all the built plugins under each one’s target folder.

**NOTE:** If you using mac , for Step 3 you may need to ensure the version for java and java version used by maven using these commands 

Change java version on mac :  **export JAVA_HOME=\`/usr/libexec/java_home -v 1.7\`**
<br>Change maven’s java version on mac : **echo JAVA_HOME=\`/usr/libexec/java_home -v 1.7\` | sudo tee -a /etc/mavenrc**

# Contributing to a plugin

If you want to add features to a plugin, fix a bug or otherwise contribute, great! Fork the community-plugins repository,
make your changes, test them and submit a pull request to us so we can incorporate your changes and make them available
to other Deployit users. 

# Discussing plugins

The [Deployit user forum](http://support.xebialabs.com/forums/20273366-deployit-users) is a place where you can discuss these community plugins (or other Deployit installation and configuration topics) with fellow Deployit users.


# Enjoy!

We hope the plugins provided here will help you make the most of Deployit. Please let us know what you think by posting a 
message on our support site at http://support.xebialabs.com.

Sincerely,

XebiaLabs
