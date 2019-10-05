#!/bin/bash

echo "start-batbot.sh: WAITING TWO MINUTES FOR THE SYSTEM TO INITIALIZE!"

sleep 120  # WAIT TWO MINUTES!!! (so Bluetooth is ready)

echo "start-batbot.sh: THE SYSTEM IS READY.. START THE BATBOT!"

cd /home/lee/src/BatBot/jetson_nano

nohup /usr/bin/python3 ./batbot.py &

