#!/usr/bin/python

import pycontrol.pycontrol as pc
import getpass
from sys import argv

bigip_address = '${container.hostname}'
bigip_user = '${container.username}'
bigip_pass = '${container.password}'
active_partition = '${container.partition}'
node_address = '${poolmember.host.address}'

node_status = 'STATE_ENABLED'

# Initiate SOAP RPC connection to BIG-IP
bigip = pc.BIGIP(hostname = bigip_address, username = bigip_user, password = bigip_pass, fromurl = True, wsdls = ["LocalLB.NodeAddress", "Management.Partition"])
print 'Connected to BIG-IP at "' + bigip_address + '" as user "' + bigip_user + '"'

na = bigip.LocalLB.NodeAddress
mp = bigip.Management.Partition

mp.set_active_partition(active_partition)
print 'Set active partition to: ' + active_partition
print 'Working with node: ' + node_address

current_status = na.get_session_enabled_state([node_address])[0]
print 'Current node status: ' + current_status

if current_status == node_status:
  print 'Status is already set to requested value, doing nothing.'
else:
  na.set_session_enabled_state([node_address], [node_status])
  current_status = na.get_session_enabled_state([node_address])[0]
  print 'New node status: ' + current_status

