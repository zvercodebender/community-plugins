#!/bin/sh

GIT_URL=`${deployed.container.cloud.rhcCommand} app show -l ${deployed.container.cloud.username} -p ${deployed.container.cloud.password} -a ${deployed.container.name} | grep "Git URL" | sed -e 's/ *Git URL: //'`

if [ -z "$GIT_URL" ]; then
  echo ERROR: Unable to retrieve OpenShift repository URL for ${deployed.container.name}
  exit 1
fi

echo Cloning repository for ${deployed.container.name} from $GIT_URL
git clone $GIT_URL ${deployed.container.name}
cd ${deployed.container.name}

ACTION_HOOK_FILE=.openshift/action_hooks/${deployed.actionHookFile}
UPDATED_HOOK_FILE=$ACTION_HOOK_FILE.`date +%s`

echo Removing action hook ${deployed.name} from $ACTION_HOOK_FILE
cat $ACTION_HOOK_FILE | grep -v '${deployed.command}' > $UPDATED_HOOK_FILE
mv -f $UPDATED_HOOK_FILE $ACTION_HOOK_FILE
chmod +x $ACTION_HOOK_FILE
git add .
git commit -m "Removal of action hook by Deployit at `date`"
git push origin