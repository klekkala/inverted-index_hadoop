#######################################################################
#Author: Kiran Kumar Lekkala
#Date: 29 August 2015
#Description: Setup file for installing required packages
#######################################################################

echo "entering into sudo"
sudo -s
echo "updating the apt-cache"
apt-get update
apt-get install avrdude binutils-avr gcc-avr avr-libc gdb-avr

apt-get install eclipse
