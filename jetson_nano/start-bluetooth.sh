#!/bin/bash

cd /home/lee
sleep 30
mount /dev/shm

echo "wifi: set power_save off.."
iw dev wlan0 set power_save off

echo "bluetooth: initializing.."
hciconfig hci0 up

# needed for backward compatibility
sdptool add SP

bluetoothctl block 74:DA:38:F2:93:5D
bluetoothctl select DC:71:96:1C:90:F7
bluetoothctl power on
bluetoothctl power on

# # if cannot connect by default with above.  try using coproc..
# # run commands inside bluetoothctl -- and remain running bluetoothctl
# coproc bluetoothctl
# echo -e 'block 74:DA:38:F2:93:5D\nselect DC:71:96:1C:90:F7\nagent on\npower on\n' >&${COPROC[1]}
# output=$(cat <&${COPROC[0]})
# echo $output
