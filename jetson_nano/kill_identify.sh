#!/bin/bash

echo
SERVER=`pgrep -x "identify.py"`

if [ "$SERVER" != "" ]
then
    echo "killing the 'identify' server.."
    kill ${SERVER}
else
    echo "the 'identify' server is not running."
fi

