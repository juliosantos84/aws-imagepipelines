#! /bin/bash

# export CDK_DEPLOY_ACCOUNT=""
# export CDK_DEPLOY_REGION=""
# export CDK_DEPLOY_PROFILE_FLAG="--profile your profile"
# export CDK_DEPLOY_EXTRA_CONTEXT="-c everythingbiig-aws-imagepipelines/etherythingbiig:distributionRegions.0=us-east-1"

PIPELINE_NAME="etherythingbiigImagepipeline"
PIPELINE_ARN=$(aws imagebuilder list-image-pipelines \
--filters "name=name,values=${PIPELINE_NAME}" ${CDK_DEPLOY_PROFILE_FLAG} \
| jq -r '.imagePipelineList[0].arn')

aws imagebuilder start-image-pipeline-execution --image-pipeline-arn $PIPELINE_ARN ${CDK_DEPLOY_PROFILE_FLAG} ${CDK_DEPLOY_EXTRA_CONTEXT}
