#!/usr/bin/python3

from bluedot.btcomm import BluetoothServer
from datetime import datetime
from signal import pause

import serial
import socket
import struct
import subprocess
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
values         = '8' # Function 8
faster         = '9' # Function 9

monitor        = '0' # Function 0

settime        = 'X' # Time
allstop        = 'H' # Halt
run_star       = '*' # Star
run_sharp      = '#' # Sharp

joy_nullregion = 20

hostname = socket.gethostname()
IPAddr = socket.gethostbyname(hostname)


def batbot_help():
    data = '\n--->: i recognize these key words:\n'
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
    data = data + 'find, '
    data = data + 'avoid, '
    data = data + 'values, '
    data = data + 'identify, '
    data = data + 'learn, '
    data = data + 'map, '
    data = data + 'monitor, '
    data = data + 'photo, '
    data = data + 'fortune, '
    data = data + 'name, '
    data = data + 'IP address, '
    data = data + 'ping, '
    data = data + 'and help.\n\n'
    return data

def read_data_from_arduino():
    robot_data = ''
    # Serial read section
    arduino.flush()
    if arduino.inWaiting() > 0:
        robot_data = ''
        try:
            # read all characters in buffer
            robot_data = arduino.read(arduino.inWaiting()).decode('ascii')
            print(robot_data.strip())
            arduino.flush()
        except Exception as e:
            print("read_data_from_arduino: WARNING: e=" + str(e))
    return robot_data

def execute_commands(command_array):
    i = 0
    data = ''
    while (i < len(command_array)):
        data = data + read_data_from_arduino()

        # Serial write section
        arduino.flush()
        print("--> wrote: ")
        command = str(command_array[i])
        encoded_command = command.encode()
        arduino.write(encoded_command)
        print(encoded_command)

        i = i + 1

    time.sleep(1) # I shortened this to match the new value in your Arduino code
    data = data + read_data_from_arduino()
    return data

def do_star():
    command_array = [run_star]
    result = execute_commands(command_array)
    return result

def do_stop():
    command_array = [allstop]
    result = execute_commands(command_array)
    return result

def do_sharp():
    command_array = [run_sharp]
    result = execute_commands(command_array)
    return result

def run_command(command):
    p = subprocess.Popen(command,
                         stdout=subprocess.PIPE,
                         stderr=subprocess.STDOUT)
    return iter(p.stdout.readline, b'')

def set_arduino_time():
    arduino.flush()
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
    timedata = read_data_from_arduino()
    return timedata

def decode_blue_dot(movementCommand):
    movementData = movementCommand.split(',')
    try:
        if len(movementData) >= 3:
            x = int(float(movementData[1]) * 100)
            y = int(float(movementData[2]) * 100)
            #pos = "X=" + str(x) + ", Y=" + str(y)
            joystick = 0
            dir = ''
            if abs(x) <= joy_nullregion and abs(y) <= joy_nullregion:
                dir = 'STOP'
            elif x > 0:
                if y > 0:
                    if x > y:
                        joystick = x - joy_nullregion
                        dir = 'RIGHT'
                    else:
                        joystick = y - joy_nullregion
                        dir = 'AHEAD'
                else:
                    if x > abs(y):
                        joystick = x - joy_nullregion
                        dir = 'RIGHT'
                    else:
                        joystick = abs(y) - joy_nullregion
                        dir = 'BACK'
            else:
                if y > 0:
                    if abs(x) > y:
                        joystick = abs(x) - joy_nullregion
                        dir = 'LEFT'
                    else:
                        joystick = abs(y) - joy_nullregion
                        dir = 'AHEAD'
                else:
                    if abs(x) > abs(y):
                        joystick = abs(x) - joy_nullregion
                        dir = 'LEFT'
                    else:
                        joystick = abs(y) - joy_nullregion
                        dir = 'BACK'

            # assume the 'joystick' value be be between 0 and ~70
            # we will scale that to be from 0 to 9 like this:
            # x/9 = joystick/70. solve for x. any x > 9 becomes 9

            joystick = int((joystick / 70) * 9)
            if joystick > 9:
                joystick = 9
            return str(joystick) + ' ' + dir
    except Exception as e:
        print("decode_blue_dot: WARNING: e=" + str(e))
    return "INVALID"

# a primitive language parser
def data_received(commandsFromPhone):
    global camera_angle
    pokeLogs = (commandsFromPhone == ' ')
    commandList = commandsFromPhone.splitlines()
    for data in commandList:
        result = read_data_from_arduino()
        printResult = False
        valid = False
        if pokeLogs:
            data = '' # don't echo the space used to poke the logs
            valid = True
        elif 'ping' in data:
            result = result + set_arduino_time()
            result = result + batbot_help()
            valid = True
        elif 'IP address' in data:
            result = result + 'host=' + hostname + ', IP Address=' + IPAddr
            printResult = True
            valid = True
        elif 'click: *' in data:
            data = '*'
            result = result + do_star()
            valid = True
        elif 'click: ok' in data:
            data = 'ok'
            result = result + do_stop()
            valid = True
        elif 'click: #' in data:
            data = '#'
            result = result + do_sharp()
            valid = True
        elif 'look ahead' in data:
            command_array = [lookahead]
            result = result + execute_commands(command_array)
            camera_angle = 90
            valid = True
        elif 'look right' in data or 'turn right' in data:
            if camera_angle == 45:
                command_array = [lookfullright]
                result = result + execute_commands(command_array)
                camera_angle = 0
            else:
                command_array = [lookright]
                result = result + execute_commands(command_array)
                camera_angle = 45
            valid = True
        elif 'right' in data:
            command_array = [rightarrow]
            result = result + execute_commands(command_array)
            valid = True
        elif 'look left' in data or 'turn left' in data:
            if camera_angle == 90 + 45:
                command_array = [lookfullleft]
                result = result + execute_commands(command_array)
                camera_angle = 180 
            else:
                command_array = [lookleft]
                result = result + execute_commands(command_array)
                camera_angle = 90 + 45
            valid = True
        elif 'left' in data:
            command_array = [leftarrow]
            result = result + execute_commands(command_array)
            valid = True
        elif 'forward' in data or 'ahead' in data:
            command_array = [uparrow]
            result = result + execute_commands(command_array)
            valid = True
        elif 'back' in data or 'reverse' in data:
            command_array = [downarrow]
            result = result + execute_commands(command_array)
            valid = True
        elif 'stop' in data or 'halt' in data:
            result = result + do_stop()
            valid = True
        elif 'faster' in data or 'speed up' in data:
            command_array = [faster]
            result = result + execute_commands(command_array)
            valid = True
        elif 'slower' in data or 'slow down' in data:
            command_array = [slower]
            result = result + execute_commands(command_array)
            valid = True
        elif 'sensor' in data or 'value' in data:
            command_array = [values]
            result = result + execute_commands(command_array)
            valid = True
        elif 'fortune' in data or 'joke' in data:
            # sudo apt-get install fortunes
            for line in run_command('/usr/games/fortune'):
                try:
                    text = line.decode('ascii')
                    result = result + text
                except Exception as e:
                    print("data_received: WARNING: e=" + str(e))
            printResult = True
            valid = True
        elif 'follow' in data: # FIXME: run Elegoo line following
            result = result + do_sharp()
            valid = True
        elif 'avoid' in data: # FIXME: run Elegoo collision avoidance
            result = result + do_star()
            valid = True
        elif 'monitor' in data or 'security' in data: # FIXME: security monitor
            command_array = [monitor]
            result = result + execute_commands(command_array)
            valid = True
        elif 'photo' in data or 'picture' in data: # FIXME: optional item
            result = result + 'FIXME: take a picture'
            valid = True
        elif 'find' in data or 'search' in data: # FIXME: next word is object
            result = result + 'FIXME: find some object'
            valid = True
        elif 'identify' in data: # FIXME: identify what robot is looking at
            result = result + 'FIXME: learn to identify'
            valid = True
        elif 'learn' in data: # FIXME: teach item name
            result = result + 'FIXME: learn about object'
            valid = True
        elif 'map' in data: # FIXME: map the world
            command_array = [map_world]
            result = result + execute_commands(command_array)
            valid = True
        elif 'name' in data:
            result = result + 'i am ' + hostname + '. i live at ' + IPAddr
            printResult = True
            valid = True
        elif 'help' in data or 'commands' in data:
            data = batbot_help()
            valid = True

        #--------------------------------------------
        data = data.strip()
        if len(data) > 0:

            # don't echo back the movement commands
            if not valid:
                if data.startswith('2,'): # BlueDot onMove
                    data = 'SPEED: ' + decode_blue_dot(data)
                    valid = True
                elif data.startswith('1,'): # BlueDot onPress
                    data = 'CLICK: ' + decode_blue_dot(data)
                    valid = True
                elif data.startswith('0,'): # BlueDot onRelease
                    data = 'RELEASE: ' + decode_blue_dot(data)
                    valid = True

            if valid:
                data = '--> ' + data.upper()
            else:
                # ignore bluedot numbers here
                if data[0].isdigit() or data[0] in ['-', ',', '.']:
                    data = ''
                else:
                    data = '??? ' + data.upper()
            print(data)

        else:
            arduino.flush()
        if len(result) > 0:
            result = result + read_data_from_arduino()
            if printResult:
                print(result.strip())
        if len(data) > 0:
            s.send(data + '\n' + result)
        else:
            s.send(result)
        #--------------------------------------------


try:
    s = BluetoothServer(data_received)
    print('---> waiting for connection <---')
    pause()
except Exception as e:
    print("PROGRAM ERROR: e=" + str(e))

