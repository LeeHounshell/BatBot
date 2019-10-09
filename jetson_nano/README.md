# This is BatBot on the Jetson Nano

The system is designed in a layered manner, so that it is easy to extend or change.
The main python script receives queries and heartbeat messages from Android.
Each time a message is received, the script reads any waiting data from the Arduino's
serial line. That data is parsed, and passed back to Android in the form of log info.
The main python script is also responsible for determining the 'meaning' of each voice
command's text to figure out what action to take. BatBot scans for keywords to identify
intent of a command. Command requests that require an image to be identified are passed
from the main python script into a set of external programs that determine if the 'identify'
server is running. BatBot uses an 'identify' server to pass requests for image analysis.
This eliminates startup overhead inherent in making use of AI model technology. It is
possible to reconfigure and stop/restart the 'identify' server from voice commands sent
to the main python script. The 'identify' server passes back to the client text that
says what the server thinks the image is. The client then 'prints' that information to stdout,
so that the main batbot.py script can obtain the results by reading process output.
When an image is sent back to Android using Bluetooth, some signaling has to first
temporarily 'disable' the heartbeat messages. Once the image is transferred, heartbeat
functionality resumes.

# Jetson Nano Software Setup

Bundled with this project is a list of python modules and another list of ubuntu moduels.
Those can be easily used to setup the systems software dependencies. See below for
instruction on how to use the 'requirements.txt' and 'packages.txt' to load your system. 

Additionally you will need to allocate 'swap' space on your Jetson Nano.
I am using a /swapfile that is 7 Gig. Create it from the command line with 'dd':

    # each time you run the 'dd' command below, one Gig is added to /swapfile
    sudo dd if=/dev/zero of=/swapfile bs=1M count=1024 oflag=append conv=notrunc
    sudo dd if=/dev/zero of=/swapfile bs=1M count=1024 oflag=append conv=notrunc
    sudo dd if=/dev/zero of=/swapfile bs=1M count=1024 oflag=append conv=notrunc
    sudo dd if=/dev/zero of=/swapfile bs=1M count=1024 oflag=append conv=notrunc
    sudo dd if=/dev/zero of=/swapfile bs=1M count=1024 oflag=append conv=notrunc
    sudo dd if=/dev/zero of=/swapfile bs=1M count=1024 oflag=append conv=notrunc
    sudo dd if=/dev/zero of=/swapfile bs=1M count=1024 oflag=append conv=notrunc


The swapfile must be mounted from /etc/fstab. This is my /etc/fstab:

    /dev/root   /          ext4     defaults                  0     1
    /swapfile   none       swap     sw                        0     0
    none        /dev/sh    tmpfs    rw,nosuid,nodev,noexec    0     0

You need to setup the /etc/rc.local service to run /etc/rc.local during boot.
Then use the example rc.local script provided to customize your own system.
There are 3 'startup' files used for initializing everything at boot.
They are invoked from the 'rc.local' script.

 - start-bluetooth.sh
 - start-synergy.sh
 - start-batbot.sh

I recommend building and configuring 'synergy' v1 to share your laptop keyboard/mouse with BatBot. I use 2 Wifi connections in BatBot. The 2nd Wifi is configured for synergy, so that none of my Internet requests interfere with my keyboard and mouse. See below for additional details.

Bluetooth needs to be run in 'compatibility' mode. You need to add the '-C' option to 'bluetoothd' when that gets run by ubuntu. To add compatibility mode for Bluetooth, change the startup options in nv-bluetooth-service.conf. Here is what mine looks like:

    $ sudo bash
    # cd /lib/systemd/system/bluetooth.service.d
    # grep ExecStart nv-bluetooth-service.conf
    ExecStart=/usr/lib/bluetooth/bluetoothd -C -d --noplugin=audio,a2dp,avrcp


# BatBot Scripts and Programs

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

