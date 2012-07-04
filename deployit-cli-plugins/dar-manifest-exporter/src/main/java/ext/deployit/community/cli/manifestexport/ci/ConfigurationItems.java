package ext.deployit.community.cli.manifestexport.ci;

public class ConfigurationItems {
    // udm-plugin-api is not available in the CLI
    public static final String DEPLOYABLE_ARTIFACT_TYPE = "udm.DeployableArtifact";
    public static final String EAR_TYPE = "jee.Ear";
    public static final String WAR_TYPE = "jee.War";
    public static final String EJB_JAR_TYPE = "jee.EjbJar";

    public static String nameFromId(String id) {
        return id.substring(id.lastIndexOf('/') + 1);
    }
}
