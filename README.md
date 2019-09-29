an experimental AI vision robot
===============================

# BatBot

 This is a powerful AI research robot created from commodity parts. No soldering required. Lower half is an Elegoo Robot Car v3.0. Upper half is a Jetson Nano. An Android app controls it with Spoken English over Bluetooth. Robot has a camera, ultrasonic sensors and a 40 pin GPIO available. AI vision pattern recognition software in the Jetson Nano controls the robot's behavior. High-level spoken commands like 'FIND AND PHOTOGRAPH SOME-OBJECT' or 'IDENTIFY THAT OBJECT' instruct the robot to find and photograph and identify objects. Low-level spoken commands like 'WHAT IS YOUR IP-ADDRESS?' or 'GO FORWARD' or 'MOVE THE CAMERA LEFT' will obtain information and/or control the robot directly.

 The companion Android app is a MVVM pattern written in Kotlin and Java. It uses the Bluetooth/BlueDot work by Martin O'Hanlon for joystick controls and for communication to/from the Jetson Nano.

 The Jetson Nano is programmed with Python and uses the BlueDot Bluetooth library, the PySerial library, the ImageAI library and dependencies. The Nano runs the AI visual pattern recognition and behavior control software and uses 'ImageAI' to tie it together. That lets you easily change the deep learninging approach being tested.  There is no need for a LCD display to report an IP address; the Bluetooth companion app using speech recognition magic will show the Nano's IP.

 This project uses 'ImageAI' and includes the pre-trained 'yolo-tiny.h5' from the 'ImageAI' project.  See https://github.com/OlafenwaMoses/ImageAI and https://stackabuse.com/object-detection-with-imageai-in-python/

 This project also includes a modified version of BlueDot by Martin O'Hanlon. You can find BlueDot here: https://github.com/martinohanlon/BlueDot

 The Elegoo Arduino Robot Car is programmed in 'C' and uses Serial over USB communication.  It acts like a 'slave' for the Jetson Nano, carrying out mostly low-level tasks and reporting back. The Arduino and Nano communicate via the Serial cable connecting them. Note that when any object gets closer than 10cm to the distance sensor, the robot automatically stops.


## The BatBot

![screen](../master/screens/batbot.png)

## Initial screen from companion Android app:

![screen](../master/screens/BatBot_connect.jpg)

## The app connected screen:

The large oval bat image functions as a virtual joystick.  If you press it, a blue circle appears under your finger, and a larger circular boundry area also displays. The finger tracking image moves as you move your finger, within boundry limits. The log area shows the last 7 lines or so of log data from the Jetson Nano. Log data includes voice commands, status, state, joystick movement, clicks and more. Displayed data currently scrolls offscreen as new log data arrives.

![screen](../master/screens/BatBot_connected.jpg)

## An example voice command and response:

Here the command 'Show sensor values' was spoken. The recognized spoken text appears under the joystick and above the logs section, in white bold text. That text also acts as a 'repeat' button, so just click it to repeat your last voice command. You can see results of voice command execution in the log section and in the robot's behavior.

![screen](../master/screens/BatBot_voice_command.jpg)

