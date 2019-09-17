#!/usr/bin/python3

from bluedot.btcomm import BluetoothServer
from bluedot import BlueDot
from signal import pause

import serial
import time

#The following line is for serial over GPIO
port = '/dev/ttyACM0' # note I'm using Jetson Nano

ard = serial.Serial(port,9600,timeout=5)
time.sleep(2) # wait for Arduino

uparrow        = 70  # Foward
uparrow_str    = 'F' # Foward
downarrow      = 66  # Back
downarrow_str  = 'B' # Back
rightarrow     = 82  # Right
rightarrow_str = 'R' # Right
leftarrow      = 76  # Left
leftarrow_str  = 'L' # Left
allstop        = 72  # Halt
allstop_str    = 'H' # Halt

game_w         = 87
game_w_str     = 'W'
game_a         = 65
game_a_str     = 'A'
game_s         = 83
game_s_str     = 'S'
game_d         = 68
game_d_str     = 'D'

def executeCommands(command_array):
    i = 0
    while (i < len(command_array)):
        # Serial read section
        ard.flush()
        msg = ard.read(ard.inWaiting()) # read all characters in buffer
        print("from arduino: ")
        print(msg)

        # Serial write section
        ard.flush()
        print("python sent: ")
        encoded_command = command_array[i].encode();
        ard.write(encoded_command)
        print(encoded_command)
        time.sleep(1) # I shortened this to match the new value in your Arduino code

        i = i + 1
    else:
        print("done.")


#bd = BlueDot()

def move(pos):
    command_array = []
    if pos.top:
        command_array = [uparrow_str]
        print("forward.")
    elif pos.bottom:
        command_array = [downarrow_str]
        print("backward.")
    elif pos.left:
        command_array = [leftarrow_str]
        print("left.")
    elif pos.right:
        command_array = [rightarrow_str]
        print("right.")
    elif pos.middle:
        command_array = [allstop_str]
        print("stop.")
    if len(command_array) > 0:
        #executeCommands(command_array)
        print("fake execute");

def stop():
    command_array = [allstop_str]
    print("stop.")
    executeCommands(command_array)

def data_received(data):
    print(data)
    s.send(data)

#bd.when_pressed = move
#bd.when_moved = move
#bd.when_released = stop

s = BluetoothServer(data_received)

pause()

