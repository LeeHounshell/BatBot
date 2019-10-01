#!/bin/bash

image_name=${1}
if [ "$image_name" == "" ]
then
    image_name="/tmp/capture.jpg"
fi

./capture.sh ${image_name} 2> /dev/null

python3 ./request_identify.py ${image_name} 2> /dev/null

