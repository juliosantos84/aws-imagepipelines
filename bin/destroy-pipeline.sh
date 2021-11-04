#! /bin/bash

# export CDK_DEPLOY_ACCOUNT="account id"
# export CDK_DEPLOY_REGION="region"
# export CDK_DEPLOY_PROFILE_FLAG="--profile profilename"

cdk destroy etherythingbiigImagePipeline --force ${CDK_DEPLOY_PROFILE_FLAG}