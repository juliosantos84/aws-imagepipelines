#! /bin/bash

REGION=$(curl http://169.254.169.254/latest/meta-data/placement/region 2> /dev/null)
INSTANCE_ID=$(curl http://169.254.169.254/latest/meta-data/instance-id 2> /dev/null)

aws autoscaling set-instance-health --instance-id ${INSTANCE_ID} --region ${REGION} --health-status Unhealthy
