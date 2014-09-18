#!/bin/bash
#This sciprt is run Phonetop client

export XAUTHORITY=/home/odroid/.Xauthority
export DISPLAY=:0.0

echo 'PhoneTop Client Running Wait...'
#Set variable
DIR=$(dirname $0)
PROCESS_NAME=phonetopclient
PROCESS_NAME2=phonetop
SCRIPT_PATH=$DIR/$PROCESS_NAME
SCRIPT_PATH2=$DIR/$PROCESS_NAME2
fork(){
# Step 1. If old Phonetop client is running, kill old process
pkill -9 $PROCESS_NAME
pkill -9 $PROCESS_NAME2
sleep 2
# Step 2. Check usb0 iface is ready
usb0=""
while [ $usb0 -z ]
do
usb0="$(ifconfig | grep -A 1 'usb0' | tail -1 | cut -d ':' -f 2 | cut -d ' ' -f 1)"
echo "usb0 is null"
sleep 1
done
# Step 3. reverse tethering script execute
#Following script is run Phonetop reverse tethering
echo 'Reverse tethering running...'
echo 1 > /proc/sys/net/ipv4/ip_forward
iptables -P FORWARD DROP
iptables -A FORWARD -o eth0 -j ACCEPT
iptables -A FORWARD -o usb0 -j ACCEPT
iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
ifconfig usb0 192.168.42.55 up
# Step 4. run Phonetop client
#Following script is run Phonetop client
#If you run via udev, you should execute to absolute path
mkfifo /tmp/phonetop_pipe
$SCRIPT_PATH2 rtp://192.168.42.129:24030 &
$SCRIPT_PATH &
exit
}
fork &
