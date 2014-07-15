/*
* This file is required by the plugin when using Java Classes to
* specify CI definitions. In indicates what to use as the namespace
* for the types.
*
* All of the CIs in XL Deploy are part of a namespace to distinguish
* them from other, similarly named CIs. For instance, CIs that are
* part of the UDM plugin all use the udm namespace (such as udm.Deployable).
*
* Plugins implemented in Java must specify their namespace in a source
* file called package-info.java. This file provides package-level
* annotations and is required to be in the same package as your CIs.
*/

@Prefix("yak")
package com.xebialabs.deployit.plugin.test.yak.ci;

import com.xebialabs.deployit.plugin.api.udm.Prefix;