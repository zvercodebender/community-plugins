package ext.deployit.community.plugin.overthere;

import java.util.List;
import java.util.Map;

import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Delegate;
import com.xebialabs.deployit.plugin.overthere.Host;

public class AliasHostTasks {

    @Delegate(name = "checkConnectionOnAliasHost")
    public List<Step> checkConnectionOnAliasHost(ConfigurationItem ci, String method, Map<String, String> params) {
        Host host = (Host) ci;
        new AliasProcessor().setCredentials(host, "username", "password");
        return host.checkConnection();
    }
}
