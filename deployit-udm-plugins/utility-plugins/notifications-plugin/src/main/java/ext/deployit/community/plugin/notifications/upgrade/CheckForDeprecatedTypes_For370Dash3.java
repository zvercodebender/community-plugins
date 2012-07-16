package ext.deployit.community.plugin.notifications.upgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.server.api.repository.RawRepository;
import com.xebialabs.deployit.server.api.upgrade.Upgrade;
import com.xebialabs.deployit.server.api.upgrade.UpgradeException;
import com.xebialabs.deployit.server.api.upgrade.Version;

public class CheckForDeprecatedTypes_For370Dash3 extends Upgrade {
    private static final Type DEPRECATED_TYPE = Type.valueOf("notify.SentTemplateEmail2");
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckForDeprecatedTypes_For370Dash3.class);

    @Override
    public boolean doUpgrade(RawRepository repository) throws UpgradeException {
        // assuming an upgrader is only run to completion once!
        if (!DescriptorRegistry.exists(DEPRECATED_TYPE)) {
            LOGGER.error("ERROR: Unable to find temporary type definition for '{}' required for the upgrade of the notifications-plugin. Please add the following definition snippet to SERVER_HOME/ext/synthetic.xml (see '{}' for details) and restart the server:\n\n{}\n",
                    new Object[] { DEPRECATED_TYPE, "http://docs.xebialabs.com/releases/3.7/deployit/customizationmanual.html#synthetic-properties",
                    "<type type=\"" + DEPRECATED_TYPE + "\" extends=\"notify.SentTemplateEmail\" />" });
            return false;
        }

        /*
         * The temporary type declaration can only be removed if there are *no* subtypes
         * and *no* instances of the type.
         */
        if (DescriptorRegistry.getSubtypes(DEPRECATED_TYPE).isEmpty()
                && repository.findNodesByType(DEPRECATED_TYPE).isEmpty()) {
            LOGGER.info("INFO: Deprecated type '{}' is not in use. Please remove the temporary type definition from SERVER_HOME/ext/synthetic.xml and restart the server", DEPRECATED_TYPE);
            // can't fail here otherwise we'd be back in the situation checked for above
        } else {
            LOGGER.warn("INFO: Deprecated type '{}' is in use. Please retain the type definition in SERVER_HOME/ext/synthetic.xml", DEPRECATED_TYPE);
        }

        return true;
    }

    @Override
    public Version upgradeVersion() {
        return Version.valueOf("notifications-plugin", "3.7.0-3");
    }
}
