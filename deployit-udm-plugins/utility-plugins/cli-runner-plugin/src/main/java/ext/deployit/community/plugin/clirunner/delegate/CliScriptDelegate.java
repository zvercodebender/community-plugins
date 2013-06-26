package ext.deployit.community.plugin.clirunner.delegate;

import java.util.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.services.Repository;
import com.xebialabs.deployit.plugin.api.udm.*;
import com.xebialabs.deployit.plugin.generic.step.ScriptExecutionStep;
import com.xebialabs.deployit.plugin.overthere.HostContainer;
import com.xebialabs.deployit.plugin.python.PythonVarsConverter;
import com.xebialabs.deployit.plugin.remoting.preview.PreviewOverthereConnection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.xebialabs.deployit.plugin.generic.freemarker.ConfigurationHolder.RESOLVED_STRING_COLLECTION_SPLITTER;
import static com.xebialabs.deployit.plugin.generic.freemarker.ConfigurationHolder.resolveExpression;
import static java.lang.String.format;

public class CliScriptDelegate {

    @Delegate(name = "cliScript")
    public static List<Step> executeCliScriptDelegate(ConfigurationItem item, String name, Map<String, String> args, Parameters params) {

        ConfigurationItem cli = findCliConfigurationItem();
        HostContainer cliHost = cli.getProperty("host");

        Map<String, Object> thisVarContext = createDefaultContext(item, args, params, cliHost, cli);

        String script = args.get("script");
        checkArgument(!isNullOrEmpty(script), "Argument [script] is required.");

        String desc = args.get("scriptDescription");
        if (isNullOrEmpty(desc)) {
            desc = "Executing cli script " + script;

        }

        Step step = createStep(script,desc, cliHost, args.get("classpathResources"), args.get("templateClasspathResources"), thisVarContext);

        return Collections.<Step>singletonList(step);

    }

    private static ConfigurationItem findCliConfigurationItem() {
        Repository repository = RepositoryHolder.getRepository();
        List<ConfigurationItem> cli = repository.search(Type.valueOf("cli.Cli"));
        checkArgument(!cli.isEmpty(),"Not cli.Cli configuration item found in repository. Please define a cli.Cli under the Configuration Node.");
        return cli.get(0);
    }

    private static String getAuthenticatedUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    private static Step createStep(String script, String desc, HostContainer host, String classpathResources, String templateClasspathResources, Map<String,Object> thisVarContext) {
        //String targetFile = ScriptExecutionStep.resolveOsSpecificFileName("cli/cli_runner",host);
        addScriptNameToContext(script, thisVarContext, host);
        String s = PythonVarsConverter.javaToPython(PreviewOverthereConnection.getPreviewConnection(), thisVarContext, false);
        thisVarContext.put("pythonVars",s);
        ScriptExecutionStep step = new ScriptExecutionStep(50, "cli/runner/cli_runner", host, thisVarContext, desc);
        if (!isNullOrEmpty(classpathResources)) {
            Iterable<String> resources = RESOLVED_STRING_COLLECTION_SPLITTER.split(classpathResources);
            step.setClasspathResources(resolveExpression(newArrayList(resources), thisVarContext));
        }

        if (!isNullOrEmpty(templateClasspathResources)) {
            Iterable<String> resources = RESOLVED_STRING_COLLECTION_SPLITTER.split(templateClasspathResources);
            step.setTemplateClasspathResources(resolveExpression(newArrayList(resources), thisVarContext));
        }

        step.getTemplateClasspathResources().add(script);
        step.getTemplateClasspathResources().add("cli/runner/wrapperscript.py");

        return step;
    }

    private static void addScriptNameToContext(String script, Map<String,Object> thisVarContext, HostContainer host) {
        String scriptPathAndName = resolveExpression(script, thisVarContext);
        char pathSeparator = host.getHost().getOs().getFileSeparatorChar();
        int index = scriptPathAndName.lastIndexOf(pathSeparator);
        String scriptName = scriptPathAndName;
        if (index > -1) {
            checkArgument(index+1 < scriptPathAndName.length(), "Script argument does not refer to a file.");
            scriptName = scriptPathAndName.substring(index+1);
        }
        thisVarContext.put("cliScript",scriptName);
    }

    private static Map<String,Object> createDefaultContext(ConfigurationItem item, Map<String, String> args, Parameters params, HostContainer host, final ConfigurationItem cli) {
        Map<String, Object> thisVarContext = newHashMap();

        thisVarContext.put("thisCi", item);
        thisVarContext.put("args", args);
        thisVarContext.put("cli", cli);
        checkArgument(params != null, "Atleast [password] parameter is required.");
        thisVarContext.put("params", params);
        thisVarContext.put("cliHome",cli.getProperty("home"));
        thisVarContext.put("cliUser",getAuthenticatedUserName());
        String cliPassword = params.getProperty("password");
        checkArgument(!isNullOrEmpty(cliPassword), "Parameter [password] is required.");
        thisVarContext.put("cliPassword",cliPassword);


        return thisVarContext;
    }

}
