${cliHome}\bin\cli.cmd -q -username ${cliUser} -password ${cliPassword} -f ${step.remoteWorkingDirectory.path}\wrapperscript.py
set RES=%ERRORLEVEL%
if not %RES% == 0 (
  exit %RES%
)
