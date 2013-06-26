print "\n ------------------ SCRIPT OUTPUT ------------------ \n\n"
import sys
import base64

class DictionaryObject:
  def __setattr__(self, propertyName, propertyValue):
    self.__dict__[propertyName] = propertyValue

${pythonVars}
currentWorkingDirectory = "${step.remoteWorkingDirectory.path}"
execfile("${step.remoteWorkingDirectory.path}/${cliScript}")
