# XL Deploy CLI script to create a package from a WAR. Optionally also deploy to an env
# 2014-05-28 - Tom Batchelor
# 2014-05-29 - Tom Batchelor - Update to support properties files, also some refactoring thing into functions
# 2014-06-05 - Tom Batchelor - Update to support getting CLASSLOADER_MODE, CLASSLOADER_POLICY, SHARED_LIBRARIES and TAGS from XL Deploy
#                               dictionaries. Also creates ATPCO properties file copy tasks. Added wait for deployment with full status
#                               output
# Version 3
#
#
# Usage:
#
# cli.sh -host <XLDeployHost> -username <username> -password <password> -f $PWD/createDar.py -- -n <appName> -b <buildID> -l <fileLocation> -p <propertiesFile> -a <autoDeploy true|false> -e <deployEnv>
#
# Properties file is in the format type.key=value where type is 'war' for sessings on a war artifact for exmaple. Note that artifact.type always needs to be speced
#
# artifact.type=was.War
# core.contextRoot=/Cars_Sample_App
# session.name=PSESSIONID
# session.scope=APPLICATION
# session.enableSSLTracking=false
# session.enableCookies=false
# session.enableUrlRewriting=false
# session.enableProtocolSwitchRewriting=false
# session.TuningParams_maxInMemorySessionCount=5
# session.Cookie_name=PSESSIONID
# session.Cookie_secure=false
# session.Cookie_httpOnly=true
# session.Cookie_domain=example.com
# session.Cookie_maximumAge=30
# session.Cookie_path=/
#
# Example command execution (using above props file):
#
# cli.sh -username admin -password deploy -source /Users/tom/Documents/CreateDARCLI/createDAR.py -- -n TestApp -l /Users/tom/builds/Cars_Sample_App.war -b 1.0-6 -p /Users/tom/builds/propertiesSample.properties -a true -e "WAS/WAS8ND"
#
# App name: TestApp
# Build ID: 1.0-6
# File location: /Users/tom/builds/Cars_Sample_App.war
# Properties File: /Users/tom/builds/propertiesSample.properties
# Auto deploy: true
# Environment: WAS/WAS8ND
#
# Properties are:
# {'session.name': 'PSTARSESSIONID', 'artifact.type': 'was.War', 'core.contextRoot': '/Cars_Sample_App', 'session.enableProtocolSwitchRewriting': 'false', 'session.Cookie_secure': 'false', 'session.Cookie_path': '/', 'session.Cookie_maximumAge': '30', 'session.TuningParams_maxInMemorySessionCount': '5', 'session.Cookie_name': 'PSTARSESSIONID', 'session.Cookie_domain': 'example.com', 'session.scope': 'APPLICATION', 'session.enableUrlRewriting': 'false', 'session.Cookie_httpOnly': 'true', 'session.enableSSLTracking': 'false', 'session.enableCookies': 'false'}
#
# Deployment started with ID: 9284b4ef-353b-4039-bcf6-9e740fa6ed53

import getopt, sys, time

def printSteps(taskInfo, verbose=True):
    for i in range(taskInfo.nrOfSteps):
        info = tasks.step(taskInfo.id, i+1)
        if info.state == 'PENDING':
            print "%s: %s %s [NOT EXECUTED]" % (str(i+1), info.description, info.state)
        else:
            print "%s: %s %s %s %s" % (str(i+1), info.description, info.state, info.startDate, info.completionDate)
            if verbose:
                print info.log
        print

def readCi(id):
    try:
        return repository.read(id)
    except:
        return None

def deployUpdate(deployEnv, appName, package):
    # Read Environment
    environment = repository.read('Environments/' + deployEnv)
    
    # Deployment
    existingDeployment = readCi("%s/%s" % (environment.id, appName))
    if existingDeployment is not None:
        # Upgrade
        print 'Performing upgrade'
        deploymentRef = deployment.prepareUpgrade(package.id, existingDeployment.id)
        deploymentRef = deployment.prepareAutoDeployeds(deploymentRef)
    else:
        # Initial
        print 'Performint initial deployment'
        deploymentRef = deployment.prepareInitial(package.id,environment.id)
        deploymentRef = deployment.generateAllDeployeds(deploymentRef)
    deploymenyRef = deployment.validate(deploymentRef)
    taskID = deployment.deploy(deploymentRef).id
    print "Deploying with task ID: " + taskID
    deployit.startTask(taskID)

    taskState = 'EXECUTING'
    while taskState == 'EXECUTING':
        time.sleep(5)
        taskInfo = deployit.retrieveTaskInfo(taskID)
        taskState = taskInfo.state
        currentStep = taskInfo.currentStepNr
        print "Task now at step %s of %s. State: %s" % (currentStep, taskInfo.nrOfSteps, taskState)

    taskInfo = deployit.retrieveTaskInfo(taskID)
    print "Final task state: %s after %s of %s steps" % (taskInfo.state, min(currentStep + 1, taskInfo.nrOfSteps), taskInfo.nrOfSteps)
    printSteps(taskInfo, True)
    if taskInfo.state != 'EXECUTED':
        print "WARN: Deployment was not completed successfully. Please log in to Deployit to review the deployment or contact a Deployit administrator"
        print "INFO: Cancelling task"
        tasks.cancel(taskInfo.id)
    else:
        tasks.archive(taskID)
    return taskID

def parseProperties(propertiesFile):
    propertiesMap = {}
    for line in open(propertiesFile):
        key, sep, val = line.strip().partition('=')
        propertiesMap[key] = val
    return propertiesMap

def createSessionManager(parent, propertiesMap):
    sessionProps = subsetProps('session',propertiesMap)
    if len(sessionProps) != 0:
        name = sessionProps['name']
        del sessionProps['name']
        sessionManager = factory.configurationItem(parent + '/' + name,'was.SessionManagerSpec',sessionProps)
        repository.create(sessionManager)

def subsetProps(paramType, propertiesMap):
    subset = {}
    for key, val in propertiesMap.items():
        type, sep, subkey = key.strip().partition('.')
        if type == paramType:
            subset[subkey] = val
    return subset

def getDictionaryEntries(appName):
    dictionary = repository.read('Environments/AppDictionaries/' + appName)
    print dictionary.entries
    return dictionary.entries

def safeGetValue(dictionary, key):
    try:
        return dictionary[key]
    except:
        return None

# Main

# vars
buildID = None
appName = None
fileLocation = None
propertiesFile = None
autoDeploy = None
deployEnv = None

# Parse out the arguments
try:
    opts, args = getopt.getopt(sys.argv[1:],'hn:b:l:f:p:a:e:',['appName=','buildID=','warFileLocation=','warFileName=','propertiesFile=','autoDeploy=','deployEnv='])
except getopt.GetoptError:
    print 'cli.sh -host <XLDeployHost> -username <username> -password <password> -f $PWD/createDar.py -- -n <appName> -b <buildID> -l <fileLocation> -p <propertiesFile> -a <autoDeploy true|false> -e <deployEnv>'
    sys.exit(2)
for opt, arg in opts:
    if opt == '-h':
        print 'cli.sh -host <XLDeployHost> -username <username> -password <password> -f $PWD/createDar.py -- -n <appName> -b <buildID> -l <fileLocation> -p <propertiesFile>  -a <autoDeploy true|false> -e <deployEnv>'
        sys.exit(-1)
    elif opt in ("-n", "--appName"):
        appName = arg
    elif opt in ("-b", "--buildID"):
        buildID = arg
    elif opt in ("-l", "--warFileLocation"):
        fileLocation = arg
    elif opt in ("-p", "--propertiesFile"):
        propertiesFile = arg
    elif opt in ("-a", "--autoDeploy"):
        autoDeploy = arg
    elif opt in ("-e", "--deployEnv"):
        deployEnv = arg

if buildID == None or appName == None or fileLocation == None:
    print 'cli.sh -host <XLDeployHost> -username <username> -password <password> -f $PWD/createDar.py -- -n <appName> -b <buildID> -l <fileLocation> -p <propertiesFile> -a <autoDeploy true|false> -e <deployEnv>'
    sys.exit(-1)

fileName = fileLocation.rpartition('/')[2]

if autoDeploy == None:
    autoDeploy = 'false'

if autoDeploy == 'true':
    if deployEnv == None:
        print 'cli.sh -host <XLDeployHost> -username <username> -password <password> -f $PWD/createDar.py -- -n <appName> -b <buildID> -l <fileLocation> -p <propertiesFile> -a <autoDeploy true|false> -e <deployEnv>'
        sys.exit(-1)

# Print Vars
print 'App name: ' + appName
print 'Build ID: ' + buildID
print 'File location: ' + fileLocation
print 'Auto deploy: ' + autoDeploy
if propertiesFile != None:
    print 'Properties File: ' + propertiesFile
else:
    print 'Properties File: '
if autoDeploy == 'true':
    print 'Environment: ' + deployEnv

# Create Properties Map
if propertiesFile != None:
    propertiesMap = parseProperties(propertiesFile)
else:
    #Just a dummy map
    propertiesMap = {'artifact.type':'was.Ear'}

print
print 'Properties are:'
print propertiesMap

# Get Application
newApplication = repository.read('Applications/' + appName)

# Create a new package
newPackage = factory.configurationItem('Applications/' + appName + '/' + buildID,'udm.DeploymentPackage')
repository.create(newPackage)

# Read app specific dictionary
dictionaryEntries = getDictionaryEntries(appName)
propertiesMap['core.classloaderMode'] = safeGetValue(dictionaryEntries, 'CLASSLOADER_MODE')
propertiesMap['core.classloaderPolicy'] = safeGetValue(dictionaryEntries, 'CLASSLOADER_POLICY')
propertiesMap['core.sharedLibraryNames'] = safeGetValue(dictionaryEntries, 'SHARED_LIBRARIES')
propertiesMap['core.tags'] = [safeGetValue(dictionaryEntries, 'TAGS')]

# Create a new EAR
earFile = open(fileLocation, 'rb').read()
props = subsetProps('core',propertiesMap)
earArtifactName = 'Applications/' + appName + '/' + buildID + '/' + fileName.rpartition('.')[0]
newEar = factory.artifact(earArtifactName, propertiesMap['artifact.type'], props, earFile)
newEar.filename = fileName
repository.create(newEar)
# createSessionManager(artifactName, propertiesMap)

#Create properties file copy job
propsJobName = 'Applications/' + appName + '/' + buildID + '/' + 'propertiesFiles'
props = {'source':propertiesMap['props.sourceRoot'] + appName.lower()}
props['destination'] = '/{{CELL}}/properties/'
newPropsJob = factory.configurationItem(propsJobName, 'atpco.PropCopyFile', props)
repository.create(newPropsJob)


# Deploy step
if autoDeploy == 'true':
    taskID = deployUpdate(deployEnv, appName, newPackage)



