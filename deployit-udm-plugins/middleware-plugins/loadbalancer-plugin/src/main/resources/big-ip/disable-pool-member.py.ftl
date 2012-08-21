#!/usr/bin/python

import pycontrol.pycontrol as pc
import getpass
from sys import argv

bigip_address = '${container.hostname}'
bigip_user = '${container.username}'
bigip_pass = '${container.password}'
active_partition = '${container.partition}'
poolmember_pool = '${poolmember.bigIpPool}'
poolmember_address = '${poolmember.bigIpAddress}'
poolmember_port = '${poolmember.bigIpPort}'

print 'Connecting to BIG-IP at [' + bigip_address + '] as user [' + bigip_user + ']'
bigip = pc.BIGIP(hostname = bigip_address, username = bigip_user, password = bigip_pass, fromurl = True, wsdls = ['Management.Partition', 'LocalLB.PoolMember'])

print 'Setting active partition to [' + active_partition + ']'
bigip.Management.Partition.set_active_partition(active_partition)

print 'Disabling pool member [' + poolmember_address + ':' + poolmember_port + '] in pool [' + poolmember_pool + ']'
pmem = bigip.LocalLB.PoolMember.typefactory.create('Common.IPPortDefinition')
pmem.address = poolmember_address
pmem.port = poolmember_port

sstate = bigip.LocalLB.PoolMember.typefactory.create('LocalLB.PoolMember.MemberSessionState')
sstate.member = pmem
sstate.session_state = 'STATE_DISABLED'

sstate_seq = bigip.LocalLB.PoolMember.typefactory.create('LocalLB.PoolMember.MemberSessionStateSequence')
sstate_seq.item = [sstate]

bigip.LocalLB.PoolMember.set_session_enabled_state(pool_names = [poolmember_pool], session_states = [sstate_seq])

print 'Done'
