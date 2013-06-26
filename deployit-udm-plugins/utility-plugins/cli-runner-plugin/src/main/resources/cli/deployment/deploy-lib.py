import sys
import os

def readCi(id):
    try:
        return repository.read(id)
    except:
        return None

def getName(ciId):
    return str(ciId[ciId.rfind('/') + 1:])

def getAppNameFromVersion(ciId):
    withoutVersion = str(ciId[0:ciId.rfind('/')])
    return getName(withoutVersion)

def getPackage(depPackage):
    package = readCi(depPackage)
    if package is None:
        print "%s not found" % depPackage
        sys.exit(1)
    return package

def executeDeployment(taskId):
    print "Executing deployment with task id %s" % taskId
    if taskId is not None:
        print "Starting deployment..."
        deployit.startTaskAndWait(taskId)
        taskInfo = deployit.retrieveTaskInfo(taskId)
        print "Final task state: %s after %s of %s steps" % (taskInfo.state, min(taskInfo.currentStepNr + 1, taskInfo.nrOfSteps), taskInfo.nrOfSteps)
        #if taskInfo.state == "DONE":
        #    print "Archiving task."
        #    tasks.archive(taskId)
        return taskInfo

def prepareDeployment(depPackage, envName):
    package = getPackage(depPackage)
    appName = getAppNameFromVersion(depPackage)

    # Load environment
    print "Preparing to deploy to environment [%s]" % envName
    environment = repository.read(envName)

    print "Preparing to deploy applicatin [%s]" % package.id
    # Start deployment
    print "Preparing deployment"
    existingDeployment = readCi("%s/%s" % (environment.id, appName))
    if existingDeployment is not None:
        print "Will perform upgrade."
        deploymentRef = deployment.prepareUpgrade(package.id, existingDeployment.id)
    else:
        print "Will perform initial."
        deploymentRef = deployment.prepareInitial(package.id, environment.id)
    deploymentRef = deployment.generateAllDeployeds(deploymentRef)

    print "Validating deployment"
    deploymentRef = deployment.validate(deploymentRef)

    print "Generating deployment plan"
    taskId = deployment.deploy(deploymentRef).id
    return taskId

def deploy(depPackage, envName):
    taskId = prepareDeployment(depPackage, envName)
    return executeDeployment(taskId)

