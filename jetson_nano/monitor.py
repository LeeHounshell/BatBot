#!/usr/bin/python3

import serial	# http://pyserial.sf.net
import time

port = '/dev/ttyACM0' # note I'm using Jetson Nano
ard = serial.Serial(port, 9600, timeout=5)
time.sleep(2) # wait for Arduino

#   command = 'H' # Halt
#   ard.flush()
#   print("Python value sent: ")
#   encoded_command = command.encode();
#   ard.write(encoded_command)
#   print(encoded_command)

while True:
    time.sleep(1) # wait for Arduino
    ard.flush()
    msg = ard.read(ard.inWaiting()) # read all characters in buffer
    text = msg.decode().strip()
    if text != '':
        print(text)
