from java.util import HashSet

def getContainers():
	containers = HashSet()
	for _delta in deltas.getDeltas():
		if (_delta.getOperation != Operation.NOOP):
			deployed = currentDeployed(_delta)
			containers.add(deployed.getContainer())
	return containers


for container in getContainers():
	myVars = {'container': container}
	context.addStep(steps.execute_remote_os_script(description = "Stopping server %s" % container.name, order = 20, script_template_path = "scripts/stop", vars = myVars, container = container.getProperty("host")))
	context.addStep(steps.execute_remote_os_script(description = "Starting server %s" % container.name, order = 80, script_template_path = "scripts/start", vars = myVars, container = container.getProperty("host")))

