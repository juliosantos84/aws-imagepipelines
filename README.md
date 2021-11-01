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
- prometheus
- cloudwatch agent

### monitoring
The pipeline creates a default CloudWatch config in SSM Parameter store and the pipeline starts the agent using:
```
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c ssm:cloudwatch-config
```

## New Pipelines
Extend `AbstractImagePipeline` and add components in `src/main/resources/imagebuilder/<pipeline name>`.