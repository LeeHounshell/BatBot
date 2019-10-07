an experimental AI vision robot
===============================

# BatBot

 This is a powerful AI research robot created from commodity parts. Lower half is an Elegoo Robot Car v3.0. The upper half is a Jetson Nano. An Android app controls it with Spoken English translated and sent over Bluetooth. The robot has a camera, an ultrasonic distance sensor and an unused 40 pin GPIO available for expansion. Software in the Jetson Nano communicating with the robot's Arduino control behavior. High-level spoken commands like 'WHAT ARE YOU LOOKING AT?' instruct the robot photograph and identify objects. The command 'GO FIND SOME-OBJECT' instructs the robot to locate, identify and photograph an object. Low-level spoken commands like 'WHAT IS YOUR IP-ADDRESS?' or 'GO FORWARD' or 'LOOK TO THE LEFT' will obtain information and/or control the robot directly. Teach BatBot to identify new objects by using voice commands from the Android app.

 The robot has five 'operating modes':

- default (accept commands from Android and the IR remote)
- collision avoidance (try not to hit anything. optionally search for an object by name.)
- line following (try and follow a black-tape line.)
- security monitor (detect, photograph and report any detected motion)
- map the world (try and map out everything around the robot)

 The companion Android app is a MVVM pattern written in Kotlin and Java. It uses the Bluetooth/BlueDot work by Martin O'Hanlon for virtual joystick controls and for communication to/from the Jetson Nano and Android. Get BlueDot here: https://github.com/martinohanlon/BlueDot  

 The Jetson Nano is programmed with Python and also uses the BlueDot Bluetooth library. Additionally it uses PySerial, ImageAI and numerous dependency libraries. The Jetson Nano runs AI visual pattern recognition and behavior control software. It uses 'ImageAI' to process image content. Included are four pre-trained models from the 'ImageAI' project. See https://github.com/OlafenwaMoses/ImageAI and https://stackabuse.com/object-detection-with-imageai-in-python/  The layered design of this robot's functions lets you easily change the learning approach being used/tested.

 The Elegoo Arduino Robot Car is programmed in 'C' and uses Serial over USB communication.  It acts like a 'slave' for the Jetson Nano, carrying out mostly low-level tasks and reporting back. The Arduino and Nano communicate via the Serial cable connecting them.

 Note that when any object gets closer than 10cm to the distance sensor, the robot automatically stops and changes to 'default' mode. The idea is to try and prevent damage.


## The BatBot

This robot was built using the Elegoo Robot Car v3.0 as the main base; then piling on top of that the Jetson Nano (inside a case), a Samsung 500 Gig SSD, a phone-battery power supply and the camera, attached to the ultrasonic sensor. The figure-head is from a PEZ dispenser. No soldering is required to build this project. No 3D printing is required either. All parts can be ordered online or found in your local hardware store.

The README.md in the 'robot' folder of this project lists the parts needed to make BatBot.
[Robot README](https://github.com/LeeHounshell/BatBot/blob/master/robot/README.md)

The README.md in the 'jetson_nano' folder of this project details the AI architecture.
[Jetson Nano README](https://github.com/LeeHounshell/BatBot/blob/master/jetson_nano/README.md)

The README.md in the 'batbot_app' folder of this project details the Android architecture.
[Android app README](https://github.com/LeeHounshell/BatBot/blob/master/batbot_app/README.md)

See https://www.elegoo.com/product/arduinocarv3-0/ for the Elegoo Car details.

![screen](../master/screens/batbot.png)


## spoken commands

Because keywords are used and the language domain is small, there is freedom in phrasing spoken commands to BatBot. Here are some examples to try:

ping the batbot.
what is your IP address?
show system info.
look straight ahead.
look to the right.
focus right.
look left.
focus to the left.
spin right.
spin around.
spin to the left.
turn around.
go right.
drive left.
forward.
go straight ahead.
go back.
reverse direction.
stop now.
halt, robot.
go faster.
speed up.
go slower.
slower, please.
slow down.
show sensor values.
tell my fortune.
tell a really good joke.
follow the line.
avoid collisions.
begin monitor mode.
set security mode.
find OBJECT.
search for OBJECT.
locate OBJECT.
increase camera resolution.
use higher resolution.
decrease resolution.
set a lower resolution.
high resolution.
3K resolution.
use medium resolution.
set normal resolution.
low resolution.
use standard resolution.
show me the resolution.
get a photo.
take a picture.
capture an image.
show the algorithm setting.
set previous algorithm.
use the next algorithm.
what algorithms do you know?
identify that thing.
what are your looking at?
start the server.
kill the server.
learn OBJECT.
set map mode.
what is your name?
hello, BabBot.
help!
get me a beer.
make some coffee.


## connecting to the robot

The Android app starts with a selection screen for the BatBot Bluetooth connection.  When the app first connects, a 'help' popup displays with command hints.

![screen](../master/screens/BatBot_connect.jpg)
![screen](../master/screens/BatBot_connected.jpg)


## primary user interface

 The main user interface consists of a yellow and black bat-image virtual joystick controller, and 5 buttons. The buttons work as follows:

 - [voice command text] translated text acts like a 'repeat command' button.
 - [*] set the operating mode to 'collision avoidance'.
 - [ok] stop the robot and change the operating mode to 'normal'.
 - [#] set the operating mode to 'line following'.
 - [Speak a Command or Help] use Google Voice Recognition to enter a robot command.

The *, ok and # buttons work the same as matching buttons on the IR remote control.
Note that all IR buttons are mapped to appropriate robot functionality.
Press the 'ok' button anytime to stop the robot and return to 'default' mode.

The large oval bat-image widget functions as a virtual joystick.  If you press it, a small blue circle appears and tracks under your finger -- inside a larger circular boundry that also appears. By moving your finger up and down the robot will move forward and backward. The further your finger from the joystick's center, the faster the robot goes. If you move your finger right and left, the robot turns right or left. Again the further your finger is from the joystick center, the faster it goes. Note that if the robot is put into another 'mode' of operation, for example 'line following mode' or 'collision avoidance mode' or 'surveillance mode' or 'map mode' then the joystick functionality changes also. Each operation mode has separate joystick functionality.

Above the control buttons is a window into the Jetson Nano's 'batbot.py' log. The log area shows the last 7 lines or so of log data, scrolling up. Log data includes voice commands, status, state, joystick movement, clicks and more. The displayed data scrolls offscreen (and is not retained) as new log data arrives.


## example voice command and response:

Here the command 'What is your IP address?' was spoken. The recognized spoken text appears under the joystick and above the logs section, in white bold text. A popup window shows the command results. Using Bluetooth eliminates need for a LCD display on the robot. Translated text also acts as a 'repeat' button for the command.

![screen](../master/screens/BatBot_voice_command.jpg)
![screen](../master/screens/BatBot_IP_address.jpg)


## example of AI image recognition and training

The following screens show what might happen if you ask 'what are you looking at?'

BatBot's Jetson Nano runs a separate 'identity' server that determines image content on request.  Initially the 'identify' server is not running, but it will start after the first image identification request or after saying 'start the server now.' The 'identify' server starts with a specified AI 'algorithm' (model). The chosen AI model is changed using voice commands like 'next/previous algorithm' and 'kill server' when the 'identity' server is already running but you want to change the ImageAI model used..

![screen](../master/screens/BatBot_identify.jpg)
![screen](../master/screens/BatBot_start_server.jpg)

..1 minute later.. Now that the 'identify' server is running, we can ask 'what are you looking at?' again. A photo is taken and analyzed. The app shows photo analysis text right away. Note that the first request is slow, as shared librarys must load for the 'identify' server. Subsequent request are a couple of seconds.

![screen](../master/screens/BatBot_say_command.jpg)
![screen](../master/screens/BatBot_identify_results.jpg)

 The app asks if you want to download this image.  If 'View' is selected, the image will download via Bluetooth, and then display in a popup alongside the same analysis result. The app disables all buttons while downloading photos. After a few seconds, a popup will display containing the robot's photo. Then save images to the phone's Gallery or 'Train' the robot.

![screen](../master/screens/BatBot_transfer_image.jpg)
![screen](../master/screens/BatBot_identify_results_image.jpg)

Here we are training the robot to recognize me. :-)

![screen](../master/screens/BatBot_identify_results_train.jpg)
![screen](../master/screens/BatBot_identify_results_learn.jpg)

The training data is saved for offline batch submission later.

![screen](../master/screens/BatBot_training.jpg)

## ask BatBot to find an object

if you ask BatBot to 'find a table.':

![screen](../master/screens/BatBot_find_table.jpg)
![screen](../master/screens/BatBot_find_table_response.jpg)

then change your mind and ask BatBot to 'find a chair.':

![screen](../master/screens/BatBot_find_chair.jpg)
![screen](../master/screens/BatBot_find_chair_response.jpg)

## ask BatBot to be a security monitor

if you ask BatBot to 'enable security monitor.':

![screen](../master/screens/BatBot_security_monitor.jpg)
![screen](../master/screens/BatBot_security_monitor_response.jpg)

then when movement is detected, you are notified:

![screen](../master/screens/BatBot_movement_detected.jpg)
![screen](../master/screens/BatBot_security_threat.jpg)

## query BatBot sensor values

if you ask BatBot to 'show sensor values.':

![screen](../master/screens/BatBot_show_sensors.jpg)
![screen](../master/screens/BatBot_sensor_values.jpg)

## query BatBot's camera resolution

if you ask BatBot to 'show camera resolution.':

![screen](../master/screens/BatBot_show_resolution.jpg)
![screen](../master/screens/BatBot_resolution.jpg)

## query which ImageAI model/algorithm is being used

if you ask BatBot to 'show AI algorithm.':

![screen](../master/screens/BatBot_ask_AI_algorithm.jpg)
![screen](../master/screens/BatBot_AI_algorithm.jpg)


