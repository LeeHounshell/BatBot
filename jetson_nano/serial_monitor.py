#!/usr/bin/python3

import serial	# http://pyserial.sf.net
import time
from datetime import datetime
import struct

port = '/dev/ttyACM0' # note I'm using Jetson Nano
arduino = serial.Serial(port, 9600, timeout=5)
time.sleep(2) # wait for Arduino

arduino.flush()
command = 'X' # SET TIME
encoded_command = command.encode();
arduino.write(encoded_command)
arduino.flush()
time.sleep(1)

now = datetime.now()
timestamp = int(datetime.timestamp(now))
commdata = arduino.write(struct.pack('>L', timestamp))
arduino.flush()
time.sleep(1)

print("SET_TIME offset sent: ")
print(str(timestamp))

while True:
    time.sleep(1) # wait for Arduino
    arduino.flush()
    msg = arduino.read(arduino.inWaiting()) # read all characters in buffer
    text = msg.decode().strip()
    if text != '':
        print(text)
