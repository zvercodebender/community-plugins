execfile("%s/deploy-lib.py" % currentWorkingDirectory)
for app in params.packages:
  deploy(app.id, thisCi.id)
  if taskInfo.state != "DONE":
    raise "Deploymet failed"
