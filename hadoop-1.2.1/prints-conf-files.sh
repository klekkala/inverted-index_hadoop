#!/bin/bash

# Clear the screen first to make room
clear 

# ~/.bashrc
echo '############################ ~/.bashrc ###############################################'
awk '/^[^#]/ {print}' ~/.bashrc
echo ''

# /etc/hosts
echo '############################ /etc/hosts ##############################################'
cat /etc/hosts
echo ''

# core-site
echo '############################ core-site.xml ############################'
cat $HADOOP_INSTALL/conf/core-site.xml | grep "<name\|<value\>"
echo ''

# hdfs-site
echo '############################ hdfs-site.xml ############################################'
cat $HADOOP_INSTALL/conf/hdfs-site.xml | grep "<name\|<value\>"
echo ''

# mapred-site
echo '############################ mapred-site.xml ##########################################'
cat $HADOOP_INSTALL/conf/mapred-site.xml | grep "<name\|<value\>"
echo ''

# yarn-site
echo '########################### yarn-site.xml ############################################'
cat $HADOOP_INSTALL/conf/mapred-site.xml | grep "<name\|<value\>"
echo ''

# ~/.ssh/config
echo '###########################  ~/.ssh/config ############################################'
cat ~/.ssh/config
echo ''

# masters y slaves
echo '############################ masters / slaves ################################################'
awk '{print "masters: " $0}' $HADOOP_INSTALL/conf/masters
awk '{print "slaves: " $0}' $HADOOP_INSTALL/conf/slaves
echo ''
