#!/bin/bash

image_name=${1}
if [ "$image_name" == "" ]
then
    image_name="/tmp/capture.jpg"
fi

gst-launch-1.0 nvarguscamerasrc num-buffers=1 ! 'video/x-raw(memory:NVMM),width=1920, height=1080, framerate=21/1, format=NV12' ! nvvidconv flip-method=0 ! 'video/x-raw,width=960, height=616' ! nvvidconv ! jpegenc ! filesink location=${image_name} > /dev/null 2> /dev/null

