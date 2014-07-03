# Exmaple XL Deploy plugin
This plugin is based on the Yak example in the XL Delpoy Customization manual. It covers basic items for creating a XLD plugin in Java.

## Building the plugin
Build for the Yak plugin is base on Maven. To build execute "mvn package", this will produce a plugin JAR in the target/ directory.

Note, that by default the yak-plugin is dependent on the wider community-plugings project. In the standalone_pom directory there is an alternative POM which does not require the parents. This will however expect the XLD dependencies to be in the local Maven repo. IF these need to be added by hand the following is a guide, repeat for all dependencies in the pom.

mvn install:install-file -Dfile=/usr/local/xebia/xl-deploy-4.0.0-server/lib/udm-plugin-api-4.0.0.jar -DgroupId=com.xebialabs.deployit -DartifactId=udm-plugin-api -Dversion=4.0.0 -Dpackaging=jar

## Working with the plugin
This can be used as a base, or jumping off point, to create your own plugin. In this package working with basic CI items and steps is covered as well as contributors and their steps. Note that examples using XLD rules is beyond the scope of this module.
