package ext.deployit.community.plugin.clirunner.delegate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Ordering;

import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Delegate;
import com.xebialabs.deployit.plugin.api.udm.Parameters;
import com.xebialabs.deployit.plugin.api.udm.Version;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class CliDeploymentDelegate {

    @Delegate(name = "cliDeployment")
    public static List<Step> executeCliScriptDelegate(ConfigurationItem item, String name, Map<String, String> args, Parameters params) {
        Type type = Type.valueOf("udm.Environment");
        checkArgument(item.getType().getDescriptor().isAssignableTo(type), "Cli deployment can only be performed from udm.Environment" );
        Ordering<Version> ordering = Ordering.from(new Comparator<Version>() {
            @Override
            public int compare(final Version o1, final Version o2) {
                Integer o1DeploymentWeight = o1.getApplication().getProperty("deploymentWeight");
                Integer o2DeploymentWeight = o2.getApplication().getProperty("deploymentWeight");
                return o1DeploymentWeight.compareTo(o2DeploymentWeight);
            }
        });

        Map<String,String> modifiedArgs = newHashMap();
        modifiedArgs.putAll(args);
        modifiedArgs.put("script", "cli/deployment/deploy.py");
        String classpathResources = args.get("classpathResources");
        if (isNullOrEmpty(classpathResources)) {
            modifiedArgs.put("classpathResources","cli/deployment/deploy-lib.py");
        } else {
            modifiedArgs.put("classpathResources",classpathResources+",cli/deployment/deploy-lib.py");
        }
        modifiedArgs.put("script","cli/deployment/deploy.py");

        List<Version> packages = params.getProperty("packages");
        List<Version> sortedPackages = ordering.sortedCopy(packages);
        List<Step> steps = newArrayList();
        for (Version sortedPackage : sortedPackages) {
            params.setProperty("packages",newArrayList(sortedPackage));
            modifiedArgs.put("scriptDescription","Deploying " + sortedPackage.getId() + " to " + item.getName());
            steps.addAll(CliScriptDelegate.executeCliScriptDelegate(item, name, modifiedArgs, params));
        }

        return steps;

    }
}
