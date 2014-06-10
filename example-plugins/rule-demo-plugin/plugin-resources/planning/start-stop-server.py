from java.util import HashSet

def getContainers():
	containers = HashSet()
	for _delta in deltas.getDeltas():
		deployed = _delta.relevantDeployed
		currentContainer = deployed.getContainer()
		if (_delta.getOperation != "NOOP" and currentContainer.type == "example.Server"):
			containers.add(currentContainer)
	return containers


for container in getContainers():
	myVars = {'container': container}
	context.addStep(steps.execute_remote_os_script(description = "Stopping server %s" % container.name, order = 20, script_template_path = "scripts/stop", vars = myVars, container = container.host))
	context.addStep(steps.execute_remote_os_script(description = "Starting server %s" % container.name, order = 80, script_template_path = "scripts/start", vars = myVars, container = container.host))

