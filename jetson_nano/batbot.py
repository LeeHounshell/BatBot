#!/usr/bin/python3

from bluedot.btcomm import BluetoothServer
from bluedot import BlueDot
from datetime import datetime
from signal import pause

import serial
import socket
import struct
import time


#The following line is for serial over GPIO
port = '/dev/ttyACM0' # note I'm using Jetson Nano

arduino = serial.Serial(port,9600,timeout=5)
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

hostname = socket.gethostname()
IPAddr = socket.gethostbyname(hostname)

def readDataFromArduino():
    robot_data = ''
    # Serial read section
    arduino.flush()
    if (arduino.inWaiting() > 0):
        robot_data = ''
        try:
            robot_data = arduino.read(arduino.inWaiting()).decode('ascii') # read all characters in buffer
            print("from arduino: ")
            print(robot_data)
        except Exception as e:
            print("ERROR: e=" + str(e))
    return robot_data

def executeCommands(command_array):
    i = 0
    data = ''
    while (i < len(command_array)):
        data = data + readDataFromArduino()

        # Serial write section
        arduino.flush()
        print("python sent: ")
        encoded_command = command_array[i].encode();
        arduino.write(encoded_command)
        print(encoded_command)

        i = i + 1
    else:
        print("done.")
    time.sleep(1) # I shortened this to match the new value in your Arduino code
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

def set_arduino_time(data):
    time.sleep(2) # wait for Arduino
    arduino.flush()
    command = 'X' # SET TIME
    encoded_command = command.encode();
    arduino.write(encoded_command)
    arduino.flush()
    time.sleep(1)
    # send 4 bytes of integer in network byte order
    now = datetime.now()
    timestamp = int(datetime.timestamp(now))
    commdata = arduino.write(struct.pack('>L', timestamp))
    arduino.flush()
    # wait a bit
    time.sleep(1)
    print("SET_TIME offset sent: ")
    print(str(timestamp))
    data = data + readDataFromArduino()
    return data

def data_received(commandsFromPhone):
    commandList = commandsFromPhone.splitlines()
    for data in commandList:
        print('$ ' + data)
        result = ''
        if "ping" in data:
            result = readDataFromArduino()
            result = set_arduino_time(result)
            print("ping ok.");
        elif 'IP address' in data:
            result = "host=" + hostname + ", IP Address=" + IPAddr;
            print(result)
        elif 'click: *' in data:
            print("---> * <---");
            command_array = [star_str]
            result = executeCommands(command_array)
        elif 'click: ok' in data:
            print("---> ok <---");
            command_array = [allstop_str]
            result = executeCommands(command_array)
        elif 'click: #' in data:
            print("---> # <---");
            command_array = [sharp_str]
            result = executeCommands(command_array)

        # FIXME: replace this code
        elif "forward" in data:
            data = "forward.";
            print(data);
            command_array = [uparrow_str]
            result = executeCommands(command_array)
        elif "backup" in data:
            data = "backward.";
            print(data);
            command_array = [downarrow_str]
            result = executeCommands(command_array)
        elif "right" in data:
            data = "right.";
            print(data);
            command_array = [rightarrow_str]
            result = executeCommands(command_array)
        elif "left" in data:
            data = "left.";
            print(data);
            command_array = [leftarrow_str]
            result = executeCommands(command_array)
        elif "stop" in data:
            data = "stop.";
            print(data);
            command_array = [allstop_str]
            result = executeCommands(command_array)

        #--------------------------------------------
        if len(result) > 0:
            s.send(data + '\n' + result)
        else:
            # don't echo back the movement commands
            if (not data.startswith('2,')):
                s.send(data)
        #--------------------------------------------


#bd.when_pressed = move
#bd.when_moved = move
#bd.when_released = stop

try:
    s = BluetoothServer(data_received)
    print('---> waiting for connection <---')
    pause()
except Exception as e:
    print("ERROR: e=" + str(e))

