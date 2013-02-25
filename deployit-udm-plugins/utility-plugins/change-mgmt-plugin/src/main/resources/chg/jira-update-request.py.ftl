#!/usr/bin/python

import requests
from requests.auth import HTTPBasicAuth
import json

restUrl = "${deployed.container.url}/rest/api/2/"
issueUrl = restUrl + "issue/${deployed.requestId}"
userAuth = HTTPBasicAuth("${deployed.container.username}", "${deployed.container.password}")

# Check for ticket

print "Checking for request ${deployed.requestId} in ${deployed.container.name}"
ticketRequest = requests.get(issueUrl, auth=userAuth)
if ticketRequest.status_code != 200:
    print "Error: unable to find request ${deployed.requestId}"
    exit(1)

# Find possible transitions

transRequest = requests.get(issueUrl + "/transitions", auth=userAuth)
if transRequest.status_code != 200:
    print "Error: unable to find transitions for request ${deployed.requestId}"
    exit(1)
transitions = transRequest.json()['transitions']

# Find transition

wantedTransition = -1
for t in transitions:
    if t['name'] == '${deployed.container.transitionName}':
        wantedTransition = t['id']

if wantedTransition == -1:
    print "Error: unable to find transition '${deployed.container.transitionName}' for request ${deployed.requestId}"
    exit(1)

# Perform transition

print "Performing transition ${deployed.container.transitionName}"

transitionData = {
    "update": {
        "comment": [
            {
                "add": {
                    "body": "${deployed.container.transitionMessage}"
                }
            }
        ]
    },
    "transition": {
        "id": wantedTransition
    }
}

headers = {"content-type": "application/json"}
transRequest = requests.post(issueUrl + '/transitions', data=json.dumps(transitionData), auth=userAuth, headers=headers)
if transRequest.status_code != 204:
    print "Error: unable to perform transition " + wantedTransition + " for request ${deployed.requestId}"
    print transRequest
    exit(1)

# Post comment

if "${deployed.container.transitionMessage}" != "":
    commentData = {
        "body": "${deployed.container.transitionMessage}"
    }

    commentRequest = requests.post(issueUrl + "/comment", data=json.dumps(commentData), auth=userAuth, headers=headers)
    if commentRequest.status_code != 201:
        print "Error: unable to post comment to request ${deployed.requestId}"
        print transRequest
        exit(1)

print "Done."

