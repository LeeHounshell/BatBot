#!/bin/bash

image_name=${1}
resolution=${2}

if [ "$image_name" == "" ]
then
    image_name="/tmp/capture.jpg"
fi

if [ "$resolution" == "" ]
then
    resolution="hd"
fi

./capture.sh ${image_name} ${resolution}

python3 ./request_identify.py ${image_name} 2> /dev/null

