#!/bin/sh

${container.cloud.rhcCommand} app create -l ${container.cloud.username} -p ${container.cloud.password} -a ${container.name} -t ${container.applicationType} -n --no-dns