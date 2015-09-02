#######################################################################
#Author: Kiran Kumar Lekkala
#Date: 29 August 2015
#Description: Setup file for installing required packages
#######################################################################

echo "updating the apt-cache"
sudo apt-get update
echo "upgrading apt"
sudo apt-get upgrade -y

sudo apt-get install eclipse
