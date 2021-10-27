#! /bin/bash

RECIPE_VERSION=""
COMPONENT_VERSION=""

if [ ! -z "${1}" ]; then
    RECIPE_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion=${VERSION}"
    COMPONENT_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:componentVersion=${VERSION}"
fi

VERSION=${1:-"from cdk.json"}

echo "Deploying versions ${VERSION}"
cdk deploy etherythingbiigImagePipeline \
--require-approval never ${RECIPE_VERSION} ${COMPONENT_VERSION}