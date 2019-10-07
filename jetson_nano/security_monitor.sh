#!/bin/bash

image_name=${1}
count=${2}
resolution=${3}
security_breach=${4}

image_prefix="/tmp/security"

if [ "$security_breach" == "" ]
then
    security_breach=false
fi

if [ "$resolution" == "" ]
then
    resolution="hd"
fi

if [ "$count" == "" ]
then
    count="1"
fi

if [ "$image_name" == "" ]
then
    image_name="${image_prefix}${count}.jpg"
fi

prev_count=`expr "$count" - 1`
prev_image="${image_prefix}${prev_count}.jpg"

if [ "$resolution" == "hd" ]
then
    ./capture_hd.sh ${image_name} 2> /dev/null
elif [ "$resolution" == "3k" ]
then
    ./capture_3k.sh ${image_name} 2> /dev/null
else
    ./capture_sd.sh ${image_name} 2> /dev/null
fi

if [ -f "$prev_image" ]
then
    # compare "$image_name" with "$prev_image"
    # if they are different then report movement
    compare_result=`./compare_images.py "${prev_image}" "${image_name}"` 2> /dev/null
    if [[ $compare_result == *"different"* ]]
    then
        #
        # Note: be careful editing this file. side-effects can happen:
        # The first output lines must be the printf line below.
        # If not, the Android app currently won't find the IMAGE_FILE_HEADER
        #
        image_size=`ls -l "${image_name}" | sed 's/  */:/g' | cut -f5 -d:`
        printf "SECURITY: MOVEMENT DETECTED!\nFile: ${image_name}\nSize: ${image_size}\n\n"
        security_breach=true
    else
        printf "SECURITY: no threats"
    fi
    if [ ! "$security_breach" == true ]
    then
        rm "${prev_image}"
    fi
else
    printf "SECURITY: initializing"
fi

