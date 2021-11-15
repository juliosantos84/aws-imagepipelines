#! /bin/bash

set -e

MVN_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
RECIPE_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion=${MVN_VERSION}"
COMPONENT_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:componentVersion=${MVN_VERSION}"

# export CDK_DEPLOY_ACCOUNT=""
# export CDK_DEPLOY_REGION=""
# export CDK_DEPLOY_EXTRA_CONTEXT="-c everythingbiig-aws-imagepipelines/etherythingbiig:distributionRegions=['us-east-1']"
# export CDK_DEPLOY_PROFILE_FLAG="--profile your profile"

echo -e "Diffing pipeline version ${MVN_VERSION}"
echo -e "Extra options:\n\t${RECIPE_VERSION}\n\t${COMPONENT_VERSION}\n\t${CDK_DEPLOY_EXTRA_CONTEXT}"

cdk diff etherythingbiigImagePipeline \
--require-approval never ${CDK_DEPLOY_PROFILE_FLAG} ${RECIPE_VERSION} ${COMPONENT_VERSION} ${CDK_DEPLOY_EXTRA_CONTEXT}