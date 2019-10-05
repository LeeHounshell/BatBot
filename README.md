an experimental AI vision robot
===============================

# BatBot

 This is a powerful AI research robot created from commodity parts. Lower half is an Elegoo Robot Car v3.0. Upper half is a Jetson Nano. An Android app controls it with Spoken English over Bluetooth. Robot has a camera, ultrasonic sensors and a free 40 pin GPIO available. AI vision pattern recognition software in the Jetson Nano controls the robot's behavior. High-level spoken commands like 'WHAT ARE YOU LOOKING AT?' instruct the robot photograph and identify objects. The command 'GO FIND SOME-OBJECT' instructs the robot to locate, identify and photograph an object. Low-level spoken commands like 'WHAT IS YOUR IP-ADDRESS?' or 'GO FORWARD' or 'LOOK TO THE LEFT' will obtain information and/or control the robot directly. You can teach BatBot to identify new objects by using voice commands from the Android app.

 The companion Android app is a MVVM pattern written in Kotlin and Java. It uses the Bluetooth/BlueDot work by Martin O'Hanlon for virtual joystick controls and for communication to/from the Jetson Nano. Find BlueDot here: https://github.com/martinohanlon/BlueDot  

 The Jetson Nano is programmed with Python and uses the BlueDot Bluetooth library, the PySerial library, the ImageAI library and numerous dependencies. The Jetson Nano runs AI visual pattern recognition and behavior control software. It uses 'ImageAI' to process image content. Included are four pre-trained models from the 'ImageAI' project. See https://github.com/OlafenwaMoses/ImageAI and https://stackabuse.com/object-detection-with-imageai-in-python/  This design lets you easily change the learning approach being used. There is no need for a LCD display to report an IP address; the Bluetooth companion app using speech recognition magic will show the Nano's IP on request.

 The Elegoo Arduino Robot Car is programmed in 'C' and uses Serial over USB communication.  It acts like a 'slave' for the Jetson Nano, carrying out mostly low-level tasks and reporting back. The Arduino and Nano communicate via the Serial cable connecting them. Note that when any object gets closer than 10cm to the distance sensor, the robot automatically stops.


## The BatBot

This robot was built using the Elegoo Robot Car v3.0 as the main base; then piling on top of that the Jetson Nano (inside a case), a Samsung 500 Gig SSD, a phone-battery power supply and the camera, attached to the ultrasonic sensor. The figure-head is from a PEZ dispenser. No soldering is required to build this project. No 3D printing is required either. All parts can be ordered online or found in your local hardware store.

The README.md in the 'robot' folder of this project lists the parts needed to make this.
See https://www.elegoo.com/product/arduinocarv3-0/ for the Elegoo Car details.

![screen](../master/screens/batbot.png)


## connecting to the robot

The Android app starts with a selection screen for the BatBot Bluetooth connection.  When the app first connects, a 'help' popup displays with command hints. Underneath that notice is the main user interface, consisting of a bat-image virtual joystick controller, and 5 buttons. The buttons work as follows:

 - [*] set the operating mode to 'collision avoidance'.
 - [ok] stop the robot and change the operating mode to 'normal'.
 - [#] set the operating mode to 'line following'.
 - [Speak a Command or Help] use Google Voice Recognition to enter a robot command.
 - [voice command text] the white voice recognition text is also a 'repeat command' button.

The large oval bat image functions as a virtual joystick.  If you press it, a small blue circle appears under your finger, and a larger circular boundry area also displays. The finger tracking image moves as you move your finger, within boundry limits. The log area shows the last 7 lines or so of log data from the Jetson Nano. Log data includes voice commands, status, state, joystick movement, clicks and more. Displayed data currently scrolls offscreen as new log data arrives.

![screen](../master/screens/BatBot_connect.jpg)
![screen](../master/screens/BatBot_connected.jpg)


## example voice command and response:

Here the command 'What is your IP address?' was spoken. The recognized spoken text appears under the joystick and above the logs section, in white bold text. That text also acts as a 'repeat' button for the last voice command. Results also display in Popup windows.

![screen](../master/screens/BatBot_voice_command.jpg)
![screen](../master/screens/BatBot_IP_address.jpg)


## example of AI image recognition and training

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


