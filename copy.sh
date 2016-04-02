#!/bin/bash

USRNAME="$(whoami)"

HDP_PREFIX=""

if [ "$USRNAME" == "jmurra15" -o "$USRNAME" == "jmurray" ] ; then
    HDP_PREFIX="${HOME}/hadoop-1.2.1/"
elif [ "$USRNAME" == "jwill221" ] ; then
    HDP_PREFIX="./"
else
    exit;
fi

HDP_CALL="$HDP_PREFIX""bin/hadoop"

CLEAN_FILES="$(ls data/clean)"


for f in $CLEAN_FILES
do
  if [[ $($HDP_CALL fs -test -e /user/$USRNAME/gutenberg/$f) -eq 0 ]] ; then 
      $HDP_CALL fs -rm /user/$USRNAME/gutenberg/$f
  fi

  $HDP_CALL fs -copyFromLocal ./data/clean/$f /user/$USRNAME/gutenberg
done

