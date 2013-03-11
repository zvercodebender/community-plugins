<#assign options = ""> 
<#if deployed.container.liquibaseConfigurationPath??>
  <#assign options = options + " --defaultsFile=${deployed.container.liquibaseConfigurationPath}"> 
</#if>
<#if deployed.container.driverClasspath??>
  <#assign options = options + " --classpath=${deployed.container.driverClasspath}"> 
</#if>
cd "${step.uploadedArtifactPath}"
${deployed.container.javaCmd} -jar ${deployed.container.liquibaseJarPath} ${options} --changeLogFile="${deployed.changeLogFile}" updateSQL
if %errorlevel%==-1 goto :done
${deployed.container.javaCmd} -jar ${deployed.container.liquibaseJarPath} ${options} --changeLogFile="${deployed.changeLogFile}" update
:done