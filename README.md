# Image Pipeline

# Deploying
Run `cdk deploy`.

# Configuration
Edit `everythingbiig-aws-imagepipelines/etherythingbiig:*` in `/cdk.json`.

# Pipelines

## EtherythingbiigImagePipeline
A pipeline to build the etherythingbiig image which includes:
- geth
- lighthouse (beacon + validator)

## New Pipelines
Extend `AbstractImagePipeline` and add components in `src/main/resources/imagebuilder/<pipeline name>`.