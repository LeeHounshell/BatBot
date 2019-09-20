an experimental AI vision robot
===============================

# BatBot

 This is a powerful AI research robot created from commodity parts. No soldering required. Lower half is an Elegoo Robot Car v3.0. Upper half is a Jetson Nano. An Android app controls it with Spoken English over Bluetooth. Robot has a camera, ultrasonic sensors and a 40 pin GPIO available. AI vision pattern recognition software in the Nano controls the robot's behavior. High-level spoken commands like 'FIND OBJECT' instruct the robot to find and photograph identified objects. Low-level spoken commands like 'WHAT IS YOUR IP-ADDRESS?' or 'GO FORWARD' will obtain information and control the robot directly.

 The companion Android app is a MVVM pattern written in Kotlin and Java with Bluetooth/BlueDot.
 The Nano is programmed with Python and uses Bluetooth/BlueDot and the PySerial library.
 The Nano runs the AI visual pattern recognition and behavior control software.
 The Elegoo Arduino Robot Car is programmed in 'C' and uses Serial over USB communication.
 It acts like a 'slave' for the Jetson Nano, carrying out tasks as requested and reporting back.

![screen](../master/screens/batbot.png)

