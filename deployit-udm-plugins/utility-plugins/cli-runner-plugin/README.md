# Cli Runner plugin #

# Overview #

The cli runner plugin defines control task delegates that can be used to run cli scripts.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.9.1+

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

Before running a cli script control task, a _cli.Cli_ configuration item must first be created under the _Configuration_ node in the Deployit repository.  The _cli.Cli_ specifies the host and location of the cli installation.

## Control Tasks ##

### Cli Deployment ###

This plugin adds a _Cli Deployment_ method (control task) to _udm.Environment_.  When the control task is executed, the user can select serveral deployment packages to be sequentially deployed via the Cli.

### Run Script ###

This plugin adds a _Run Script_ method (control task) to _cli.Cli_.  When the control task is executed, the user can enter Cli commands that must be executed in the Cli.


## Control Task Delegates ##

### cliScript Delegate ###

A _cliScript_ delegate has the capability of executing a cli script. The delegate can be used in any configuration item in the Deployit repository.

<table class="ci-table">
    <tbody><tr class="odd ci-prop-header">
        <th>Argument</th>
        <th>Description</th>
    </tr>
        <tr class="even">
            <td>
                <div class="ci-property-info"><span class="ci-property-name">script</span><span>: </span>
                    <span class="ci-property-kind">STRING</span></div>
            </td>
            <td>
                <div class="ci-property-desc">Required. The classpath to the python cli script.</div>
            </td>
        </tr>
        <tr class="odd">
            <td>
                <div class="ci-property-info"><span class="ci-property-name">scriptDescription</span><span>: </span>
                    <span class="ci-property-kind">STRING</span></div>
            </td>
            <td>
                <div class="ci-property-desc">The description of the script as it appears in Deployit's control task step list.</div>
            </td>
        </tr>
        <tr class="even">
            <td>
                <div class="ci-property-info"><span class="ci-property-name">classpathResources</span><span>: </span>
                    <span class="ci-property-kind">LIST_OF_STRING</span></div>
            </td>
            <td>
                <div class="ci-property-desc">Comma separated string of additional classpath resources that should be uploaded to the working directory before executing the script.</div>
            </td>
        </tr>
        <tr class="odd">
            <td>
                <div class="ci-property-info">
                    <span class="ci-property-name">templateClasspathResources</span>
                    <span>: </span>
                    <span class="ci-property-kind">LIST_OF_STRING</span>
                </div>
            </td>
            <td>
                <div class="ci-property-desc">Comma separated string of additional template classpath resources that should be uploaded to the working directory before executing the script.The template is first rendered and the rendered content copied to a file, with the same name as the template, in the working directory.</div>
            </td>
        </tr>
        </tbody>
</table>

<table class="ci-table">
    <tbody><tr class="odd ci-prop-header">
        <th>Parameter</th>
        <th>Description</th>
    </tr>
        <tr class="even">
            <td>
                <div class="ci-property-info"><span class="ci-property-name">password</span><span>: </span>
                    <span class="ci-property-kind">STRING</span></div>
            </td>
            <td>
                <div class="ci-property-desc">Required. The password for the current logged in user name. These credentials will be used to log into the Cli.</div>
            </td>
        </tr>
        <tr class="odd">
            <td>
                <div class="ci-property-info"><span class="ci-property-name">…</span></div>
            </td>
            <td>
                <div class="ci-property-desc">Any other parameters that your script may need</div>
            </td>
        </tr>
                </tbody>
</table>

<table class="ci-table">
    <tbody><tr class="odd ci-prop-header">
        <th>Python Variable</th>
        <th>Description</th>
    </tr>
        <tr class="even">
            <td>
                <div class="ci-property-info"><span class="ci-property-name">thisCi</span><span>: </span>
                    <span class="ci-property-kind">CI</span></div>
            </td>
            <td>
                <div class="ci-property-desc">The configuration item on which the control task is executed.</div>
            </td>
        </tr>
        <tr class="odd">
            <td>
                <div class="ci-property-info"><span class="ci-property-name">params</span><span>: </span>
                    <span class="ci-property-kind">CI</span></div>
            </td>
            <td>
                <div class="ci-property-desc">The parameters defined for the control task.</div>
            </td>
        </tr>
        <tr class="even">
            <td>
                <div class="ci-property-info"><span class="ci-property-name">args</span><span>: </span>
                    <span class="ci-property-kind">String</span></div>
            </td>
            <td>
                <div class="ci-property-desc">The arguments defined for the control task.</div>
            </td>
        </tr>
    </tbody>
</table>

Example :

	<!-- synthetic.xml -->
	<type-modification type="overthere.Host">
	    <method name="echo" delegate="cliScript" script="sample/echo.py" scriptDescription="Pretty print">
            <parameters>
                <parameter name="cli" kind="ci" referenced-type="cli.Cli" />
                <parameter name="password" password="true"/>
            </parameters>
        </method>
	</type-modification>
	
    <!-- sample/echo.py -->
    deployit.print(repository.read(thisCi.id))
