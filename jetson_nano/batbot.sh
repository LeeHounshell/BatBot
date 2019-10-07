#!/bin/bash

while true
do
    ./batbot.py
    echo "BatBot exited with code $? - restarting BatBot in 20 seconds."
    sleep 20
done

