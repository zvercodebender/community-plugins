import com.xebialabs.deployit.plugin.api.udm.artifact.Artifact
import getopt, sys, time, ast



def reverseDar(id, propertiesFile):
	count = 0
	file = open(propertiesFile, "w")
	cis = [str(item) for item in repository.read(id).deployables]
	for item in cis:
		ci = repository.read(item)
		file.write("artifact" + str(count) + ".name=" + str(ci.name) + "\n")
		file.write("artifact" + str(count) + ".type=" + str(ci.type) + "\n")
		if isinstance(ci.values._ci, com.xebialabs.deployit.plugin.api.udm.artifact.Artifact):
				file.write("artifact" + str(count) + ".fileLocation=\n")	
		for entries in ci.values._ci.getSyntheticProperties().entrySet():		
			if not entries.key == "checksum":
				file.write("artifact" + str(count) + "." + str(entries.key) + "=" + str(entries.value) + "\n")
		count = count + 1		
	file.close()	
	




try:
    opts, args = getopt.getopt(sys.argv[1:],'hd:p:',['id=','propertiesFile='])
except getopt.GetoptError:
    print 'cli.sh -host <XLDeployHost> -username <username> -password <password> -f $PWD/reverseDar.y -- -d <id> -p <propertiesFile>'
    sys.exit(2)
for opt, arg in opts:
	if opt == '-h':
		print 'cli.sh -host <XLDeployHost> -username <username> -password <password> -f $PWD/reverseDar.y -- -d <id> -p <propertiesFile>'
		sys.exit(-1)
	elif opt in ('-d', '--id'):
		id = arg
	elif opt in ('-p', '--propertiesFile'):
		propertiesFile = arg        

if id == None:
	print 'ERROR: appName and buildID are mandatory on the command line'
	print 'cli.sh -host <XLDeployHost> -username <username> -password <password> -f $PWD/reverseDar.py -- -d <id> -p <propertiesFile>'
	sys.exit(-1)
reverseDar(id,propertiesFile)	
