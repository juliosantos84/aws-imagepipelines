# Image Pipeline

# Deploying
Run `bin/deploy-pipeline.sh [version]`.

# Configuration
Edit `everythingbiig-aws-imagepipelines/etherythingbiig:*` in `/cdk.json` or provide overrides at commandline using the `-c` option.

# Pipelines

## EtherythingbiigImagePipeline
A pipeline to build the etherythingbiig image which includes:
- geth
- lighthouse (beacon + validator)

## New Pipelines
Extend `AbstractImagePipeline` and add components in `src/main/resources/imagebuilder/<pipeline name>`.