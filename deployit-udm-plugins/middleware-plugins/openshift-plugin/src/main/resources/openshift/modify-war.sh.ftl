#!/bin/sh

GIT_URL=`${deployed.container.cloud.rhcCommand} app show -l ${deployed.container.cloud.username} -p ${deployed.container.cloud.password} -a ${deployed.container.name} | grep "Git URL" | sed -e 's/ *Git URL: //'`

if [ -z "$GIT_URL" ]; then
  echo ERROR: Unable to retrieve OpenShift repository URL for ${deployed.container.name}
  exit 1
fi

echo Cloning repository for ${deployed.container.name} from $GIT_URL
git clone $GIT_URL ${deployed.container.name}
cd ${deployed.container.name}

# requires DEPLOYITPB-3422
if [ "${previous.contextRoot}" != "${deployed.contextRoot}" ]; then
  echo Removing current version of ${deployed.name} at context root ${previous.contextRoot}
  git rm deployments/${previous.contextRoot}.war
fi

echo Adding ${deployed.name} to deployments using context root ${deployed.contextRoot}
cp ${deployed.file} deployments/${deployed.contextRoot}.war
git add .
git commit -m "Upgrade deployment from Deployit at `date`"
git push origin