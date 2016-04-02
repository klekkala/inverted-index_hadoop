#!/bin/bash

USRNAME="$(whoami)"

HDP_PREFIX=""

mkdir -p results
#python src/mappers/wordCountMapper.py < data/clean/Macbeth.txt > results/MacbethMapped.txt

#cat data/clean/Macbeth.txt | src/mappers/wordCountMapper.py | sort -k1,1 | src/reducers/wordCountReducer.py > results/MacbethReduced.txt

if [ "$USRNAME" == "jmurra15" ] ; then
    HDP_PREFIX="${HOME}/hadoop-1.2.1/"
elif [ "$USRNAME" == "jwill221" ] ; then
    HDP_PREFIX="${HOME}/Software-Systems/map-reduce/hadoop-1.2.1/"
else
    exit;
fi


HDP_CALL="$HDP_PREFIX""bin/hadoop"

if [[ $($HDP_CALL fs -test -e /user/$USRNAME/gutenberg-output) -ne 0 ]] ; then 
    $HDP_CALL fs -mkdir -p /user/$USRNAME/gutenberg-output
fi

MAPPER=invertedIndexMapper.py
#MAPPER=wordCountMapper.py

REDUCER=invertedIndexReducer.py
#REDUCER=wordCountReducer.py


$HDP_CALL jar "$HDP_PREFIX"contrib/streaming/hadoop-*streaming*.jar \
  -file    ./src/mappers/$MAPPER   \
  -mapper  ./src/mappers/$MAPPER   \
  -file    ./src/reducers/$REDUCER \
  -reducer ./src/reducers/$REDUCER \
  -input   /user/$USRNAME/gutenberg/*         \
  -output  /user/$USRNAME/gutenberg-output/run_$(date +"%Y%m%d_%H%M")
