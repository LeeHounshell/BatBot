#!/bin/bash

image_name=${1}
resolution=${2}
algorithm=${3}

if [ "$image_name" == "" ]
then
    image_name="/tmp/capture.jpg"
fi

if [ "$resolution" == "" ]
then
    resolution="hd"
fi

if [ "$algorithm" == "" ]
then
    algorithm="DenseNet"
fi

if pgrep -x "identify.py" > /dev/null
then
    ./capture.sh ${image_name} ${resolution}
    python3 ./request_identify.py ${image_name} 2> /dev/null
else
    echo "starting the 'identify' server.."
    echo "algorithm set to: ${algorithm}"
    echo "please wait 1 minute.."
    echo
    echo "..and then retry.."
    echo "the first request will be *very* slow."
    echo "after that, it speeds up greatly."
    nohup ./identify.py ${algorithm} > /dev/null 2>&1 &
fi
