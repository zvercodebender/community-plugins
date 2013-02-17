from java.util import List, Map, HashMap
from javax.script import ScriptException

def getValueMap(ci):
  descriptor = DescriptorRegistry.getDescriptor(ci.type)
  values = HashMap()
  for propertyDescriptor in descriptor.propertyDescriptors:
    value = ci.values[propertyDescriptor.name]
    if value is not None:
      values.put(propertyDescriptor.name, ci.values[propertyDescriptor.name])
  return values

def findChildren(parentNode):
	childrenIds = repository.search(None, parentNode)
	print "Found " + str(len(childrenIds)) + " children under parent " + parentNode
	allIds = []
	for id in childrenIds:
		allIds.append(id)
		allIds.extend(findChildren(id))
	return allIds

def findInfrastructure():
	return findChildren("Infrastructure")

def findEnvironments():
	return findChildren("Environments")

def findConfigurations():
	return findChildren("Configuration")

def loadRepositoryObjects(ids):
	return repository.read(ids);

def objectToScript(obj):
	value = ''
	if(isinstance(obj, List)):
		value = listToScript(obj)
	elif(isinstance(obj, Map)):
		value = mapToScript(obj)
	else:
		objAsString = str(obj)
		if objAsString.find("'") > -1:
			value = '"' + str(obj) + '"'
		else:
			value = "'" + str(obj) + "'"
	return value
	
def listToScript(lst):
	listStr="["
	first = True
	for item in lst:
		if(not first):
			listStr = listStr + ","
		first = False
		listStr = listStr + objectToScript(item)
	return listStr + "]"
	
def mapToScript(map):
	mapStr = "{"
	first  = True
	for entry in map.entrySet(): 
		if(not first):
			mapStr = mapStr + ","
		first = False
		value = objectToScript(entry.value)
		mapStr = mapStr + "'"+entry.key+"':"+value
	return mapStr + "}"
		
def appendRepositoryObject(script, ro, listName):
	return script + "\n"+listName+".append(create('"+ro.id+"','"+ro.type+"',"+mapToScript(getValueMap(ro))+"))"

def appendRepositoryObjects(script,repositoryObjects, listName):
	script += "\n\n\n" + listName +" = []\n"
	for ro in repositoryObjects:
		script = appendRepositoryObject(script, ro, listName)
	script += "\nsave(" + listName + ")";
	return script

def createExportScript():
	script = """from java.util import ArrayList

def create(id, type, values):
   return factory.configurationItem(id, type, values)

def verifyNoValidationErrors(entity):
   print "Validaing ci of type:", entity.type
   if entity.validations is None or len(entity.validations) == 0:
       return entity
   else:
       raise Exception("Validation errors are present! " + entity.validations.toString())

def verifyNoValidationErrorsInRepoObjectsEntity(repositoryObjects):
   for repoObject in repositoryObjects:
       verifyNoValidationErrors(repoObject)

def saveRepositoryObjectsEntity(repoObjects):
	print "Saving repository objects"
	repositoryObjects = repository.create(repoObjects)
	verifyNoValidationErrorsInRepoObjectsEntity(repositoryObjects)
	print "Saved repository objects"
	return repositoryObjects

def save(listOfCis):
	return saveRepositoryObjectsEntity(listOfCis)

"""
	repositoryObjects = loadRepositoryObjects(findInfrastructure())
	script = appendRepositoryObjects(script,repositoryObjects,"infrastructureList")
	repositoryObjects = loadRepositoryObjects(findEnvironments())
	script = appendRepositoryObjects(script,repositoryObjects,"environmentsList")
	repositoryObjects = loadRepositoryObjects(findConfigurations())
	script = appendRepositoryObjects(script,repositoryObjects,"configurationList")
	return script

def writeToFile(fileName, data):
	print "\nWriting to file " + fileName + "..."
	f = open(fileName, "w")
	f.write(data)
	f.close()

print "Exporting repository...\n"

writeToFile("repository-export.py", createExportScript())

print "... done.\n"
