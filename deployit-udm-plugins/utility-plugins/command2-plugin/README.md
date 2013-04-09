# Command2 plugin #

# Overview #

The Command2 plugin is an alternative to the standard Deployit [command plugin](http://docs.xebialabs.com/releases/latest/deployit/commandPluginManual.html) that supports commands and commands with resources and re-uses generic plugin replacement and templating functionality.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.

# Usage #

The Command2 plugin allows you to execute arbitrary sequences of commands during a deployment, optionally making use of additional files. The intention is that these should _only_ be used for actions that are required for a specific application or application version, such as running an app-specific post-installation script. If you are looking to configure "general" deployment logic in Deployit, such as e.g. how a configuration element needs to be deployed to [nginx](https://en.wikipedia.org/wiki/Nginx), please consider Deployit's [generic](http://docs.xebialabs.com/releases/latest/deployit/genericPluginManual.html), [Python](http://docs.xebialabs.com/releases/latest/deployit/pythonPluginManual.html) or PowerShell (unlikely for nginx) plugins instead. 

See the [customization manual](docs.xebialabs.com/releases/latest/deployit/customizationmanual.html) for more details.

The Command2 plugin defines two types of deployable items that you can add to your [deployment packages](http://docs.xebialabs.com/releases/latest/deployit/packagingmanual.html): [`cmd2.Command`](https://github.com/xebialabs/community-plugins/blob/master/deployit-udm-plugins/utility-plugins/command2-plugin/src/main/resources/synthetic.xml#L30) and [`cmd2.CommandFolder`](https://github.com/xebialabs/community-plugins/blob/master/deployit-udm-plugins/utility-plugins/command2-plugin/src/main/resources/synthetic.xml#L6). A `Command` simply defines a sequence of command-line commands to be executed; a `CommandFolder` allows you to additionally provide a folder of resources (such as utility scripts) that are temporarily required on the target system in order for the command-line commands to execute successfully. These resources will be removed from the target system once the commands have been executed.

The `createOrder` property specifies _when_ in the overall deployment sequence the commands need be executed. You can optionally also specify a sequence of "undo" commands (via the `undoCommand` property) and the associated order (via `destroyOrder`). These commands will be executed when the application is undeployed or rolled back.

If `alwaysRun` is set, the commands will also be executed during every upgrade. This would be appropriate for a command to e.g. flush an application cache or trigger a synchronization with a CDN.

The command-line commands are executed within a configurable "wrapper" [shell script](https://github.com/xebialabs/community-plugins/blob/master/deployit-udm-plugins/utility-plugins/command2-plugin/src/main/resources/synthetic.xml#L30) or [batch file](https://github.com/xebialabs/community-plugins/blob/master/deployit-udm-plugins/utility-plugins/command2-plugin/src/main/resources/cmd2/CommandRunner.bat.ftl), so they should conform to shell/batch command syntax. For example, to call multiple batch files, the [`CALL`](https://www.microsoft.com/resources/documentation/windows/xp/all/proddocs/en-us/call.mspx?mfr=true) command should be used:
```
REM CALL required to ensure execution continues *after* batch1 completes
CALL batch1.cmd
CALL batch2.cmd
```
Any temporary resources provided with `CommandFolder` will be uploaded into the same directory in which the commands will be executed.