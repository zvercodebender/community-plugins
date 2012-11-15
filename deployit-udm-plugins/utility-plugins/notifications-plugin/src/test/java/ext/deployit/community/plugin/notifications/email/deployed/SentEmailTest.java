/*
 * @(#)SentEmailTest.java     24 Sep 2011
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package ext.deployit.community.plugin.notifications.email.deployed;

import static com.google.common.collect.Lists.newLinkedList;
import static com.xebialabs.deployit.test.support.TestUtils.newInstance;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.xebialabs.deployit.plugin.api.deployment.execution.DeploymentStep;
import com.xebialabs.deployit.plugin.api.deployment.planning.DeploymentPlanningContext;
import com.xebialabs.deployit.plugin.api.deployment.planning.ReadOnlyRepository;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.DeploymentPackage;
import com.xebialabs.deployit.plugin.api.udm.Version;

import ext.deployit.community.plugin.notifications.email.TestBase;
import ext.deployit.community.plugin.notifications.email.step.EmailSendStep;
import ext.deployit.community.plugin.notifications.util.StepAdapter;

public class SentEmailTest extends TestBase {

    @Test
    public void supportsNoTos() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");
        // just to be sure
        newDeployed.setProperty("to", null);
        assertThat(newDeployed.getToAddresses().size(), is(0));
    }
    
    @Test
    public void supportsNoCcs() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");
        // just to be sure
        newDeployed.setProperty("cc", null);
        assertThat(newDeployed.getCcAddresses().size(), is(0));
    }
    
    @Test
    public void supportsNoBccs() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");
        // just to be sure
        newDeployed.setProperty("bcc", null);
        assertThat(newDeployed.getBccAddresses().size(), is(0));
    }

    @Test
    public void supportsEmptyTos() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");
        // just to be sure
        newDeployed.setProperty("to", "");
        assertThat(newDeployed.getToAddresses().size(), is(0));
    }
    
    @Test
    public void supportsEmptyCcs() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");
        // just to be sure
        newDeployed.setProperty("cc", "");
        assertThat(newDeployed.getCcAddresses().size(), is(0));
    }
    
    @Test
    public void supportsEmptyBccs() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");
        // just to be sure
        newDeployed.setProperty("bcc", "");
        assertThat(newDeployed.getBccAddresses().size(), is(0));
    }
    
    @Test
    public void supportPlaceholdersInSubject() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");

        DeployedApplication deployedApplication = newInstance(DeployedApplication.class);
        Version version = newInstance(DeploymentPackage.class);
        version.setId("/Applications/PetClinic-ear/1.0");
        deployedApplication.setVersion(version);
        newDeployed.setDeployedApplication(deployedApplication);
        newDeployed.setProperty("subject", "Deployment of ${deployed.deployedApplication.version.name} started!");
        assertThat(newDeployed.getSubject(), is("Deployment of 1.0 started!"));
    }

    @Test
    public void defaultsToNoAwaitCompletionStep() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");
        StubPlanningContext capturingContext = new StubPlanningContext();
        newDeployed.executeCreate(capturingContext, null);
        List<Step> steps = capturingContext.steps;
        assertThat(steps.size(), is(1));
        assertThat(steps.get(0), instanceOf(EmailSendStep.class));
    }

    @Test
    public void addsAwaitCompletionStepIfRequested() {
        SentEmail newDeployed = newInstance("notify.BasicSentEmail");
        newDeployed.setProperty("awaitConfirmation", true);
        StubPlanningContext capturingContext = new StubPlanningContext();
        newDeployed.executeCreate(capturingContext, null);
        List<Step> steps = capturingContext.steps;
        assertThat(steps.size(), is(2));
        assertThat(steps.get(0), instanceOf(EmailSendStep.class));
        // also a GenericBaseStep but *not* an EmailSendStep
        assertThat(steps.get(1), not((instanceOf(EmailSendStep.class))));
    }

    private static class StubPlanningContext implements DeploymentPlanningContext {
        private final List<Step> steps = newLinkedList();
        
        @Override
        public void addStep(Step step) {
            steps.add(step);
        }

        @Override
        public void addSteps(Step... steps) {
            addSteps(asList(steps));
        }

        @Override
        public void addSteps(Iterable<Step> steps) {
            for (Step step : steps) {
                addStep(step);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void addStep(DeploymentStep step) {
            addStep(StepAdapter.wrapIfNeeded(step));
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public void addSteps(DeploymentStep... steps) {
            for (DeploymentStep step : steps) {
                addStep(step);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void addSteps(Collection<DeploymentStep> steps) {
            for (DeploymentStep step : steps) {
                addStep(step);
            }
        }

        @Override
        public Object getAttribute(String name) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public void setAttribute(String name, Object value) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public DeployedApplication getDeployedApplication() {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public ReadOnlyRepository getRepository() {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public void addCheckpoint(Step arg0, Delta arg1) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public void addCheckpoint(Step arg0, Iterable<Delta> arg1) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public void addCheckpoint(Step arg0, Delta arg1, Operation arg2) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public void addStepWithCheckpoint(Step arg0, Delta arg1) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public void addStepWithCheckpoint(Step arg0, Iterable<Delta> arg1) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");            
        }

        @Override
        public void addStepWithCheckpoint(Step arg0, Delta arg1, Operation arg2) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }
    }
}