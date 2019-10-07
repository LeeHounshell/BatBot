# This is BatBot on the Jetson Nano

## batbot.sh

This scripts starts the batbot.py and restarts it on error.

## batbot.py

This is the main BatBot control and communications program.
The 'batbot.py' script must be running in the same directory as scripts below.
This program uses Bluetooth to communicate with Android and PySerial to communicate with the Arduino robot.

## bluetooth_monitor.py

Script to check if Bluetooth works.

## capture_3k.sh

Script used by BatBot.py to capture one 3K photo.

## capture_and_identify.sh

Script used by BatBot.py to capture and identify a photo. Program arguments determine resolution and AI 'model' algorithm.
Output from this script will appear as a Popup Dialog in the Android app.

## capture_hd.sh

Script used by BatBot.py to capture one HD photo.
Output from this script will appear as a Popup Dialog in the Android app.

## capture_sd.sh

Script used by BatBot.py to capture one SD photo.
Output from this script will appear as a Popup Dialog in the Android app.

## capture.sh

Script used by BatBot.py to capture one photo. Program arguments determine image resolution.
Output from this script will appear as a Popup Dialog in the Android app.

## identify.py

The 'identify' server program gets started by BatBot.py.
It has the job of identifying photo content on request.
Depending on arguments passed, different AI 'models' are initialized and used.
Currently 4 model types (from ImageAI) are supported here:

 - DenseNet (default)
 - Inception_V3
 - Resnet50
 - Squeezenet

## kill_identify.sh

Script to stop the 'identify' server.

## learn_about.sh

Append instruction to train BatBot model on new images to /tmp/BatBot_TRAIN.txt.
The text file can be processed offline as a batch job for training models about new objects.

## models

Directory holding AI 'models' used by the 'identify' server.
The 'models' folder must exist in the same directory as 'batbot.py'.

## packages.txt

Use this list of Ubuntu packages to install the same on your Jetson Nano.
From a 'bash' shell, run:

    $ sudo apt-get install dselect
    $ sudo dpkg --set-selections < packages.txt
    $ sudo apt-get dselect-upgrade

## rc.local

This is a copy of my '/etc/rc.local' file. You will need to edit it for your machine.
This starts 'Synergy' and 'Bluetooth' and the 'BatBot' during boot.

## request_identify.py

Script to start and communicate with the 'identify' server.
This script will start the 'identify' server, if it is not running.
If 'identify' is running, this script will pass a request to identify an image.
Output from this script will appear as a Popup Dialog in the Android app.

## requirements.txt

You can use this file to install all python3 dependencies.
From a 'bash' shell virtual env, run:

    $ pip3 install -r requirements.txt

## serial_monitor.py

Script to check serial communications with the Arduino in the Elegoo Robot Car v3.0

## start-batbot.sh

Script called by '/etc/rc.local' to start BatBot at system boot.
You will need to edit this file to match your system.

## start-bluetooth.sh

Script called by '/etc/rc.local' to start Bluetooth at system boot.
You will need to edit this file to match your system.

## start-synergy.sh

Script called by '/etc/rc.local' to start Synergy at system boot.
You will need to edit this file to match your system.

You can get Synergy Core from https://github.com/symless/synergy-core

Follow the instructions there to build from source.  Be sure to 'git checkout v1-dev' of the Synergy Core.  The default 'v2-dev' on master does not work. But v1 works fine.  I use Synergy with Windows 10 + Bootcamp to control the BatBot by moving my laptop's mouse. I think Synergy is a better alternative than Jyupter Notebook. Both are installed on this Jetson Nano.  

## ubuntu_installed_packages.txt

Detailed list of packages and versions installed on this system.

