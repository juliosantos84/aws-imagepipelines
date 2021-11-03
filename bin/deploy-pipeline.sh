#! /bin/bash

set -e

MVN_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

RECIPE_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion=${MVN_VERSION}"
COMPONENT_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:componentVersion=${MVN_VERSION}"

# if [ ! -z "${MVN_VERSION}" ]; then
#     RECIPE_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion=${MVN_VERSION}"
#     COMPONENT_VERSION="-c everythingbiig-aws-imagepipelines/etherythingbiig:componentVersion=${MVN_VERSION}"
#     echo -e "Overriding versions with\n\t${RECIPE_VERSION}\n\t${COMPONENT_VERSION}"
# fi

# VERSION=${1:-$(cat cdk.json | jq -r '.context["everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion"]')}

# echo "Updating project version to ${VERSION}"
# mvn versions:set -DnewVersion=${VERSION}

echo -e "Deploying version ${MVN_VERSION}:\n\textra options: ${RECIPE_VERSION}\n\t${COMPONENT_VERSION}"
cdk deploy etherythingbiigImagePipeline \
--require-approval never ${RECIPE_VERSION} ${COMPONENT_VERSION}