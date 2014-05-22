
def getContainers():
	containers = []
	for _delta in deltas.getDeltas():
		if (_delta.getOperation != Operation.NOOP):
			deployed = currentDeployed(_delta)
			containers.append(deployed.getContainer())
	return containers


for container in getContainers():
	context.addStep(steps.success(description = "Stopping server " + container.getName(), order = 20))
	context.addStep(steps.success(description = "Starting server " + container.getName(), order = 80))

