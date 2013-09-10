# Deployit Python script

import sys, re
print sys.argv

id = sys.argv[1]
ciType = sys.argv[2]

values = {}
for i in range(3, len(sys.argv)):
	arg = sys.argv[i]
	match = re.search("^(\w+)=(.*)$", arg)
	if match != None:
		values[match.group(1)] = match.group(2)

print "Creating CI '%s' of type '%s' with values: %s" % (id, ciType, values)

try:
	ci = repository.read(id)

except:
	ci = factory.configurationItem(id, ciType, values)
	print "Created CI:", ci

	repository.create(ci)

else:
	ci.values = values

	repository.update(ci)
