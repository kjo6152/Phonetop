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

# Step 2. Check usb0 iface is ready
#if you don't configure nework about usb0, you should below ifconfig script run
#ifconfig usb0 0.0.0.0 up

#else, you have to configure below content about usb0 iface.
#example ubuntu path '/etc/network/interfaces'

#auto usb0
#iface usb0 inet static
#address 192.168.42.55
#netmask 255.255.255.0
#network 192.168.42.0
#broadcast 192.168.42.255


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
$SCRIPT_PATH

echo 'PhoneTop Client Run !'

}

fork &
