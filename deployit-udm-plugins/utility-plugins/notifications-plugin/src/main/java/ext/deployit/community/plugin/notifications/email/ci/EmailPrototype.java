/*
 * @(#)WebContent.java     18 Aug 2011
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
package ext.deployit.community.plugin.notifications.email.ci;

import static ext.deployit.community.plugin.notifications.email.deployed.SentEmail.*;

import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.api.udm.base.BaseConfigurationItem;

import ext.deployit.community.plugin.notifications.email.deployed.SentEmail;

@SuppressWarnings("serial")
@Metadata(virtual = true)
public class EmailPrototype extends BaseConfigurationItem {

    public void applyToEmail(SentEmail email) {
        email.setProperty(SUBJECT_PROPERTY, this.<String>getProperty(SUBJECT_PROPERTY));
        email.setProperty(FROM_PROPERTY, this.<String>getProperty(FROM_PROPERTY));
        email.setProperty(TO_PROPERTY, this.<String>getProperty(TO_PROPERTY));
        email.setProperty(CC_PROPERTY, this.<String>getProperty(CC_PROPERTY));
        email.setProperty(BCC_PROPERTY, this.<String>getProperty(BCC_PROPERTY));
        email.setProperty(BODY_PROPERTY, this.<String>getProperty(BODY_PROPERTY));
    }
}