# BatBot app
This app controls the BatBot robot using Bluetooth.

## Description

The app first presents a screen with a list of Bluetooth devices nearby.
Select the BatBot and connect to it. The app initiates a connection with
BatBot and pops up an alert from BatBot showing 'help' instructions.

From the main interface, the BlueDot control moves the robot. If the robot
is in 'collision avoidance' mode or 'line following mode' then the BlueDot control
rotates the camera instead. Finger distance from the control's center determines movement speed.

The buttons labeled '*', 'ok' and '#' function the same as similar IR remote buttons.
The 'Speak a Command' button performs entry of voice commands.

When a voice command is spoken, Google translates that to text and that text appears
below the virtual joystick. Click the text to repeat a voice command without speaking.
The text is sent via Bluetooth to the Jetson Nano, where it gets decoded.

As you use the app, log information from both Jetson Nano and Arduino logs appear and scroll.
The log window shows what the robot is doing internally.
This log is updated as result of 'heartbeat' messages that go from Android to the Jetson Nano.

Any log messages received that begin with '!' are flagged for UI notification.
After a group of '!' messages are collected, they get displayed together in a popup window.

While a photo is loading, the 'heartbeat' is disabled. After the photo is downloaded, the heartbeat resumes.


## Features
 * MVVM design + Android Architecture Components
 * Kotlin + Java
 * Fragments
 * BlueDot Bluetooth
 * Green Robot
 * Life Cycle
 * Data Binding
 * View Model


# Credits
 * Martin O'Hanlon for the BlueDot - https://github.com/martinohanlon/BlueDot
 * daimajia for the NumberProgressBar - https://github.com/daimajia/NumberProgressBar

