#!/usr/bin/python3

from bluedot.btcomm import BluetoothServer
from signal import pause
import time

def data_received(data):
    print(data)
    s.send(data)

while True:
    try:
        s = BluetoothServer(data_received)
        pause()
    except Exception as e:
        print("ERROR: e=" + str(e))
    time.sleep(2)
