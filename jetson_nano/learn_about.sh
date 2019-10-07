#!/bin/bash

image_path=${1}
image_name=${2}
algorithm=${3}

if [ "$image_path" == "" ]
then
    echo "usage: learn_about.sh path name algorithm"
    exit 0
fi

if [ "$image_name" == "" ]
then
    echo "usage: learn_about.sh path name algorithm"
    exit 0
fi

#
# TODO: actually train the model..
# see: https://github.com/OlafenwaMoses/ImageAI/blob/master/imageai/Prediction/CUSTOMTRAINING.md
#

# save the request for batch training
printf "TRAIN: ${image_path} NAME: ${image_name} ALGORITHM: ${algorithm}" >> "/tmp/BatBot_TRAIN.txt"

printf "I saved the photo of a '${image_name}' for training with '${algorithm}'.\n"
