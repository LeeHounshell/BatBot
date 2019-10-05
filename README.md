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

When the Android app first connects, a 'help' popup displays with command hints.
Under that notice is the main user interface, consisting of a bat-image robot controller, and 5 buttons.  The buttons work as follows:

 - [*] set the operating mode to 'collision avoidance'.
 - [ok] stop the robot and change the operating mode to 'normal'.
 - [#] set the operating mode to 'line following'.
 - [Speak a Command or Help] use Google Voice Recognition to enter a robot command.
 - [voice command text] the white voice recognition text is also a 'repeat command' button.

The large oval bat image functions as a virtual joystick.  If you press it, a blue circle appears under your finger, and a larger circular boundry area also displays. The finger tracking image moves as you move your finger, within boundry limits. The log area shows the last 7 lines or so of log data from the Jetson Nano. Log data includes voice commands, status, state, joystick movement, clicks and more. Displayed data currently scrolls offscreen as new log data arrives.

![screen](../master/screens/BatBot_connected.jpg)

## An example voice command and response:

Here the command 'What is your IP address?' was spoken. The recognized spoken text appears under the joystick and above the logs section, in white bold text. That text also acts as a 'repeat' button for the last voice command. Results also display in Popup windows.

![screen](../master/screens/BatBot_voice_command.jpg)
![screen](../master/screens/BatBot_IP_address.jpg)

## An example of AI image recognition and training

These screens show what might happen when you ask 'what are you looking at?'
Initially the 'identify' server is not running, and must be started.
Note the 'identify' server does not start until the first image identification request.
Then the 'identify' server will startup with a specified AI 'algorithm' (model).
The model can be changed using just voice commands.

![screen](../master/screens/BatBot_identify.jpg)
![screen](../master/screens/BatBot_start_server.jpg)

Now that the server is running, we can ask 'what are you looking at?'
A photo is taken and analyzed. The app shows analysis text right away.
If View is selected, the image will download via Bluetooth, and
then display in a popup alongside the analysis result.

![screen](../master/screens/BatBot_identify_results.jpg)
![screen](../master/screens/BatBot_transfer_image.jpg)
![screen](../master/screens/BatBot_identify_results_image.jpg)

You can train BatBot if the analysis is incorrect. Saved images go to the Gallery.

![screen](../master/screens/BatBot_identify_results_train.jpg)
![screen](../master/screens/BatBot_identify_results_learn.jpg)

## query BatBot for sensor values

What happens when you ask BatBot to 'show sensor values.':

![screen](../master/screens/BatBot_show_sensors.jpg)
![screen](../master/screens/BatBot_sensor_values.jpg)

## query BatBot camera resolution

What happens when you ask BatBot to 'show camera resolution.':

![screen](../master/screens/BatBot_show_resolution.jpg)
![screen](../master/screens/BatBot_resolution.jpg)

## query BatBot AI model algorithm

![screen](../master/screens/BatBot_ask_AI_algorithm.jpg)
![screen](../master/screens/BatBot_AI_algorithm.jpg)


