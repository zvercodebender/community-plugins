#!/usr/bin/python

import requests
from requests.auth import HTTPBasicAuth

restUrl = "${deployed.container.url}/rest/api/2/"
issueUrl = restUrl + "issue/${deployed.requestId}"

print "Checking for ${deployed.requestId} in ${deployed.container.name}"
ticketRequest = requests.get(issueUrl, auth=HTTPBasicAuth('${deployed.container.username}', '${deployed.container.password}'))
if ticketRequest.status_code != 200:
    print "Error: unable to find request ${deployed.requestId}"
    exit(1)

print "Request found, done."
