#!/usr/bin/python3

# Copyright (c) 2019, Lee Hounshell. All rights reserved.

from bluedot.btcomm import BluetoothServer
from datetime import datetime
from signal import pause

import serial
import socket
import struct
import subprocess
import time
import re
import os


#The following line is for serial over GPIO
port = '/dev/ttyACM0' # note I'm using Jetson Nano

arduino = serial.Serial(port,9600,timeout=5)
time.sleep(3) # wait for Arduino

K3_RESOLUTION     = '3k'
HD_RESOLUTION     = 'hd'
SD_RESOLUTION     = 'sd'

TIME_TO_LOOK      = 5
thing_to_find     = ''
thing_capturetime = 0

threat_detected   = False
security_count    = 0
security_time     = 0
security_image    = '/tmp/security{}.jpg'

resolution        = HD_RESOLUTION # one of [K3_RESOLUTION, HD_RESOLUTION, SD_RESOLUTION]
algorithmList     = ['SqueezeNet', 'DenseNet', 'InceptionV3', 'ResNet']
algorithmIndex    = 1 # 'DenseNet' is default
algorithmFixed    = False
capture_command   = '@UPLOAD '
delete_command    = '@DELETE '
capture_image     = '/tmp/capture{}.jpg'
capture_count     = 0
sending_now       = False

camera_angle      = 90

uparrow           = 'F'  # Foward
downarrow         = 'B'  # Back
rightarrow        = 'R'  # Right
rightspin         = 'I'  # Spin Right
leftarrow         = 'L'  # Left
leftspin          = 'J'  # Spin Left

lookright         = '1'  # Function 1
lookahead         = '2'  # Function 2
lookleft          = '3'  # Function 3

lookfullright     = '4'  # Function 4
map_world         = '5'  # Function 5
lookfullleft      = '6'  # Function 6

slower            = '7'  # Function 7
values            = '8'  # Function 8
faster            = '9'  # Function 9

monitor           = '0'  # Function 0

settime           = 'X'  # Time
setjoystick       = 'Y'  # Joystick
allstop           = 'H'  # Halt
run_star          = '*'  # Star
run_sharp         = '#'  # Sharp

SLD_MAX_VALUE     = 80.0
JOY_MAX_VALUE     = 70.0
JOY_NULL_REGION   = 10.0

joystick          = 0 # maps to the 90 degree camera_angle
slider            = 5 # maps to the 90 degree camera_angle
prev_slider       = 5
prev_joystick     = 0
state             = 'default'
joy_direction     = 'stopped'
slider_direction  = 'stopped'

hostname = socket.gethostname()
IPAddr = socket.gethostbyname(hostname)


def batbot_help():
    data = '\n! ---> some key words i know:\n! '
    data = data + 'look ahead, '
    data = data + 'look right, '
    data = data + 'look left, '
    data = data + 'forward, '
    data = data + 'back, '
    data = data + 'right, '
    data = data + 'left, '
    data = data + 'spin, '
    data = data + 'stop, '
    data = data + 'faster, '
    data = data + 'slower, '
    data = data + 'follow, '
    data = data + 'find [object], '
    data = data + 'avoid, '
    data = data + 'identify, '
    data = data + 'learn, '
    data = data + 'map, '
    data = data + 'monitor, '
    data = data + 'high/medium/low resolution, '
    data = data + 'photo, '
    data = data + 'sensor values, '
    data = data + 'fortune, '
    data = data + 'IP address, '
    data = data + 'ping, '
    data = data + 'my name, '
    data = data + 'algorithm, '
    data = data + 'kill server, '
    data = data + 'and help.\n\n'
    return data

def read_data_from_arduino():
    arduino.flush()
    robot_data = ''
    if arduino.inWaiting() > 0:
        robot_data = ''
        try:
            # read all characters in buffer
            robot_data = arduino.read(arduino.inWaiting()).decode('ascii')
            print(robot_data.strip())
            arduino.flush()
        except Exception as e:
            print("ERROR: read_data_from_arduino: e=" + str(e))
    return robot_data

def read_all_data_from_arduino():
    arduino.flush()
    result = ''
    while True:
        data = read_data_from_arduino()
        if len(data) == 0:
            break
        result = result + data
    return result

def write_commands(command_array):
    i = 0
    while (i < len(command_array)):
        arduino.flush()
        command = str(command_array[i])
        encoded_command = command.encode()
        arduino.write(encoded_command)
        #print(encoded_command)
        i = i + 1

def execute_commands(command_array):
    i = 0
    data = ''
    while (i < len(command_array)):
        data = data + read_data_from_arduino()

        # Serial write section
        arduino.flush()
        print("--> wrote command: ")
        command = str(command_array[i])
        encoded_command = command.encode()
        arduino.write(encoded_command)
        print(encoded_command)

        i = i + 1

    time.sleep(1) # I shortened this to match the new value in your Arduino code
    data = data + read_all_data_from_arduino()
    return data

def do_star():
    global state
    command_array = [run_star]
    result = execute_commands(command_array)
    result = set_state('avoid')
    return result

def do_stop():
    global state
    global threat_detected
    command_array = [allstop]
    result = execute_commands(command_array)
    result = set_state('default')
    threat_detected = False
    return result

def do_sharp():
    global state
    command_array = [run_sharp]
    result = execute_commands(command_array)
    result = set_state('following')
    return result

# i am sure there is a better way to do this
def run_command(arg0, arg1 = '', arg2 = '', arg3 = '', arg4 = ''):
    command = arg0
    if len(str(arg1)) > 0:
        command = command + ' ' + str(arg1)
        if len(str(arg2)) > 0:
            command = command + ' ' + str(arg2)
            if len(str(arg3)) > 0:
                command = command + ' ' + str(arg3)
                if len(str(arg4)) > 0:
                    command = command + ' ' + str(arg4)
    p = subprocess.Popen(command,
                         shell=True,
                         stdout=subprocess.PIPE,
                         stderr=subprocess.STDOUT)
    return iter(p.stdout.readline, b'')

def arduino_write_integer(value):
    # send 4 bytes of integer in network byte order
    commdata = arduino.write(struct.pack('>L', value))
    time.sleep(1) # wait for Arduino
    arduino.flush()
    result = read_all_data_from_arduino()
    return result

def set_arduino_time():
    arduino.flush()
    command = settime
    encoded_command = command.encode()
    arduino.write(encoded_command)
    arduino.flush()
    time.sleep(2)
    arduino.flush()
    result = read_all_data_from_arduino()
    #print("result=" + result)
    time.sleep(2)
    now = datetime.now()
    timestamp = 'T' + str(int(datetime.timestamp(now))) + '\n'
    encoded_timestamp = timestamp.encode()
    arduino.write(encoded_timestamp)
    arduino.flush()
    #print("SET_TIME offset sent: ")
    #print(timestamp)
    result = result + read_all_data_from_arduino()
    return result

def send_joystick_09_to_arduino(joystick):
    print("DBG: send_joystick_09_to_arduino=" + str(joystick))
    command = setjoystick
    encoded_command = command.encode()
    arduino.write(encoded_command)
    # send 4 bytes of integer in network byte order
    now = datetime.now()
    commdata = arduino.write(struct.pack('>L', joystick))
    print("SET_JOYSTICK sent: " + str(joystick))

def move_arduino_using_joystick(direction):
    print("DBG: move_arduino_using_joystick=" + direction)
    if direction == 'STOP':
        command_array = [allstop]
        write_commands(command_array)
    elif direction == 'AHEAD':
        command_array = [uparrow]
        write_commands(command_array)
    elif direction == 'BACK':
        command_array = [downarrow]
        write_commands(command_array)
    elif direction == 'RIGHT':
        command_array = [rightarrow]
        write_commands(command_array)
    elif direction == 'LEFT':
        command_array = [leftarrow]
        write_commands(command_array)

def change_camera_angle_using_slider_value(slider, slider_direction):
    print("DBG: change_camera_angle_using_slider_value=" + str(slider) + ", direction=" + slider_direction)
    go_right = slider_direction == 'RIGHT'
    if slider == 0:
        pass
    elif slider == 1:
        camera_angle = 90
        command_array = [lookahead]
        write_commands(command_array)
    elif slider == 2:
        camera_angle = 90
        command_array = [lookahead]
        write_commands(command_array)
    elif slider == 3:
        if go_right:
            camera_angle = 55
            command_array = [lookright]
        else:
            camera_angle = 125
            command_array = [lookleft]
        write_commands(command_array)
    elif slider == 4:
        if go_right:
            camera_angle = 55
            command_array = [lookright]
        else:
            camera_angle = 125
            command_array = [lookleft]
        write_commands(command_array)
    elif slider == 5:
        if go_right:
            camera_angle = 55
            command_array = [lookright]
        else:
            camera_angle = 125
            command_array = [lookleft]
        write_commands(command_array)
    elif slider == 6:
        if go_right:
            camera_angle = 55
            command_array = [lookright]
        else:
            camera_angle = 125
            command_array = [lookleft]
        write_commands(command_array)
    elif slider == 7:
        if go_right:
            camera_angle = 20
            command_array = [lookfullright]
        else:
            camera_angle = 160
            command_array = [lookfullleft]
        write_commands(command_array)
    elif slider == 8:
        if go_right:
            camera_angle = 20
            command_array = [lookfullright]
        else:
            camera_angle = 160
            command_array = [lookfullleft]
        write_commands(command_array)
    elif slider == 9:
        if go_right:
            camera_angle = 20
            command_array = [lookfullright]
        else:
            camera_angle = 160
            command_array = [lookfullleft]
        write_commands(command_array)

def process_blue_dot_slider(movementCommand):
    movementData = movementCommand.split(',')
    global camera_angle
    global slider
    global prev_slider
    global slider_direction
    try:
        if len(movementData) >= 3:
            x = int(float(movementData[1]) * 100)
            if x > 0:
                slider_direction = 'RIGHT'
            else:
                slider_direction = 'LEFT'

            # assume the 'slider' value be be between 0 and ~80
            # we will scale that to be from 0 to 9 like this:
            # x/9 = slider/80. solve for x. any x > 9 becomes 9

            slider = int((abs(x) / SLD_MAX_VALUE) * 9)
            if slider > 9:
                slider = 9
            print("DBG: calculated slider=" + str(slider))
            print("DBG: calculated slider_direction=" + slider_direction)

            if slider != prev_slider:
                prev_slider = slider
                change_camera_angle_using_slider_value(slider, slider_direction)

    except Exception as e:
        print("ERROR: process_blue_dot_slider: e=" + str(e))
    return str(slider)

def process_blue_dot_joystick(movementCommand, isRelease):
    movementData = movementCommand.split(',')
    global joy_direction
    global joystick
    global prev_joystick
    try:
        if len(movementData) >= 3:
            x = 0
            y = 0
            try:
                x = int(float(movementData[1]) * 100)
                y = int(float(movementData[2]) * 100)
                print("DBG: x=" + str(x) + ", y=" + str(y))
            except Exception as bad:
                print("ERROR: process_blue_dot_joystick: problem x/y: bad=" + str(bad))
                return "INVALID X/Y"

            if abs(x) <= JOY_NULL_REGION and abs(y) <= JOY_NULL_REGION:
                joy_direction = 'STOP'
            elif x > 0:
                if y > 0:
                    if x > y:
                        joystick = x - JOY_NULL_REGION
                        joy_direction = 'RIGHT'
                    else:
                        joystick = y - JOY_NULL_REGION
                        joy_direction = 'AHEAD'
                else:
                    if x > abs(y):
                        joystick = x - JOY_NULL_REGION
                        joy_direction = 'RIGHT'
                    else:
                        joystick = abs(y) - JOY_NULL_REGION
                        joy_direction = 'BACK'
            else:
                if y > 0:
                    if abs(x) > y:
                        joystick = abs(x) - JOY_NULL_REGION
                        joy_direction = 'LEFT'
                    else:
                        joystick = abs(y) - JOY_NULL_REGION
                        joy_direction = 'AHEAD'
                else:
                    if abs(x) > abs(y):
                        joystick = abs(x) - JOY_NULL_REGION
                        joy_direction = 'LEFT'
                    else:
                        joystick = abs(y) - JOY_NULL_REGION
                        joy_direction = 'BACK'

            # assume the 'joystick' value be be between 0 and ~70
            # we will scale that to be from 0 to 9 like this:
            # x/9 = joystick/70. solve for x. any x > 9 becomes 9

            joystick = int((joystick / JOY_MAX_VALUE) * 9)
            if joystick > 9:
                joystick = 9

            if isRelease:
                joy_direction = 'STOP'

            print("DBG: calculated joystick=" + str(joystick))
            print("DBG: calculated joy_direction=" + joy_direction)

            if isRelease or joystick != prev_joystick:
                prev_joystick = joystick
                send_joystick_09_to_arduino(joystick)
                move_arduino_using_joystick(joy_direction)

    except Exception as e:
        print("ERROR: process_blue_dot_joystick: e=" + str(e))
    return str(joystick) + ' ' + joy_direction

def show_algorithm_hint(result):
    result = result + "\n! Say 'kill server' to do that."
    result = result + '\n! once the \'identify\' server starts, '
    result = result + '\n! the AI algorithm is fixed until restart.'
    result = result + '\n! \n! The current algorithm is ' + algorithmList[algorithmIndex]
    return result

def send_image_to_phone(filename, begin_header, end_header):
    global sending_now
    try:
        sending_now = True
        s.send(begin_header)
        time.sleep(1) # wait for Android
        image_data = open(filename, "rb").read()
        print("IMAGE SIZE: " + str(len(image_data)))
        s._send_data(image_data)
        time.sleep(2) # wait for Android
        s.send(end_header)
        time.sleep(1) # wait for Android
    except Exception as e:
        print("ERROR: in send_image_to_phone: e=" + str(e))
    sending_now = False

def set_state(new_state):
    global state
    state_info = ''
    if state == 'security':
        if new_state != 'security':
            state_info = '\n! ===> SECURITY MODE DISABLED.'
    state = new_state
    return state_info

def command_contains(wordList, command):
    for word in wordList:
        if word not in command:
            return False
    return True

#
# TODO: refactor this thing
#       note the syntax checking dependency on test order
#
# a primitive language parser
def data_received(commandsFromPhone):
    global resolution
    global camera_angle
    global state
    global capture_count
    global algorithmIndex
    global algorithmFixed
    global thing_to_find
    global thing_capturetime
    global security_time
    global security_count
    global threat_detected

    if sending_now:
        print("\n===> WARNING: data_received while sending_now: " + commandsFromPhone)
        return

    pokeLogs = (commandsFromPhone == ' ')
    commandList = commandsFromPhone.splitlines()
    for data in commandList:
        # decode the command in 'data'

        if data.startswith(capture_command):
           filename = data[len(capture_command):]
           print("\n*** SENDING IMAGE '" + filename + "' TO PHONE ***")
           send_image_to_phone(filename, "~BEGIN CAPTURE~", "~END CAPTURE~")
           print("*** IMAGE '" + filename + "' SENT TO PHONE ***\n")
           continue

        if data.startswith(delete_command):
           filename = data[len(delete_command):]
           if filename.startswith('/tmp/'):
               print("\n*** REQUEST DELETE IMAGE '" + filename + "' ***\n")
               try:
                   os.remove(filename)
               except Exception as e:
                    print("ERROR: in '@DELETE' data_received: e=" + str(e))
           continue

        printResult = False
        valid = False
        result = read_data_from_arduino()

        if pokeLogs:
            data = '' # don't echo the space used to poke the logs
            result = result + read_all_data_from_arduino()
            valid = True

        elif 'ping' in data:
            result = result + batbot_help()
            printResult = True
            valid = True

        elif command_contains(['IP', 'address'], data) or command_contains(['system', 'info'], data):
            result = result + '\n! Hostname: ' + hostname + '\n! IP Address: ' + IPAddr
            printResult = True
            valid = True

        elif 'click: *' in data:
            data = '*'
            result = result + do_star()
            printResult = True
            valid = True

        elif 'click: ok' in data:
            data = 'ok'
            result = result + do_stop()
            printResult = True
            valid = True

        elif 'click: #' in data:
            data = '#'
            result = result + do_sharp()
            printResult = True
            valid = True

        elif command_contains(['look', 'ahead'], data):
            command_array = [lookahead]
            result = result + execute_commands(command_array)
            printResult = True
            camera_angle = 90
            valid = True

        elif command_contains(['look', 'right'], data) or command_contains(['focus', 'right'], data):
            if camera_angle == 45:
                command_array = [lookfullright]
                result = result + execute_commands(command_array)
                camera_angle = 10
            else:
                command_array = [lookright]
                result = result + execute_commands(command_array)
                camera_angle = 45
            printResult = True
            valid = True

        elif command_contains(['look', 'left'], data) or command_contains(['focus', 'left'], data):
            if camera_angle == 135:
                command_array = [lookfullleft]
                result = result + execute_commands(command_array)
                camera_angle = 170
            else:
                command_array = [lookleft]
                result = result + execute_commands(command_array)
                camera_angle = 135
            printResult = True
            valid = True

        elif command_contains(['spin', 'right'], data) or command_contains(['spin', 'around'], data) or command_contains(['spinrite'], data):
            command_array = [rightspin]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif command_contains(['spin', 'left'], data) or command_contains(['turn', 'around'], data):
            command_array = [leftspin]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif 'right' in data:
            command_array = [rightarrow]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif 'left' in data:
            command_array = [leftarrow]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif 'forward' in data or 'ahead' in data:
            command_array = [uparrow]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif 'back' in data or 'reverse' in data:
            command_array = [downarrow]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif 'stop' in data or 'halt' in data:
            result = result + do_stop()
            printResult = True
            valid = True

        elif 'faster' in data or 'speed up' in data:
            command_array = [faster]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif 'slower' in data or command_contains(['slow', 'down'], data):
            command_array = [slower]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif 'sensor' in data or 'value' in data:
            command_array = [values]
            result = result + execute_commands(command_array)
            printResult = True
            valid = True

        elif 'fortune' in data or 'joke' in data:
            # sudo apt-get install fortunes
            result = result + '\n'
            for line in run_command('/usr/games/fortune'):
                try:
                    text = line.decode('ascii')
                    result = result + '! ' + text
                except Exception as e:
                    print("ERROR: in 'fortune' data_received: e=" + str(e))
            printResult = True
            valid = True

        elif 'follow' in data: # enable simple Elegoo line following
            result = result + do_sharp()
            printResult = True
            valid = True

        elif 'avoid' in data: # enable simple Elegoo collision avoidance 
            result = result + do_star()
            printResult = True
            valid = True

        elif 'monitor' in data or 'security' in data:
            command_array = [monitor]
            result = result + execute_commands(command_array)
            result = result + "\n! ===> SECURITY MONITOR RUNNING!"
            result = result + set_state('security')
            printResult = True
            valid = True

        #
        # TODO: implement better search movement than 'collision avoidance mode'
        #       use a database history to determine where to search first.
        #
        elif 'find' in data or 'search' in data or 'locate' in data:
            if thing_to_find != '':
                result = result + "\n! I was looking for a '" + thing_to_find + "'"
                result = result + "\n! but will abandon that search now."

            thing_to_find = data.split()[-1] # get the last word
            result = result + "\n! I will look for a '" + thing_to_find + "'"
            result = result + "\n! and will tell you when I find it.\n"
            thing_capturetime = 0
            result = result + do_star() # set 'collision avoidance mode'
            printResult = True
            valid = True

        elif command_contains(['increase', 'resolution'], data) or command_contains(['higher', 'resolution'], data):
            if resolution == HD_RESOLUTION:
                resolution = K3_RESOLUTION
                result = result + '\n! ==> Use 3K Images\n! 3264 by 2464 pixels'
            elif resolution == SD_RESOLUTION:
                resolution = HD_RESOLUTION
                result = result + '\n! ==> Use HD Images\n! 1920 by 1024 pixels'
            else:
                result = result + '\n! already using the highest resolution.'
                result = result + '\n! ==> Use 3K Images\n! 3264 by 2464 pixels'
            printResult = True
            valid = True

        elif command_contains(['decrease', 'resolution'], data) or command_contains(['lower', 'resolution'], data):
            if resolution == HD_RESOLUTION:
                resolution = SD_RESOLUTION
                result = result + '\n! ==> Use SD Images\n! 960 by 616 pixels'
            elif resolution == K3_RESOLUTION:
                resolution = HD_RESOLUTION
                result = result + '\n! ==> Use HD Images\n! 1920 by 1024 pixels'
            else:
                result = result + '\n! already using the lowest resolution.'
                result = result + '\n! ==> Use SD Images\n! 960 by 616 pixels'
            printResult = True
            valid = True

        elif command_contains(['high', 'resolution'], data) or command_contains(['3K', 'resolution'], data):
            resolution = K3_RESOLUTION
            result = result + '\n! ==> Use 3K Images\n! 3264 by 2464 pixels'
            printResult = True
            valid = True

        elif command_contains(['medium', 'resolution'], data) or command_contains(['normal', 'resolution'], data):
            resolution = HD_RESOLUTION
            result = result + '\n! ==> Use HD Images\n! 1920 by 1024 pixels'
            printResult = True
            valid = True

        elif command_contains(['low', 'resolution'], data) or command_contains(['standard', 'resolution'], data):
            resolution = SD_RESOLUTION
            result = result + '\n! ==> Use SD Images\n! 960 by 616 pixels'
            printResult = True
            valid = True

        elif 'resolution' in data:
            result = result + '\n! ==> the resolution is set at ' + resolution
            result = result + "\n! \n! resolutions I know about:"
            result = result + "\n! HD (default) at 1920x1024"
            result = result + "\n! SD at 960x616"
            result = result + "\n! 3K at 3264x2464"
            printResult = True
            valid = True

        elif 'photo' in data or 'download' in data or 'picture' in data or command_contains(['capture', 'image'], data):
            result = result + '\n'
            capture_count += 1
            image_path = capture_image.format(capture_count)
            for line in run_command('./capture.sh', image_path, resolution):
                try:
                    text = line.decode('ascii')
                    result = result + '! ' + text
                except Exception as e:
                    print("ERROR: in 'photo' data_received: e=" + str(e))
            result = result + '\n'
            printResult = True
            valid = True

        elif 'show algorithm' in data:
            result = result + "\n! AI algorithm set to '" + algorithmList[algorithmIndex] + "'\n"
            result = result + "\n! \n! AI algorithms I know about:"
            for potential_algorithm in algorithmList:
                result = result + "\n! " + potential_algorithm
            printResult = True
            valid = True

        elif command_contains(['previous', 'algorithm'], data):
            if algorithmFixed:
                result = result + show_algorithm_hint(result)
            else:
                algorithmIndex -= 1
                if algorithmIndex <= -1:
                    algorithmIndex = len(algorithmList) - 1
                result = result + "\n! AI algorithm set to '" + algorithmList[algorithmIndex] + "'\n"
            printResult = True
            valid = True

        elif command_contains(['next', 'algorithm'], data):
            if algorithmFixed:
                result = result + show_algorithm_hint(result)
            else:
                algorithmIndex += 1
                if algorithmIndex >= len(algorithmList):
                    algorithmIndex = 0
                result = result + "\n! AI algorithm set to '" + algorithmList[algorithmIndex] + "'\n"
            printResult = True
            valid = True

        elif 'algorithm' in data:
            result = result + '\n! ==> the AI algorithm is set to ' + algorithmList[algorithmIndex]
            result = result + "\n! \n! AI algorithms I know about:"
            for potential_algorithm in algorithmList:
                result = result + "\n! " + potential_algorithm
            printResult = True
            valid = True

        elif 'identify' in data or command_contains(['what', 'looking'], data) or command_contains(['start', 'server'], data):
            algorithmFixed = True
            result = result + '\n'
            capture_count += 1
            image_path = capture_image.format(capture_count)
            for line in run_command('./capture_and_identify.sh', image_path, resolution, algorithmList[algorithmIndex]):
                try:
                    text = line.decode('ascii')
                    result = result + '! ' + text
                except Exception as e:
                    print("ERROR: in 'identify' data_received: e=" + str(e))
            result = result + '\n'
            printResult = True
            valid = True

        elif command_contains(['kill', 'server'], data):
            algorithmFixed = False
            result = result + '\n'
            for line in run_command('./kill_identify.sh'):
                try:
                    text = line.decode('ascii')
                    result = result + '! ' + text
                except Exception as e:
                    print("ERROR: in 'kill' data_received: e=" + str(e))
            result = result + '\n'
            printResult = True
            valid = True

        elif 'learn' in data:
            image_path = ''
            image_name = ''
            if not data.startswith('@TRAIN'):
                image_name = data.split()[-1] # get the last word
                capture_count += 1
                image_path = capture_image.format(capture_count)
                for line in run_command('./capture.sh', image_path, resolution):
                    try:
                        text = line.decode('ascii')
                        result = result + '! ' + text
                    except Exception as e:
                        print("ERROR: in 'learn' data_received: e=" + str(e))
                result = result + '\n'
            else:
                # e.g. --> @TRAIN '/tmp/capture22.jpg' learn='Lee'
                items = re.findall("([^']*)", data)
                image_path = items[2]
                image_name = items[6]
            if image_name != '' and image_path != '':
                for line in run_command('./learn_about.sh', image_path, image_name.strip().replace(" ", "_"), algorithmList[algorithmIndex]):
                    try:
                        text = line.decode('ascii')
                        result = result + '! ' + text
                    except Exception as e:
                        print("ERROR: in 'learn' data_received: e=" + str(e))
                result = result + '\n'
            else:
                result = result + "\n! THE 'LEARN' COMMAND FAILED!"
                result = result + "\n! \n! I tried to understand: "
                result = result + "\n! " + data
            printResult = True
            valid = True

        #
        # TODO: map the world. keep the map in a searchable database.
        #       enable 'find' to use the map to locate objects.
        #       create 'GO TO [PLACENAME]' and 'REMEMBER [PLACENAME]' commands.
        #
        elif 'map' in data:
            command_array = [map_world]
            result = result + execute_commands(command_array)
            result = result + set_state('map')
            printResult = True
            valid = True

        elif 'name' in data:
            result = result + '\n! i am ' + hostname + '. i live at ' + IPAddr
            printResult = True
            valid = True

        elif 'hello' in data or 'batbot' in data:
            result = result + '\n! Hello.'
            printResult = True
            valid = True

        elif 'help' in data or 'commands' in data:
            result = batbot_help()
            printResult = True
            valid = True

        elif 'get' in data or 'make' in data:
            result = result + '\n! Build me some hands.'
            printResult = True
            valid = True

        #--------------------------------------------

        #
        #TODO: keep a history of everything we see inside a database.
        #      map the world and recognize current location in map.
        #      use history and map to quickly find objects already seen.
        #
        # check if we are looking for something
        if thing_to_find != '':
            now = time.time()
            elapsed = now - thing_capturetime
            if elapsed > TIME_TO_LOOK:
                printResult = True
                print("\nlooking for: '" + thing_to_find + "'")
                capture_count += 1
                image_path = capture_image.format(capture_count)
                capture_results = run_command('./capture_and_identify.sh', image_path, resolution, algorithmList[algorithmIndex])
                thing_capturetime = time.time()
                found = False
                for line in capture_results:
                    try:
                        text = line.decode('ascii')
                        result = result + text
                        if thing_to_find in text:
                            found = True
                    except Exception as e:
                        print("ERROR: in 'find' data_received: e=" + str(e))
                if found:
                    for line in capture_results:
                        try:
                            text = line.decode('ascii')
                            result = result + "! " + text
                        except Exception as e:
                            print("ERROR: in 'find' data_received: e=" + str(e))
                    result = result + "\n! \n! I found a '" + thing_to_find + "'!"
                    thing_to_find = ''
                    thing_capturetime = 0

        #--------------------------------------------

        # check if security monitor is running
        if state == 'security':
            now = time.time()
            elapsed = now - security_time
            if elapsed > TIME_TO_LOOK:
                printResult = True
                security_count += 1
                image_path = security_image.format(security_count)
                result = result + "\nchecking security..\n"
                detected = False
                threat = ''
                for line in run_command('./security_monitor.sh', image_path, security_count, resolution, threat_detected):
                    try:
                        text = line.decode('ascii')
                        if detected:
                            result = result + "! " + text
                        elif threat_detected: # only send the Android app 1 notification, but keep capturing
                            result = result + text
                        elif 'DETECTED' in text.upper():
                            detected = True
                            threat_detected = True
                            threat = "! " + text
                        else:
                            result = result + text
                    except Exception as e:
                        print("ERROR: in 'learn' data_received: e=" + str(e))
                result = result + threat
                security_time = time.time()

        #--------------------------------------------

        # talk to Android
        data = data.strip()
        if len(data) > 0:

            # don't echo back the movement commands
            if not valid:
                if state == 'default':
                    if data.startswith('2,'): # BlueDot onMove
                        data = 'JOYSTICK: ' + process_blue_dot_joystick(data, False)
                        result = result + read_all_data_from_arduino()
                        valid = True
                    elif data.startswith('1,'): # BlueDot onPress
                        data = 'CLICK: ' + process_blue_dot_joystick(data, False)
                        result = result + read_all_data_from_arduino()
                        valid = True
                    elif data.startswith('0,'): # BlueDot onRelease
                        data = 'RELEASE: ' + process_blue_dot_joystick(data, True)
                        result = result + read_all_data_from_arduino()
                        valid = True
                else:
                    if data.startswith('2,'): # BlueDot onMove
                        data = 'SLIDER: ' + process_blue_dot_slider(data)
                        result = result + read_all_data_from_arduino()
                        valid = True
                    elif data.startswith('1,'): # BlueDot onPress
                        data = 'CLICK: ' + process_blue_dot_slider(data)
                        result = result + read_all_data_from_arduino()
                        valid = True
                    elif data.startswith('0,'): # BlueDot onRelease
                        data = 'RELEASE: ' + process_blue_dot_slider(data)
                        result = result + read_all_data_from_arduino()
                        valid = True

            if valid:
                data = '--> ' + data
            else:
                # ignore bluedot numbers here
                if data[0].isdigit() or data[0] in ['-', ',', '.']:
                    data = ''
                else:
                    data = '??? ' + data
            print(data)

        else:
            arduino.flush()
        if len(result) > 0:
            result = result + read_all_data_from_arduino()
            if printResult:
                print(result.strip())
        if len(data) > 0:
            s.send(data + '\n' + result)
        else:
            s.send(result)
        #--------------------------------------------

def client_connect():
    print("*** BLUETOOTH CLIENT CONNECTED ***")

def client_disconnect():
    print("*** BLUETOOTH CLIENT DISCONNECTED ***")


result = read_all_data_from_arduino()
#print(result)

result = set_arduino_time()
#print("ARDUINO TIME: " + result)
result = read_all_data_from_arduino()
#print(result)

time.sleep(3)
result = read_all_data_from_arduino()
#print(result)

result = do_stop()
#print(result)
result = read_all_data_from_arduino()
#print(result)


while True:
    try:
        print('===> CREATING BLUETOOTH SERVER <===')
    
        s = BluetoothServer(data_received_callback = data_received,
                when_client_connects=client_connect,
                when_client_disconnects=client_disconnect)
    
        print('---> waiting for connection <---')
        pause()
    
    except ConnectionAbortedError as ex:
        print("*** ERROR ***: got connection request for closed socket: ex=" + str(ex))
    except OSError as ex:
        print("*** ERROR ***: got mysterious OSError: ex=" + str(ex))
    except Exception as ex:
        print("*** PROGRAM ERROR ***: ex=" + str(ex))
    
