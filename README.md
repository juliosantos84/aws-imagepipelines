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

### Overview
Enhances the base pipeline by copying `/assets/etherythingbiig/{scripts,services}` onto the image.

### Usage
The application stack needs to configure which services to run during the cloud-init phase:

```bash
# example cloud init commands

# configure the environment
sudo ln -s /etc/systemd/system/geth/geth.service.testnet.env /etc/systemd/system/geth.service.env

# enable the service and start immediately
sudo systemctl enable --now geth.service
```

See [ethereum-beacon-chain-aws](https://github.com/juliosantos84/ethereum-beacon-chain-aws.git) for a working example.

### monitoring
The pipeline creates an SSM Parameter called `cloudwatch-config` and starts the agent during installation:
```
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c ssm:cloudwatch-config
```

## New Pipelines
Extend `AbstractImagePipeline` and add components in `src/main/resources/imagebuilder/<pipeline name>`.