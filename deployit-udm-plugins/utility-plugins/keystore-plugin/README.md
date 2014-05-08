# Keystore plugin #

This document describes the functionality provided by the Keystore plugin.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Keystore plugin is a Deployit plugin and allows to package certifcates and deploy them to a keystore.KeyStore.

##Features##

* Install a certificate
* Modify a certificate 
* Remove a certificate

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.6+

# Usage #

The plugin works with the standard deployment package of DAR format. Please see the _Packaging Manual_ for more details about the DAR format and the ways to 
compose one. 

The following is a sample deployment-manifest.xml file that can be used to install a certificate.


TODO:
    Manifest-Version: 1.0
    Deployit-Package-Format-Version: 1.3
    CI-Application: SampleApp
    CI-Version: 1.0

    Name: 
    CI-Name: myCer
    CI-Type: keystore.Certificate

