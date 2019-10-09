
# LIMITATION OF LIABILITY

YOU ACCEPT FULL RESPONSIBILITY FOR ANY DAMAGE, INJURY, DEATH, DELAY or FAILURE RESULTING FROM USE OF THIS PROJECT OR ANY OF ITS ASSOCIATED MATERIALS, PROGRAMS OR INSTRUCTIONS. YOU ACKNOWLEDGE AND UNDERSTAND THAT NONE OF THIS IS GUARANTEED TO BE SAFE OR CORRECT OR COMPLETE. YOU AGREE NOT TO HOLD THE AUTHORS OF BATBOT LIABLE FOR ANYTHING.


# BatBot Assembly Instructions

First assemble the Elegoo robot car. Build it per included instructions, except when mounting the battery case, move that to the back set of 2 bolt holes only. This will make a little extra space in between the case and the Arduino to use later for the Jetson Nano Assembly. These videos will help with car assembly:
- https://www.youtube.com/watch?v=xTILIkExKZo&t=2872s
- https://www.youtube.com/watch?v=DD8sYXKe268&t=932s
- https://www.youtube.com/watch?v=6QQta3Cc568&t=780s

Next assemble the Jetson Nano WiFi card and Antenna. Then put that in the acrylic transparent case with the Fan installed. Remeber to install a jumper for the Barrel Connector Power Supply at this time. This video will help with case assembly: https://www.youtube.com/watch?v=v0yUUtqKDhU

Attach and configure the Jetson Nano's SSD. Make the Jetson Nano boot from the SSD, instead of from the SD card. Use the jetsonhacks instructions here: https://www.jetsonhacks.com/2019/09/17/jetson-nano-run-from-usb-drive/

Now use the foam packaging from the "SD Card Reader" (see below) and 1 zip tie to hold the Samsung SSD firmly between the Jetson Nano and the foam protector. Use pliers to bend the zip ties at exactly the right place, for best fit. Be sure everything is mounted on the case side opposite the fan. Be aware that the camera connection area will be facing up. And the 40 PIN GPIO will be on the back bottom section.

Next position the 2-inch L Shape Stainless Steel Angle Bracket above the SSD and foam protector and against the Jetson Nano case's "front." (Note the "back" holds the fan assembly.) Double-check exact placement.
The top of the bracket should be nearly aligned with the top of the case. The bottom of the bracket will be resting on the foam and SSD.

Then.. CAREFUL THIS NEXT STEP.. put one of the Gorilla Double-Sided Tapes on the side of the L Bracket that you will affix to the Jetson Nano's case.
You got one shot at this, that tape is strong, so if you position it wrong, that will be trouble.. So do it right.. Tape the L Bracket to the Jetson Nano case.

Now use another zip tie to affix the top of the L Bracket to the Jetson Nano case.
Again use pliers to bend everything just right.
Be careful that the 5v barrel power plugin is not covered by the zip tie.
You need the 5v barrel connector to set everything up, later.
If done right, the zip tie will snugly hold the L Bracket's top against the top-front of the case.

You should be able to fit the Jetson Nano Assembly, including the SSD and foam protector, exactly between the battery case and the Arduino.
Measure it and double-check that everything fits snug but not too tight.
For me it was an exact fit.
It really depends on where Elegoo drills the holes to mount the battery.. I don't know if every kit is exactly the same.
So find your exact placement for the Jetson Nano Assembly on top of the Elegoo car.

Now do the placement again but this time position mirror hangers on the robot. Adjust them so the hangers support the Nano's edges, with the nano resting on and in-between the hangers.
Be sure to carefully position and exactly measure mirror hanger placement so that they hold the Nano firmly in place.
This prevents left-right movement. The Nano should not "rock" either.
Make sure you can lift the Nano out, and set it firmly back in place as needed.
Double check everything. Ensure none of the mirror hangers touch wheels.
Then use masking tape to hold all the mirror hangers in place so you can glue them.
Remove the Jetson Nano and set it aside. Be sure the mirror hangers do not move.

Next you will tape down the mirror hangers with Gorilla Tape.
Again.. CAREFUL THIS NEXT STEP.. That Gorilla Tape is strong.
One-by-one put Gorilla Tape on the back of each mirror hanger and then carefully put it back.
You have to be exact with placement, or the Jetson Nano won't be held properly.

In my robot, I was unable to use 4 mirror hangers, because one of them would have rubbed against a wheel.
If you have that problem, then just use 3 hangers and cut a zip-tie into 3 measured pieces that you can Gorilla Tape down as a makeshift "floor" for the missing mirror hanger space.
That is so the Jetson Nano Assembly does not "rock."

Put back the Jetson Nano Assembly. It should be firmly held in place, with no forward or side to side movement possible.

Now to build the Battery Support Frame. Use the nylon protective case that came with the battery as the "battery holder."
The trick is to attach it at exactly the right point on the L Bracket, using 2 small bolts with nuts and washers..
The nylon is tough. It is hard to make a hole. What I did was mark the sections of the nylon case where I wanted to make 2 holes.
Those spots were directly above the L Bracket's holes on right and left sides.
Then use a small thin Phillips screwdriver to bulge out where each hole should be.
Briefly hold a lighter to that point to melt the Nylon. The screwdriver will punch through.
BE VERY CAREFUL HERE.. THE NYLON MELTS QUICKLY. Only a small touch of the flame is needed.

Use bolts, washers and nuts to attach the battery holder to the L Bracket.
I only used 2 of the L Bracket's 4 holes to attach the battery case in this manner.

Now build the support frame for the battery.
First make a flat support structure from the flat mounting brackets, as shown in in the pictures.
You will be able to attach the extension to the 2 remaining holes in the L Bracket.
The long support bolt will extend at 90 degrees through one of the mounting brackets.
Use 2 nuts to hold it in place at the proper height.
This long support bolt will keep the battery and mount from drooping.

Next you need to find a piece of foam about a quarter inch thick that you can put into the battery holder to protect the battery from rubbing against the bolts. I repurposed some packing foam.
Use the battery to trace an outline of the shape you want, then cut it with scissors.
The foam cut-out should fit neatly into the battery holder with enough room to slide the battery in on top.

Now mount the 40 PIN GPIO ribbon cable to the back of the Jetson Nano.

And fit the Jetson Nano Assembly onto the robot. It will be firmly held in place by the mirror hangers and the Battery Support Frame.
Attach the robot's serial cable to the Jetson Nano by wrapping it once around the Nano's case.

Attach the plastic pipe clamps to the ultrasonic sensor, as shown in photos.
Also attach a plastic pipe clamp to the camera. A single screw attaches the camera's pipe clamp to one of the clamps on the ultrasonic sensor. A rubber band holds the other ultrasonic sensor clamp in place for additional support.

Attach the 300mm camera ribbon cable to the camera and to the Jetson Nano.

Attach a HDMI display and a USB keyboard and mouse. I use a wireless keyboard with built-in mousepad.
You only need the keyboard and mouse to setup the Jetson Nano.
Once synergy is installed, you are able to use your laptop's keyboard and mouse.

Do not connect the battery yet. You won't use the battery until after everything is setup.
Instead attach the 5v barrel power connector to the Jetson Nano.
Remember that the barrel connector requires a jumper on the Nano.

Everything should boot. Welcome to BatBot.

Use the 'Settings' program on the Jetson Nano to setup wifi and bluetooth.
Use Bluetooth to pair the Jetson Nano and your phone.
Build and install Synergy so you don't need a keyboard and mouse attached to the Nano.
Install the jetson_nano/start scripts to start Bluetooth and Synergy.
Hold off installing the jetson_nano/start-batbot.sh until after everything is working.

Install the 'arduino' program in the BatBot. You want the 64-bit ARM version.
Get it here: https://www.arduino.cc/en/main/software

From a terminal prompt, enter the command 'arduino.'
Then paste in the robot/batbot.sketch and compile and upload that to the robot.

Quit the 'arduino' program.

Reference the Jetson Nano README to setup and install the Jetson Nano's software dependencies and and boot configuration.
[Jetson Nano README](https://github.com/LeeHounshell/BatBot/blob/master/jetson_nano/README.md)

Now in the terminal window, cd to the BatBot directory.
Then 'cd jetson_nano' and run the BatBot with the command './batbot.sh'
You will see log messages from that.

Finally install the Android app on your phone. Run the app to control BatBot.
See the Android README for app install instructions.
[Android app README](https://github.com/LeeHounshell/BatBot/blob/master/batbot_app/README.md)

If you purchased a PEZ figure-head for the robot, carefully break it off and use Gorilla Tape to hold that in place. Be careful with placement. The servo assembly with mounted camera and ultrasonics rotates and you want to ensure that the servo can not hit the figure-head when it moves. The figure-head needs to be positioned against the Arduino in back, and flush with the side.

When everything is working, add the 'start-batbot.sh' script to your boot configuration.
Charge your batteries and remove the Jumper for the 5v power supply.
Insert and plug in all batteries. Flip the robot's 'on' switch. Boot BatBot and have fun!


# IR Controls

![screen](../../master/robot/screens/IR_controls.jpg)

The arrow buttons make the robot go forward, back, and turn right and left.

The ok buttons stops the robot and changes to 'default' mode.

The 1 button makes the camera look left.

The 2 button makes the camera look forward.

The 3 button makes the camera look right.

The 4 button makes the camera look full right.

The 5 button sets 'map' mode.

The 6 button makes the camera look full left.

The 7 button makes the robot slow down.

The 8 button displays sensor values and status.

The 9 button makes the robot speed up.

The * button sets 'collision avoidance + find object' mode.

The 0 button sets 'security' mode.

The # button sets 'line following' mode.



# All together the BatBot cost me a little over $500

This is the breakdown:

## Nvidia Jetson Nano Developer Kit

    $99 This is the Jetson Nano AI hardware kit 
    https://www.amazon.com/gp/product/B07PZHBDKT/ref=ppx_yo_dt_b_asin_title_o05_s00?ie=UTF8&psc=1

## Power Supply for the Jetson Nano

    $9 DC 5V/4A 20W Switching Power Supply Adapter 
    https://www.amazon.com/gp/product/B01N4HYWAM/ref=ppx_yo_dt_b_asin_title_o05_s00?ie=UTF8&psc=1

## Protective Case for the Jetson Nano

    $17 Acrylic Case for the Jetson Nano with Cooling Fan 
    https://www.amazon.com/gp/product/B07TH8NBWF/ref=ppx_yo_dt_b_asin_title_o06_s00?ie=UTF8&psc=1

## Strong Double Sided Mounting Tape

    $5 Gorilla Mounting Tape Clear Double Sided 
    https://www.amazon.com/gp/product/B0787PGKWR/ref=ppx_yo_dt_b_search_asin_title?ie=UTF8&psc=1

## Elegoo Smart Robot Car Kit V3.0

    $70 This kit is the lower half of the BatBot.
    https://www.amazon.com/gp/product/B07KPZ8RSZ/ref=ppx_yo_dt_b_asin_title_o02_s01?ie=UTF8&psc=1 

## External SSD for the Jetson Nano

    $90 Samsung T5 Portable SSD - 500GB 
    https://www.amazon.com/gp/product/B07Q4RBHWP/ref=ppx_yo_dt_b_asin_title_o05_s00?ie=UTF8&psc=1

## SD Card for the Jetson Nano

    $20 Samsung 128GB Evo Select MicroSDXC Memory Card 
    https://www.amazon.com/gp/product/B06XWZWYVP/ref=ppx_yo_dt_b_asin_title_o04_s00?ie=UTF8&psc=1

## SD Card Reader for your PC or Mac

    $8 Note: the packaging from this was also used in BatBot construction - it buffers the SSD and the Arduino 
    https://www.amazon.com/gp/product/B00OJ5WBUE/ref=ppx_yo_dt_b_asin_title_o05_s00?ie=UTF8&psc=1

## Wifi + Bluetooth Card for Jetson Nano

    $25 Intel Dual Ban Wireless with Bluetooth 
    https://www.amazon.com/gp/product/B01MZA1AB2/ref=ppx_yo_dt_b_asin_title_o08_s00?ie=UTF8&psc=1

## Antenna for Jetson Nano Wifi

    $14 Antenna Mod Kit for Jetson Nano 
    https://www.amazon.com/gp/product/B01E29566W/ref=ppx_yo_dt_b_asin_title_o07_s00?ie=UTF8&psc=1

## Secondary Wifi + Bluetooth

    $41 The Jetson Nano performs better with 2 Wifi installed. I use this with Synergy
    https://www.amazon.com/gp/product/B01INRAC2C/ref=ppx_yo_dt_b_asin_title_o03_s00?ie=UTF8&psc=1 

## Battery for the Jetson Nano

    $20 INIU 10000 mAh Portable Power Bank LED Display Ultra Compact 2 USB Ports Mobile Charger External Battery Backup Powerbank 
    https://www.amazon.com/gp/product/B07H6LB4J4/ref=ppx_yo_dt_b_asin_title_o02_s00?ie=UTF8&psc=1

## Battery Cable for the Jetson Nano

    $7 9 inch Micro USB Cable Combo Left and Right Angle Male / Male 
    https://www.amazon.com/gp/product/B01N337FQF/ref=ppx_yo_dt_b_asin_title_o02_s01?ie=UTF8&psc=1

## GPIO Ribbon Cable for Jetson Nano sensor expansion

    $7 GPIO 40pin Flat Ribbon Cable for Raspberry Pi 
    https://www.amazon.com/gp/product/B0761NYF6Y/ref=ppx_yo_dt_b_asin_title_o05_s00?ie=UTF8&psc=1

## Camera Ribbon Cable for Jetson Nano

    $5 Adafruit Flex Cable for Raspberry Pi Camera 300mm 
    https://www.amazon.com/gp/product/B00I6LJ19G/ref=ppx_yo_dt_b_asin_title_o06_s00?ie=UTF8&psc=1

## Camera for Jetson Nano

    $30 Waveshare IMX219-160 Camera for NVIDIA Jetson Nano 
    https://www.amazon.com/gp/product/B07SQ92SC7/ref=ppx_yo_dt_b_asin_title_o01_s00?ie=UTF8&psc=1

## Cable for Samsung SSD

    $9 Angled USB C Cable Extension 90 Degree Type C to USB 3.0 
    https://www.amazon.com/gp/product/B077M8G7XL/ref=ppx_yo_dt_b_asin_title_o03_s00?ie=UTF8&psc=1

## Wireless keyboard with mousepad

    $35 Keyboard and integrated mouse used for setup of the Jetson Nano uses only 1 USB port.
    https://www.amazon.com/gp/product/B06Y1PGF1X/ref=ppx_yo_dt_b_search_asin_title?ie=UTF8&psc=1

## Accessory Figure-Head

    $7 PEZ dispenser
    https://www.amazon.com/Pez-Candy-Justice-League-Batman/dp/B078SH5KJH/ref=sr_1_1?keywords=pez+dispenser+batman&qid=1570505999&sr=8-1

## Bracket for Battery Support Frame

    $14 Stainless Steel Angle Bracket L Shape 90 Degree 2 inches wide.
    https://www.amazon.com/gp/product/B07D7SDHKF/ref=ppx_yo_dt_b_asin_title_o04_s00?ie=UTF8&psc=1

## Zip Ties for the Jetson Nano Assembly

    $14 Heavy Duty Cable Zip Tie, 24 inches 
    https://www.amazon.com/gp/product/B000GATB8G/ref=ppx_yo_dt_b_asin_title_o04_s01?ie=UTF8&psc=1

## From your local Hardware Store

    3 plastic pipe clamps. 2 for the ultrasonics and 1 for the camera. sorry, i don't have the size. but the camera clamp is one size smaller.

    3 flat two-inch thin mounting brackets, with 4 half-inch bolts, and nuts and washers to fit.  used for constructing the Battery Support Frame 

    1 three-inch long support bolt with 3 nuts and washers to fit.

    3 one-inch square-ish shape mirror hangers for Jetson Nano base side supports. I used "Reflections Mirror Accessories #193672".


