# Repository Migration 

# Overview #

This script allows to migrate the Deployit Repository backend. The typical use case is to migrate from the default Deployit configuration (using Derby & FileSystem) to another backend (eg Full database storage).

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.9
	
# Installation #

1. Unzip the content of the distribution zip file (deployit-repository-migration-${version}-distribution.zip) in your deployit server installation.
    * the `bin` directory should include now 2 new files migrate.sh & migrate.cmd
    * the `lib` directory should include now the repository-migration-${version}.jar
    * a new `sample` directory
2. If necessary, copy the jdbc drivers to the lib/ directory

# Execution

1. Configure the new target repository structure (jackrabbit-repository.xml) see the documentation [System Administartion Manual](http://docs.xebialabs.com/releases/3.9/deployit/systemadminmanual.html#using-a-database)
2. Run the migration script

`bin/migrate.sh  -deployitHome <Deployit-Server-Home> -jackrabbit-config-file <Path-to-new-configuration-file> -repository-name <Name> -updateDeployitConfiguration`

Exemple
`bin/migrate.sh  -deployitHome /opt/deployit/deployit-3.9.4-server -jackrabbit-config-file ./bin/jackrabbit-mysql-repository.xml  -repository-name migration-to-mysql -updateDeployitConfiguration`

Once the script has been successful execution, you should have a new folder in the deployit-server-home directory having the <Name>. The `-updateDeployitConfiguration` flag updates the deployit.conf configuration file and copy the new jackrabbit configuration file to the conf/ directory. (previous jackrabbit configuration file is backed up)