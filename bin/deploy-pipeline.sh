#! /bin/bash

RECIPE_VERSION=""
COMPONENT_VERSION=""

if [ ! -z "${1}" ]; then
    RECIPE_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion=${1}"
    COMPONENT_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:componentVersion=${1}"
fi

VERSION=${1:-"from cdk.json"}

echo "Deploying version ${VERSION} with\n ${RECIPE_VERSION} ${COMPONENT_VERSION}"
cdk deploy etherythingbiigImagePipeline \
--require-approval never ${RECIPE_VERSION} ${COMPONENT_VERSION}