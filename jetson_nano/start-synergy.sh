#!/bin/bash
cd /home/lee
echo "synergyc: initializing.."
while true
do
    /usr/local/bin/synergyc --display ":0.0" -d INFO --name batbot 192.168.1.109
    exit_status=$?
    echo "synergyc: exit_status=${exit_status}"
    if [ ${exit_status} -eq 0 ]; then
       echo "synergyc: started successfully!"
       break
    fi
    sleep 3

    /usr/local/bin/synergyc --display ":0.0" -d INFO --name batbot 192.168.1.69
    exit_status=$?
    echo "synergyc: exit_status=${exit_status}"
    if [ ${exit_status} -eq 0 ]; then
       echo "synergyc: started successfully!"
       break
    fi
    sleep 3
done

gsettings set org.gnome.desktop.interface cursor-size 50

iw dev wlan0 set power_save off

