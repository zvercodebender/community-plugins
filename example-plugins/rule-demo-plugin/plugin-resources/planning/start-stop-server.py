from java.util import HashSet

def getContainers():
	containers = HashSet()
	for _delta in deltas.deltas:
		deployed = _delta.deployedOrPrevious
		currentContainer = deployed.container
		if (_delta.operation != "NOOP" and currentContainer.type == "example.Server"):
			containers.add(currentContainer)
	return containers


for container in getContainers():
	stepVars = {'container': container}
	context.addStep(steps.execute_remote_os_script(description = "Stopping server %s" % container.name, order = 20, script_template_path = "scripts/stop", vars = stepVars, container = container.host))
	context.addStep(steps.execute_remote_os_script(description = "Starting server %s" % container.name, order = 80, script_template_path = "scripts/start", vars = stepVars, container = container.host))

