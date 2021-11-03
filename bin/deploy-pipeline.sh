#! /bin/bash

set -e

RECIPE_VERSION=""
COMPONENT_VERSION=""

if [ ! -z "${1}" ]; then
    RECIPE_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion=${1}"
    COMPONENT_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:componentVersion=${1}"
    echo -e "Overriding versions with\n\t${RECIPE_VERSION}\n\t${COMPONENT_VERSION}"
fi

VERSION=${1:-$(cat cdk.json | jq -r '.context["everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion"]')}

echo "Updating project version to ${VERSION}"
mvn versions:set -DnewVersion=${VERSION}

echo "Deploying version ${VERSION}"
cdk deploy etherythingbiigImagePipeline \
--require-approval never ${RECIPE_VERSION} ${COMPONENT_VERSION}