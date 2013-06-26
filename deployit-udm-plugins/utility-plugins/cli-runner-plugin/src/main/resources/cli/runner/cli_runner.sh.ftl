${cliHome}/bin/cli.sh -q -username ${cliUser} -password ${cliPassword} -f ${step.remoteWorkingDirectory.path}/wrapperscript.py
res=$?
if [ $res != 0 ] ; then
exit $res
fi

