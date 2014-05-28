
def getContainers():
	containers = []
	for _delta in deltas.getDeltas():
		if (_delta.getOperation != Operation.NOOP):
			deployed = currentDeployed(_delta)
			#todo check container
			containers.append(deployed.getContainer())
	return containers


for container in getContainers():
	myVars = {'container': container}
	context.addStep(steps.execute_remote_os_script(description = "Stopping server", order = 20, script_template_path = "scripts/stop", vars = myVars, container = container.getProperty("host")))
	context.addStep(steps.execute_remote_os_script(description = "Starting server", order = 80, script_template_path = "scripts/start", vars = myVars, container = container.getProperty("host")))

