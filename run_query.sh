#!/bin/bash


DATA_FILE_PATH=""


usage()
{
cat << EOF
Usage: $0 -i <data_file>

Options:
  -h Display this help summary
  -i Input file

Ex: For <out_dir>/<run>/part-00000:
  ./run_query.sh -i <out_dir>/<run>/part-00000

EOF
exit 1
}


while getopts ":hi:" opt; do
  case "$opt" in
    h)
      usage
      ;;
    i)
      DATA_FILE_PATH="$OPTARG"
      ;;
    *)
      usage
      ;;
  esac
done

# Error checking
if [[ -z $DATA_FILE_PATH ]] ; then
  usage
  exit 1
fi

if [[ ! -e $DATA_FILE_PATH ]] ; then
  echo "File doesn't exist!"
  exit 1
fi


python ./src/query/query.py $DATA_FILE_PATH
