#! /bin/bash

PIPELINE_NAME="etherythingbiigImagepipeline"
PIPELINE_ARN=$(aws imagebuilder list-image-pipelines \
--filters "name=name,values=${PIPELINE_NAME}" \
| jq -r '.imagePipelineList[0].arn')

aws imagebuilder start-image-pipeline-execution --image-pipeline-arn $PIPELINE_ARN
