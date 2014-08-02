#!/bin/bash
#This sciprt is run Phonetop client
echo 'PhoneTop Client Running Wait...'

#Set variable
DIR=$(dirname $0)
PROCESS_NAME=phonetop
SCRIPT_PATH=$DIR/$PROCESS_NAME

fork(){

# Step 1. If old Phonetop client is running, kill old process
pkill -9 $PROCESS_NAME

# Step 2. reverse tethering script execute
#Following script is run Phonetop reverse tethering
echo 'Reverse tethering running...'
echo 1 > /proc/sys/net/ipv4/ip_forward
iptables -P FORWARD DROP
iptables -A FORWARD -o eth0 -j ACCEPT
iptables -A FORWARD -o usb0 -j ACCEPT
iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
ifconfig usb0 192.168.42.55 up

# Step 3. run Phonetop client
#Following script is run Phonetop client
#If you run via udev, you should execute to absolute path
$SCRIPT_PATH

echo 'PhoneTop Client Run !'

}

fork &