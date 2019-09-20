BATBOT: an experimental AI vision robot
=======================================

# BatBot

This is a robot created from commodity parts. Lower half is an Elegoo Robot Car v3.0. Upper half is a Jetson Nano. An Android app controls it with Spoken English over Bluetooth. Robot has a camera, ultrasonic sensors and a 40 pin GPIO available. AI vision pattern-recognition software in the Nano controls the robot's behavior. High-level spoken commands like 'GO FIND <OBJECT>" instruct the robot to find and photograph. The Android app is a MVVM pattern written in Kotlin and Java. The Nano is programmed with Python. The Arduino Robot Car is programmed in 'C'.

![screen](../master/screens/batbot.png)

