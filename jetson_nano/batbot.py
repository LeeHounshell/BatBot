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
star_str       = '*' # Star
star           = 42  # Star
sharp_str      = '#' # Sharp
sharp          = 35  # Sharp

game_w         = 87
game_w_str     = 'W'
game_a         = 65
game_a_str     = 'A'
game_s         = 83
game_s_str     = 'S'
game_d         = 68
game_d_str     = 'D'

def readDataFromArduino():
    robot_data = ''
    # Serial read section
    ard.flush()
    if (ard.inWaiting() > 0):
        robot_data = ard.read(ard.inWaiting()).decode('ascii') # read all characters in buffer
        print("from arduino: ")
        print(robot_data)
    return robot_data

def executeCommands(command_array):
    i = 0
    data = ''
    while (i < len(command_array)):
        data = data + readDataFromArduino()

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
    data = data + readDataFromArduino()
    return data


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
        #result = executeCommands(command_array)
        print("fake execute");

def stop():
    command_array = [allstop_str]
    print("stop.")
    result = executeCommands(command_array)

def data_received(data):
    print(data)
    result = ''
    if data == 'click: *\n':
        print("---> * <---");
        command_array = [star_str]
        result = executeCommands(command_array)
    elif data == 'click: ok\n':
        print("---> ok <---");
        command_array = [allstop_str]
        result = executeCommands(command_array)
    elif data == 'click: #\n':
        print("---> # <---");
        command_array = [sharp_str]
        result = executeCommands(command_array)
    if len(result) > 0:
        s.send(data + '\n' + result)
    else:
        s.send(data)

#bd.when_pressed = move
#bd.when_moved = move
#bd.when_released = stop

while True:
    try:
        s = BluetoothServer(data_received)
        pause()
    except Exception as e:
        print("ERROR: e=" + str(e))
    time.sleep(2)


