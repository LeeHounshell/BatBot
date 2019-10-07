#!/bin/bash

while true
do
    ./batbot.py
    echo "BatBot exited with code $? - restarting BatBot."
done

