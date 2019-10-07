#!/bin/bash

printf
SERVER=`pgrep -x "identify.py"`

if [ "$SERVER" != "" ]
then
    printf "killing the 'identify' server.."
    kill ${SERVER}
else
    printf "the 'identify' server is not running."
fi

