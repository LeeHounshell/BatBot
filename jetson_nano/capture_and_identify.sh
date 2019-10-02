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

echo
if pgrep -x "identify.py" > /dev/null
then
    ./capture.sh ${image_name} ${resolution}
    python3 ./request_identify.py ${image_name} 2> /dev/null
else
    echo "starting the identify server.."
    echo "please wait 1 minute."
    echo
    echo "then retry."
    echo "the first request will be slow."
    echo "after that it speeds up."
    nohup ./identify.py > /dev/null 2>&1 &
fi
