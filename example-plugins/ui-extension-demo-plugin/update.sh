#!/bin/sh

gradle clean build
cp build/libs/ui-extension-demo-plugin.jar $DIT_OUT/plugins

/usr/bin/stopDeployIt.sh
/usr/bin/runServer.sh -s &
