#!/bin/bash

echo ''

if [ $# -eq 0 ]; then
    echo 'Not enough arguments, specify at least an input file'
    echo 'Usage:'
    echo 'create-huge-files repeating-times [input-file]'
    echo ''
elif [ $# -eq 1 ]; then
    echo "Creating in example.ignore $1 times"
    for i in $(seq $1); do cat $HADOOP_INSTALL/examples-files/pg4300.txt >> $HADOOP_INSTALL/examples-files/example.ignore; done
    echo "File example.ignore created in "$HADOOP_INSTALL/examples-files
    echo ''
elif [ $# -eq 2 ]; then
    echo "Creating file with contents of $2 in example.ignore $1 times"
    for i in $(seq $1); do cat $2 >> $HADOOP_INSTALL/examples-files/example.ignore; done
    echo "File example.ignore created in "$HADOOP_INSTALL/example-files
fi
