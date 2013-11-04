package com.xebialabs.deployit.community.verifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import com.xebialabs.deployit.plugin.api.udm.Version;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeploymentMappingVerifierTest {

    @Mock private DeployedApplication deployedApplication;

    @Mock private Environment environment;

    @SuppressWarnings("rawtypes") private Deployed deployed1;

    @SuppressWarnings("rawtypes") private Deployed deployed2;

    @SuppressWarnings("rawtypes") private Deployed deployed2b;

    private Deployable deployable1;

    private Deployable deployable2;

    private DeploymentMappingVerifier verifier;

    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);

        deployable1 = mockDeployable("Applications/completeDeploymentTest/1.0/test-command-1");
        deployable2 = mockDeployable("Applications/completeDeploymentTest/1.0/test-command-2");

        final Version version = mock(Version.class);
        when(version.getId()).thenReturn("Applications/completeDeploymentTest/1.0");
        when(version.getDeployables()).thenReturn(new HashSet<Deployable>(Arrays.asList(deployable1, deployable2)));

        when(deployedApplication.getVersion()).thenReturn(version);
        when(deployedApplication.getEnvironment()).thenReturn(environment);

        deployed1 = mockDeployed("Infrastructure/test-server-1/test-command-1", deployable1);
        deployed2 = mockDeployed("Infrastructure/test-server-1/test-command-2", deployable2);
        deployed2b = mockDeployed("Infrastructure/test-server-2/test-command-2", deployable2);

        verifier = new DeploymentMappingVerifier();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void completeDeploymentShouldPassValidation() {
        setRequiredInstancesPerEnvironment(deployable1, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesPerEnvironment(deployable2, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesEnforcement(environment, RequiredInstancesEnforcement.FULL);
        when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void incompleteDeploymentShouldFailValidation() {
        setRequiredInstancesPerEnvironment(deployable1, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesPerEnvironment(deployable2, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesEnforcement(environment, RequiredInstancesEnforcement.FULL);
        when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertFalse(errorMessages.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void incompleteDeploymentShouldPassValidationIfDeployableCardinalityIsOptional() {
        setRequiredInstancesPerEnvironment(deployable1, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesPerEnvironment(deployable2, RequiredInstancesPerEnvironment.ZERO_OR_MORE);
        setRequiredInstancesEnforcement(environment, RequiredInstancesEnforcement.FULL);
        when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void redundantDeploymentShouldPassValidationIfDeployableCardinalityIsRedudant() {
        setRequiredInstancesPerEnvironment(deployable1, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesPerEnvironment(deployable2, RequiredInstancesPerEnvironment.TWO_OR_MORE);
        setRequiredInstancesEnforcement(environment, RequiredInstancesEnforcement.FULL);
        when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2, deployed2b)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void nonRedundantDeploymentShouldFailValidationIfDeployableCardinalityIsRedudant() {
        setRequiredInstancesPerEnvironment(deployable1, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesPerEnvironment(deployable2, RequiredInstancesPerEnvironment.TWO_OR_MORE);
        setRequiredInstancesEnforcement(environment, RequiredInstancesEnforcement.FULL);
        when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertFalse(errorMessages.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void nonRedundantDeploymentShouldPassValidationIfDeployableCardinalityIsRedudantAndEnvironmentIgnoresRedundancyRequirements() {
        setRequiredInstancesPerEnvironment(deployable1, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesPerEnvironment(deployable2, RequiredInstancesPerEnvironment.TWO_OR_MORE);
        setRequiredInstancesEnforcement(environment, RequiredInstancesEnforcement.LENIENT);
        when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void incompleteDeploymentShouldPassValidationIfEnvironmentIgnoresCardinalityRequirements() {
        setRequiredInstancesPerEnvironment(deployable1, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesPerEnvironment(deployable2, RequiredInstancesPerEnvironment.ONE_OR_MORE);
        setRequiredInstancesEnforcement(environment, RequiredInstancesEnforcement.NONE);
        when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
    }

    private void setRequiredInstancesEnforcement(Environment environment, RequiredInstancesEnforcement enforcementLevel) {
        when(environment.hasProperty("requiredInstancesEnforcement")).thenReturn(true);
        when(environment.getProperty("requiredInstancesEnforcement")).thenReturn(enforcementLevel);
    }

    private void setRequiredInstancesPerEnvironment(Deployable deployable, RequiredInstancesPerEnvironment mustBeMapped) {
        when(deployable.hasProperty("requiredInstancesPerEnvironment")).thenReturn(true);
        when(deployable.getProperty("requiredInstancesPerEnvironment")).thenReturn(mustBeMapped);
    }

    private Deployable mockDeployable(final String id) {
        Deployable deployable = mock(Deployable.class);
        when(deployable.getId()).thenReturn(id);
        return deployable;
    }

    @SuppressWarnings("rawtypes")
    private Deployed mockDeployed(final String id, final Deployable deployable) {
        Deployed deployed = mock(Deployed.class);
        when(deployed.getId()).thenReturn(id);
        when(deployed.getDeployable()).thenReturn(deployable);
        return deployed;
    }
}
