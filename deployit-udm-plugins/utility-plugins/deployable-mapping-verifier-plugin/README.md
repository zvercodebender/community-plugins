# Deployable Mapping Verifier plugin #

# Overview #

The Deployable Mapping Verifier plugin is a Deployit plugin that enables automatic verification that a deployment includes all required deployeds. Deployment will not proceed if the requirements are not met.

# Requirements #

* **Deployit**: tested with Deployit 3.9.3, your mileage may vary with older releases.

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

# Usage

This plugin adds the optional property `requiredInstancesPerEnviroment` to `udm.BaseDeployable`. This is an enum that specifies how many Deployeds a valid deployment should map from this Deployable. It can have the following values:

* EXACTLY_ZERO. #deployeds == 0, i.e. this Deployable should not be deployed (in this Enviroment). Mostly useful when preventing deployment to a particular Environment.
* ZERO_OR_MORE. #deployeds >= 0, this Deployable may be deployed but does not have to be.
* EXACTLY_ONE. #deployeds == 1, this Deployable must de deployed to exactly one Container.
* ONE_OR_MORE. #deployeds >= 1, this Deployable must be deployed.
* TWO_OR_MORE. #deployeds > 1, this Deployable must be deployed to more than one Container.

The default value for this property is `ONE_OR_MORE`, indicating that typically Deployables must be part of the deployment. This can be overridden in `deployit-defaults.properties` of course.

The plugin also adds an optional property `requiredInstancesEnforcementLevel` to `udm.Environment`. This specifies the strictness of enforcement of the `requiredInstancesPerEnviroment` requirements. It can have the following values:

* NONE. Skip `requiredInstancesPerEnviroment` checking completely.
* LENIENT. Enforce `requiredInstancesPerEnviroment` checking, but treat TWO_OR_MORE as ONE_OR_MORE. This enables scenarios where redundant deployment is mandatory for the production environment but deployemnt to a single server is sufficient for test environments. 
* FULL. Enforce full `requiredInstancesPerEnviroment` checking.

The default value for this property is `NONE`. This enables a gradual transition to enforcement once all applications for an environment have correct `requiredInstancesPerEnviroment` configurations. 
