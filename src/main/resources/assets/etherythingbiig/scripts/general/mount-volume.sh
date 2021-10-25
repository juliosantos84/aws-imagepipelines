#! /bin/bash

VOLUME_MOUNT_PATH=$(cat /home/ubuntu/volume-mount-path)
# VOLUME_MOUNT_PATH_OWNER=$(cat /home/ubuntu/volume-mount-path-owner)

# Mount the volume
echo "Mounting volume..." && sudo mkdir -p ${VOLUME_MOUNT_PATH} \
&& sudo mount /dev/nvme1n1 ${VOLUME_MOUNT_PATH}

echo "Creating subdirectories..." \
&& sudo mkdir -p ${VOLUME_MOUNT_PATH}/{lighthouse,goethereum}

echo "Setting ownership..." \
&& sudo chown -R goeth:goeth ${VOLUME_MOUNT_PATH}/goethereum \
&& sudo chown -R lighthousebeacon:lighthousebeacon ${VOLUME_MOUNT_PATH}/lighthouse
