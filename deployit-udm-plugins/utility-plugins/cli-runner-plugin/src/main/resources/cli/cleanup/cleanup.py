# Clean Deployment Package
from java.util import Calendar

def isWantedApplication(packageId):
  if params.applications is None or len(params.applications) == 0:
    return True
  for app in params.applications:
    if packageId.startswith(app.id):
      return True
  return False

now = Calendar.getInstance()
now.add(Calendar.DAY_OF_MONTH,-params.days)
print "Base date is ",now.getTime()
allPackages = repository.search('udm.DeploymentPackage', now)
deletedPackage = 0
missDeletedPackage = 0

for packageId in allPackages:
  if not isWantedApplication(packageId):
    continue
  package = repository.read(packageId)
  version=packageId.split('/')[-1]
  if params.versionMarker in version:
    if params.dryRun:
      print "Dry run - will delete package", package.id
      deletedPackage = deletedPackage + 1
    else:
      print "Deleting package", package.id
      try:
        repository.delete(package.id)
        deletedPackage = deletedPackage + 1
      except:
        missDeletedPackage = missDeletedPackage +1

print len(allPackages),"\tpackage(s) in the repository imported before", now.getTime()
print deletedPackage,"\tdeleted package(s)"
print missDeletedPackage,"\tcandidate package(s) but not deleted because it's still referenced"
print "done"

