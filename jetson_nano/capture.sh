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

if [ "$resolution" == "hd" ]
then
    ./capture_hd.sh ${image_name} 2> /dev/null
elif [ "$resolution" == "3k" ]
then
    ./capture_3k.sh ${image_name} 2> /dev/null
else
    ./capture_sd.sh ${image_name} 2> /dev/null
fi

echo "File: ${image_name}"
image_size=`ls -l "${image_name}" | sed 's/  */:/g' | cut -f5 -d:`
echo "Size: ${image_size}"
