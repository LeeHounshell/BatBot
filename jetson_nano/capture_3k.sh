#!/bin/bash

image_name=${1}

if [ "$image_name" == "" ]
then
    image_name="/tmp/capture_3k.jpg"
fi

gst-launch-1.0 nvarguscamerasrc num-buffers=1 ! 'video/x-raw(memory:NVMM),width=3264, height=2464, framerate=21/1, format=NV12' ! nvvidconv flip-method=0 ! 'video/x-raw' ! nvvidconv ! jpegenc ! filesink location=${image_name} > /dev/null 2> /dev/null

