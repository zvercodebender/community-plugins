######################
# usageinfo.py
#
# Prints a report of Deployit usage.
#
# Usage: CLI_HOME> bin/cli.sh -expose-proxies -username admin -password admin -f usageinfo.py -- -server-home /opt/deployit-server
#
# TO DO:
# text versions of top 5 reports
# number of users having performed deployments
# security setup
#
# Add the server installation directory as a parameter to the script and we can find:
#
# are they using LDAP? which one?
# how have they configured Jackrabbit?
#
# Bundle os.py?

# Deployit 3.0.x: edit here to add the server home directory.
DEPLOYIT_SERVER_HOME='unknown'

from cStringIO import StringIO
import sys

# Deployit 3.0 doesn't expose this module by default
try:
	import os
except:
	print ""
	print "Please ensure the CLI has the 'os' module available before running this script. See http://support.xebialabs.com/entries/514462-using-python-modules-in-cli-scripts for more information."
	raise

def is30():
	version = getVersion()
	if version.startswith('3.0'):
		return True
	else:
		return False

def is38():
	version = getVersion()
	if version.startswith('3.8'):
		return True
	else:
		return False

def is37():
	version = getVersion()
	if version.startswith('3.7'):
		return True
	else:
		return False

def is36():
	version = getVersion()
	if version.startswith('3.6'):
		return True
	else:
		return False

def is35():
	version = getVersion()
	if version.startswith('3.5'):
		return True
	else:
		return False

def getVersion():
	try:
		return deployit.info().version
	except:
		return "3.0.x"

def header():
	print ""
	print "======================================="
	print "Deployit usage information, v0.1"
	print ""
	
def footer():
	print ""
	print "======================================="
	print ""

def cicount():
	print "   CI usage:"
	print ""

	descriptorLookup = {}
	if is38():
		descriptors = proxies.referenceData.listDescriptors()
	elif is37():
		descriptors = proxies.referenceData.list().entity.descriptors
	else:
		descriptors = proxies.descriptors.list().entity.descriptors
		
	for descriptor in descriptors:
		if is30():
			lookupname = descriptor.simpleName
		else:
			lookupname = str(descriptor.type)
    
		cis = repository.search(lookupname)
		if len(cis) > 0:
			print "   " + lookupname + ":" , len(cis) , "occurrences"

def getPlatform():
	try:
		return str(os.uname())
	except:
		try:
			os.system('ls > /dev/null')
			return "Unix variant"
		except:
			try:
				os.system('dir > NUL')
				return "Windows variant"
			except:
				return "unknown"

def listFiles(directory, exts = None):
	files = []
	for dirname, dirnames, filenames in os.walk(directory):
		for filename in filenames:
			if exts == None or filename[filename.rfind('.')+1:] in exts:
				files.append(filename)
	return str(files)

def listSshHosts():
	if is30():
		sshHostCI = 'Host'
	else:
		sshHostCI = 'overthere.SshHost'
	
	unixHosts = repository.search(sshHostCI)
	print "   SSH hosts: " , len(unixHosts)
	for hostid in unixHosts:
		host = repository.read(hostid)
		if is30() and host.values['accessMethod'].startswith('SSH'):
			print "     OS:",host.values['operatingSystemFamily'],", Connection type:",host.values['accessMethod'],", Sudo username:" , host.values['sudoUsername']
		else:
			print "     OS:",host.values['os'],", Connection type:",host.values['connectionType'],", Sudo username:" , host.values['sudoUsername']

def listCifsHosts():
	if is30():
		winHostCI = 'Host'
	else:
		winHostCI = 'overthere.CifsHost'
		
	winHosts = repository.search(winHostCI)
	print "   CIFS hosts: " , len(winHosts)
	for hostid in winHosts:
		host = repository.read(hostid)
		if is30() and host.values['accessMethod'].startswith('CIFS'):
			print "     OS:",host.values['operatingSystemFamily'],", Connection type:",host.values['accessMethod'],", Sudo username:" , host.values['sudoUsername']
		else:
			print "     OS:",host.values['os'],", Connection type: " , host.values['connectionType']

def listRepositoryConfig():
	repoauth = 'unknown'
	repostorage = 'unknown'
	
	#
	# TODO:
	# - make version-specific code
	#
	jackrabbitJaasConfig = serverHome + os.sep + 'conf' + os.sep + 'jackrabbit-jaas.config'
	if os.path.exists(jackrabbitJaasConfig): # Deployit 3.0 - 3.6
		f = open(jackrabbitJaasConfig)
		for line in f:
			#if re.match(, line):
			pass
		f.close()

	print "   Authentication mechanism:",repoauth
	print "   Storage mechanism:",repostorage
	
######################
# MAIN

try:
	if proxies != None:
		pass
except:
	print ""
	print "Error: this script should be run with the -expose-proxies flag."
	sys.exit(1)

if len(sys.argv) >= 3 and sys.argv[1] == '-server-home':
	serverHome = sys.argv[2]
else:
	serverHome = DEPLOYIT_SERVER_HOME

# TO DO: properly parse args	
cliHome = '.'

header()

# Deployit version number
print "-- Deployit --"
print ""
print "   Deployit version: " + getVersion()
print ""

# CIs per plugin
print "-- Repository --"
print ""
cicount()
print ""

# Hosts
print "-- Hosts --"
print ""
listSshHosts()
listCifsHosts()
print ""

# Deployments
print "-- Deployments --"
print ""
print "   Total deployed applications: " , len(repository.search('udm.DeployedApplication'))
print "   Total deployments performed: " , len(repository.getArchivedTasks().tasks)

print ""

# CLI configuration
print "-- CLI configuration --"
print ""
if cliHome != "unknown":
	print "   Installed CLI plugins: " , listFiles(cliHome + os.sep + 'plugins', ['jar'])
	print "   Installed CLI hotfixes: " , listFiles(cliHome + os.sep + 'hotfix', ['jar'])
print ""

# CLI extension
print "-- CLI extension --"
print ""
if cliHome != "unknown":
	print "   Installed CLI extensions: " , listFiles(cliHome + os.sep + 'ext', ['cli', 'py'])
print ""

# Server configuration
print "-- Server configuration --"
print ""
if serverHome != "unknown":
	print "   Deployit server home: " , serverHome
	print "   Deployit platform: " , getPlatform()
	print "   Installed server plugins: " , listFiles(serverHome + os.sep + 'plugins', ['jar'])
	print "   Installed server hotfixes: " , listFiles(serverHome + os.sep + 'hotfix', ['jar'])
	listRepositoryConfig()
else:
	print "Unable to examine server installation directory. Please pass the server home directory with the -server-home flag or enter it directly in this script."
print ""

# Server extension
print "-- Server extension --"
print ""
if serverHome != "unknown":
	print "   Installed server extensions: " , listFiles(serverHome + os.sep + 'ext')

	serverSynthetic = serverHome + os.sep + 'ext' + os.sep + 'synthetic.xml'
	if os.path.exists(serverSynthetic):
		print "   Contents of synthetic.xml:"
		print ""
		print "----------"
		f = open(serverSynthetic)
		for line in f:
			print "      " + line,
		print "----------"
		f.close()
else:
	print "Unable to examine server installation directory. Please pass the server home directory with the -server-home flag or enter it directly in this script."

footer()
