#!/bin/sh

${container.cloud.rhcCommand} app destroy -l ${container.cloud.username} -p ${container.cloud.password} -a ${container.name} -b