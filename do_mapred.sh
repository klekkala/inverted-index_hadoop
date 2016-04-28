#!/bin/bash

USRNAME="$(whoami)"

HDP_PREFIX=""
INPUT_DIR_PATH=""
OUTPUT_DIR_PATH=""
HDFS_IN_DIR=""
HDFS_IN_DIR_DEF="/home/$USRNAME/input"
HDFS_OUT_DIR=""
HDFS_OUT_DIR_DEF="/home/$USRNAME/output"

# do_mapred.sh Usage
usage()
{
cat << EOF
Usage: $0 [-options]

Options:
  -c Directory to put cleaned files into on HDFS (default: /user/<netid>/input)
  -h Display this help summary
  -i Input directory (containing files to use)
  -o Output directory (where results should be placed)
  -p Hadoop prefix (path to hadoop directory)
  -r Directory to use for output in HDFS (default: /user/<netid>/output)

Ex: For ~/hadoop-1.2.1, input files in ./data/dirty, and output in ./results:
  ./do_mapred.sh -p ~/hadoop-1.2.1 -i ./data/dirty -o ./results
  
EOF
exit 1
}

while getopts ":hci:o:p:r" opt; do
  case "$opt" in
    c)
      HDFS_IN_DIR="$OPTARG"
      ;;
    h)
      usage
      ;;
    i)
      INPUT_DIR_PATH="$OPTARG"
      ;;
    o)
      OUTPUT_DIR_PATH="$OPTARG"
      ;;
    p)
      HDP_PREFIX="$OPTARG"
      ;;
    r)
      HDFS_OUT_DIR="$OPTARG"
      ;;
    *)
      usage
      ;;
  esac
done

# Error checking
if [[ -z $INPUT_DIR_PATH ]] || [[ -z $OUTPUT_DIR_PATH ]] || [[ -z $HDP_PREFIX ]] ; then
  usage
  exit 1
fi

if [[ ! -e $INPUT_DIR_PATH ]] || [[ ! -d $INPUT_DIR_PATH ]] ; then
  echo "Input path isn't a directory!"
  exit 1
fi

if [[ ! -e $HDP_PREFIX ]] || [[ ! -d $HDP_PREFIX ]] ; then
  echo "Invalid Hadoop prefix passed!"
  exit 1
fi


if [[ -z "$HDFS_IN_DIR" ]] ; then
  HDFS_IN_DIR="$HDFS_IN_DIR_DEF"
fi

if [[ -z "$HDFS_OUT_DIR" ]] ; then
  HDFS_OUT_DIR="$HDFS_OUT_DIR_DEF"
fi


HDP_DIR_PATH="$(dirname $HDP_PREFIX)/$(basename $HDP_PREFIX)"
HDP_BIN="$HDP_DIR_PATH/bin"
HDP_CALL="$HDP_BIN/hadoop"
HDP_STRM_JAR="$HDP_DIR_PATH/contrib/streaming/hadoop-*streaming*.jar"


if [[ ! -e "$HDP_CALL" ]] ; then
  echo "Can't find \"<hadoop-prefix>/bin/hadoop\" for the hadoop prefix passed!"
  exit 1
fi

# Start up hadoop (if it's not running)
IS_HDP_RUNNING="$(jps | grep -iP 'jobtracker|namenode')"
if [[ -z $IS_HDP_RUNNING ]] ; then 
  echo -e "\nHadoop is not currently running"
  echo -e "Calling start-all.sh...\n"
  $HDP_BIN/start-all.sh
else
  echo -e "\nHadoop is already running\n"
fi


# Which mapper and reducer to use
MAPPER=invertedIndexMapper.py
REDUCER=invertedIndexReducer.py

DIRTY_DIR_PATH="$INPUT_DIR_PATH"
CLEAN_DIR_PATH="$(dirname $INPUT_DIR_PATH)/clean"

# Make dirs
mkdir -p $CLEAN_DIR_PATH
mkdir -p $OUTPUT_DIR_PATH

# Preprocess input files
echo -e "\nPreprocessing input files\n"

DIRTY_FILES="$(ls $DIRTY_DIR_PATH)"

for f in $DIRTY_FILES ; do
  python src/preprocess/preprocess.py $f < $INPUT_DIR_PATH/$f > $CLEAN_DIR_PATH/$f
done


# Check that the input dir in HDFS exists
echo -e "\nSetting up HDFS for input files\n"
if [[ $($HDP_CALL fs -test -e $HDFS_IN_DIR) -ne 0 ]] ; then 
  if [[ $(HDP_CALL fs -test -d $HDFS_IN_DIR) -ne 0 ]] ; then
    $HDP_CALL fs -rmr $HDFS_IN_DIR
    $HDP_CALL fs -mkdir -p $HDFS_IN_DIR
  fi
fi

# Copy input files to HDFS
echo -e "\nCopying preprocessed input files into HDFS\n"

CLEAN_FILES="$(ls $CLEAN_DIR_PATH)"

#for f in $CLEAN_FILES ; do
#  if [[ $($HDP_CALL fs -test -e $HDFS_IN_DIR/$f) -eq 0 ]] ; then 
#    $HDP_CALL fs -rm $HDFS_IN_DIR/$f
#  fi
#  $HDP_CALL fs -copyFromLocal $CLEAN_DIR_PATH/$f $HDFS_IN_DIR/$f
#done


# Check that the output dir in HDFS exists
echo -e "\nSetting up HDFS for output files\n"

if [[ $($HDP_CALL fs -test -e $HDFS_OUT_DIR) -ne 0 ]] ; then 
  $HDP_CALL fs -mkdir -p $HDFS_OUT_DIR
fi


RUN_DIR="run_$(date +"%Y%m%d_%H%M")"

# Call hadoop
echo -e "\nCalling hadoop\n"

$HDP_CALL jar $HDP_STRM_JAR \
  -file    ./src/mappers/$MAPPER   \
  -mapper  ./src/mappers/$MAPPER   \
  -file    ./src/reducers/$REDUCER \
  -reducer ./src/reducers/$REDUCER \
  -input   $HDFS_IN_DIR/*  \
  -output  $HDFS_OUT_DIR/$RUN_DIR


# Copy results from HDFS to output dir
echo -e "\nCopying output files from HDFS to $OUTPUT_DIR_PATH\n"

$HDP_CALL fs -get $HDFS_OUT_DIR/$RUN_DIR $OUTPUT_DIR_PATH


# Output data file path
OUT_DATA=$OUTPUT_DIR_PATH/$RUN_DIR/part-00000

# Run query program
echo -e "\nStarting up query program\n"

python ./src/query/query.py $OUT_DATA
