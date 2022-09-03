#! /bin/bash

VOLUME_MOUNT_PATH=$(cat /home/ubuntu/volume-mount-path)

echo "Creating subdirectories..." \
&& sudo mkdir -p ${VOLUME_MOUNT_PATH}/goethereum \
&& sudo mkdir -p ${VOLUME_MOUNT_PATH}/lighthouse/validators \
&& sudo mkdir -p ${VOLUME_MOUNT_PATH}/beaconshared

echo "Setting ownership..." \
&& sudo chown -R goeth:goeth ${VOLUME_MOUNT_PATH}/goethereum \
&& sudo chown -R lighthousebeacon:lighthousebeacon ${VOLUME_MOUNT_PATH}/lighthouse \
&& sudo chown -R lighthousevalidator:lighthousevalidator ${VOLUME_MOUNT_PATH}/lighthouse/validators \
&& sudo chown -R :beaconshared ${VOLUME_MOUNT_PATH}/beaconshared