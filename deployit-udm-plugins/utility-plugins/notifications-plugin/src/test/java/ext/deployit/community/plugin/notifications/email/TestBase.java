/*
 * @(#)TestBase.java     24 Sep 2011
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
package ext.deployit.community.plugin.notifications.email;

import static com.xebialabs.deployit.test.support.TestUtils.createDeployedApplication;
import static com.xebialabs.deployit.test.support.TestUtils.createDeploymentPackage;
import static com.xebialabs.deployit.test.support.TestUtils.createEnvironment;
import static com.xebialabs.deployit.test.support.TestUtils.newInstance;

import org.junit.BeforeClass;

import com.google.common.collect.ImmutableSet;
import com.xebialabs.deployit.deployment.planner.DeltaSpecificationBuilder;
import com.xebialabs.deployit.plugin.api.boot.PluginBooter;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import com.xebialabs.deployit.test.support.TestUtils;

import ext.deployit.community.plugin.notifications.email.ci.EmailPrototype;
import ext.deployit.community.plugin.notifications.email.ci.MailServer;

public abstract class TestBase {

    @BeforeClass
    public static void boot() {
        PluginBooter.bootWithoutGlobalContext();
    }
    
    protected static Environment newEnvironment() {
        MailServer mailServer = newInstance(MailServer.class);
        mailServer.setEmailPrototypes(ImmutableSet.of(
                TestUtils.<EmailPrototype>newInstance("notify.DeploymentStartNotificationPrototype"), 
                TestUtils.<EmailPrototype>newInstance("notify.DeploymentEndNotificationPrototype")));
        return createEnvironment(mailServer);
    }
    
    protected static DeltaSpecificationBuilder newDeltaSpec(Environment env,
            Deployed<?, ?>... newDeployeds) {
        DeltaSpecificationBuilder builder = DeltaSpecificationBuilder.newSpecification();
        builder.initial(createDeployedApplication(createDeploymentPackage(), env));
        for (Deployed<?, ?> newDeployed : newDeployeds) {
            builder.create(newDeployed);
        }
        return builder;
    }
}
