#!/usr/bin/python3

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

command_array = [uparrow_str, downarrow_str, rightarrow_str, leftarrow_str, allstop_str, game_w_str, game_a_str, game_s_str, game_d_str, allstop_str, allstop_str]

i = 0
while (i < len(command_array)):
    # Serial read section
    ard.flush()
    msg = ard.read(ard.inWaiting()) # read all characters in buffer
    print("Message from arduino: ")
    print(msg)

    # Serial write section
    ard.flush()
    print("Python value sent: ")
    encoded_command = command_array[i].encode();
    ard.write(encoded_command)
    print(encoded_command)
    time.sleep(1) # I shortened this to match the new value in your Arduino code

    i = i + 1
    time.sleep(2)
else:
    print("Exiting")

exit()

