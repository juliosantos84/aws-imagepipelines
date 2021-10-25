#! /bin/bash

NEW_VERSION=$(date +%s%3)
VERSION=${1:-NEW_VERSION}

echo "Deploying pipeline version ${VERSION}"
cdk deploy etherythingbiigImagePipeline \
-c everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion=${VERSION} \
-c everythingbiig-aws-imagepipelines/etherythingbiig:componentVersion=${VERSION}