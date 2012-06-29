#!/bin/sh

GIT_URL=`${deployed.container.cloud.rhcCommand} app show -l ${deployed.container.cloud.username} -p ${deployed.container.cloud.password} -a ${deployed.container.name} | grep "Git URL" | sed -e 's/ *Git URL: //'`

if [ -z "$GIT_URL" ]; then
  echo ERROR: Unable to retrieve OpenShift repository URL for ${deployed.container.name}
  exit 1
fi

echo Cloning repository for ${deployed.container.name} from $GIT_URL
git clone $GIT_URL ${deployed.container.name}
cd ${deployed.container.name}

echo Removing ${deployed.name} at context root ${deployed.contextRoot}
git rm deployments/${deployed.contextRoot}.war
git commit -m "Undeployment from Deployit at `date`"
git push origin