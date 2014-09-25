#!/bin/bash
#This sciprt is run Phonetop client

#export XAUTHORITY=/home/odroid/.Xauthority
#export DISPLAY=:0.0

echo 'PhoneTop Client Running Wait...'
#Set variable
DIR=$(dirname $0)
CLIENT_NAME=phonetopclient
PLAYER_NAME=phonetop
CLIENT_PATH=$DIR/$CLIENT_NAME
PLAYER_PATH=$DIR/ffmpeg-2.3/$PLAYER_NAME

fork(){
# Step 1. If old Phonetop client is running, kill old process
pkill -9 $CLIENT_NAME
pkill -9 $PLAYER_NAME
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
$PLAYER_PATH rtp://192.168.42.129:24030 &
$CLIENT_NAME &
exit
}
fork &
