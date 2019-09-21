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

camera_angle   = 90

uparrow        = 'F' # Foward
downarrow      = 'B' # Back
rightarrow     = 'R' # Right
leftarrow      = 'L' # Left

lookright      = '1' # Function 1
lookahead      = '2' # Function 2
lookleft       = '3' # Function 3

lookfullright  = '4' # Function 4
map_world      = '5' # Function 5
lookfullleft   = '6' # Function 6

slower         = '7' # Function 7
sensors        = '8' # Function 8
faster         = '9' # Function 9

monitor        = '0' # Function 0

settime        = 'X' # Time
allstop        = 'H' # Halt
run_star       = '*' # Star
run_sharp      = '#' # Sharp

game_w         = 'W'
game_a         = 'A'
game_s         = 'S'
game_d         = 'D'

hostname = socket.gethostname()
IPAddr = socket.gethostbyname(hostname)


def batbot_help():
    data = 'commands i know:\n'
    data = data + 'look ahead, '
    data = data + 'look right, '
    data = data + 'look left, '
    data = data + 'forward, '
    data = data + 'back, '
    data = data + 'right, '
    data = data + 'left, '
    data = data + 'stop, '
    data = data + 'faster, '
    data = data + 'slower, '
    data = data + 'follow, '
    data = data + 'avoid, '
    data = data + 'sensors, '
    data = data + 'identify, '
    data = data + 'learn, '
    data = data + 'map, '
    data = data + 'monitor, '
    data = data + 'photo, '
    data = data + 'find, '
    data = data + 'name, '
    data = data + 'IP address, '
    data = data + 'ping, '
    data = data + 'and help.\n\n'
    return data

def readDataFromArduino():
    robot_data = ''
    # Serial read section
    arduino.flush()
    if (arduino.inWaiting() > 0):
        robot_data = ''
        try:
            # read all characters in buffer
            robot_data = arduino.read(arduino.inWaiting()).decode('ascii')
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
        command = str(command_array[i])
        encoded_command = command.encode()
        arduino.write(encoded_command)
        print(encoded_command)

        i = i + 1
    else:
        print("done.")
    time.sleep(1) # I shortened this to match the new value in your Arduino code
    data = data + readDataFromArduino()
    return data

def do_star():
    command_array = [run_star]
    print("-> star.")
    result = executeCommands(command_array)
    return result

def do_stop():
    command_array = [allstop]
    print("-> stop.")
    result = executeCommands(command_array)
    return result

def do_sharp():
    command_array = [run_sharp]
    print("-> sharp.")
    result = executeCommands(command_array)
    return result

def set_arduino_time():
    arduino.flush()
    timedata = readDataFromArduino()
    command = settime
    encoded_command = command.encode()
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
    timedata = timedata + readDataFromArduino()
    return timedata

def data_received(commandsFromPhone):
    global camera_angle
    commandList = commandsFromPhone.splitlines()
    for data in commandList:
        print('$ ' + data)
        result = ''
        if 'ping' in data:
            result = set_arduino_time()
            result = result + batbot_help()
            print('ping ok.')
        elif 'IP address' in data:
            result = result + 'host=' + hostname + ', IP Address=' + IPAddr
            print(result)
        elif 'click: *' in data:
            print('---> button * <---')
            result = do_star()
        elif 'click: ok' in data:
            print('---> button ok <---')
            result = do_stop()
        elif 'click: #' in data:
            print('---> button # <---')
            result = do_sharp()
        elif 'forward' in data:
            data = 'forward.'
            print(data)
            command_array = [uparrow]
            result = executeCommands(command_array)
        elif 'back' in data:
            data = 'backward.'
            print(data)
            command_array = [downarrow]
            result = executeCommands(command_array)
        elif 'look ahead' in data:
            data = 'look ahead.'
            print(data)
            command_array = [lookahead]
            result = executeCommands(command_array)
            camera_angle = 90
        elif 'look right' in data:
            data = 'look right.'
            print(data)
            if camera_angle == 45:
                command_array = [lookfullright]
                result = executeCommands(command_array)
                camera_angle = 0
            else:
                command_array = [lookright]
                result = executeCommands(command_array)
                camera_angle = 45
        elif 'right' in data:
            data = 'right.'
            print(data)
            command_array = [rightarrow]
            result = executeCommands(command_array)
        elif 'look left' in data:
            data = 'look left.'
            print(data)
            if camera_angle == 90 + 45:
                command_array = [lookfullleft]
                result = executeCommands(command_array)
                camera_angle = 180 
            else:
                command_array = [lookleft]
                result = executeCommands(command_array)
                camera_angle = 90 + 45
        elif 'left' in data:
            data = 'left.'
            print(data)
            command_array = [leftarrow]
            result = executeCommands(command_array)
        elif 'stop' in data:
            data = 'stop.'
            print(data)
            result = do_stop()
        elif 'faster' in data:
            data = 'faster.'
            print(data)
            command_array = [faster]
            result = executeCommands(command_array)
        elif 'slower' in data:
            data = 'slower.'
            print(data)
            command_array = [slower]
            result = executeCommands(command_array)
        elif 'follow' in data:
            data = 'follow.' # FIXME: run Elegoo line following
            print(data)
            result = do_sharp()
        elif 'avoid' in data:
            data = 'avoid.' # FIXME: run Elegoo collision avoidance
            print(data)
            result = do_star()
        elif 'sensors' in data:
            data = 'sensors.'
            print(data)
            command_array = [sensors]
            result = executeCommands(command_array)
        elif 'identify' in data:
            data = 'identify.' # FIXME: identify what robot is looking at now
            print(data)
            result = 'FIXME: learn to identify'
        elif 'learn' in data:
            data = 'learn.' # FIXME: next word teaches last item's real name
            print(data)
            result = 'FIXME: learn about object'
        elif 'map' in data:
            data = 'map.' # FIXME: map the world
            print(data)
            command_array = [map_world]
            result = executeCommands(command_array)
        elif 'monitor' in data:
            data = 'monitor.' # FIXME: run the security monitor
            print(data)
            command_array = [monitor]
            result = executeCommands(command_array)
        elif 'photo' in data:
            data = 'photo.' # FIXME: optional next word is item to photograph
            print(data)
            result = 'FIXME: take a picture'
        elif 'find' in data:
            data = 'find.' # FIXME: next word is thing to find/search for
            print(data)
            result = 'FIXME: find some object'
        elif 'name' in data:
            result = 'i am ' + hostname + '. i live at ' + IPAddr
            print(result)
        elif 'help' in data:
            data = batbot_help()
            print(data)

        #--------------------------------------------
        if len(result) > 0:
            s.send(data + '\n' + result)
        else:
            # don't echo back the movement commands
            if (not data.startswith('2,')):
                s.send(data)
        #--------------------------------------------


#bd = BlueDot()
#
#def move(pos):
#    command_array = []
#    if pos.top:
#        command_array = [uparrow]
#        print("forward.")
#    elif pos.bottom:
#        command_array = [downarrow]
#        print("backward.")
#    elif pos.left:
#        command_array = [leftarrow]
#        print("left.")
#    elif pos.right:
#        command_array = [rightarrow]
#        print("right.")
#    elif pos.middle:
#        command_array = [allstop]
#        print("stop.")
#    if len(command_array) > 0:
#        #result = executeCommands(command_array)
#        print("fake execute")
#
#bd.when_pressed = move
#bd.when_moved = move
#bd.when_released = stop


try:
    s = BluetoothServer(data_received)
    print('---> waiting for connection <---')
    pause()
except Exception as e:
    print("ERROR: e=" + str(e))

