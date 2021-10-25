#! /bin/bash

VOLUME_MOUNT_PATH=$(cat /home/ubuntu/volume-mount-path)

# Mount the volume
echo "Mounting volume..." && sudo mkdir -p ${VOLUME_MOUNT_PATH} \
&& sudo mount /dev/nvme1n1 ${VOLUME_MOUNT_PATH}

echo "Creating subdirectories..." \
&& sudo mkdir -p ${VOLUME_MOUNT_PATH}/goethereum \
&& sudo mkdir -p ${VOLUME_MOUNT_PATH}/lighthouse/validators

echo "Setting ownership..." \
&& sudo chown -R goeth:goeth ${VOLUME_MOUNT_PATH}/goethereum \
&& sudo chown -R lighthousebeacon:lighthousebeacon ${VOLUME_MOUNT_PATH}/lighthouse \
&& sudo chown -R lighthousevalidator:lighthousevalidator ${VOLUME_MOUNT_PATH}/lighthouse/validators
