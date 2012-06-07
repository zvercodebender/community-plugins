## Description

A Deployit 3.7 plugin that generate pause steps.

## Installation

Place the 'pause-plugin-&lt;version&gt;.jar' file into your SERVER_HOME/plugins directory.

## Configuration
The udm.Environment ci has 2 new properties:
* pausable (boolean)
* pauseOrder
## Usage

if pause property is true, The plugin will generate a pause step a the given order for all deployments targeting this environment.