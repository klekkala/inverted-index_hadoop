#!/bin/bash

# Cleans and stores all data

mkdir -p data/clean

DIRTY_FILES="$(ls data/dirty)"

for f in $DIRTY_FILES
do
  python src/preprocess/preprocess.py $f < data/dirty/$f > data/clean/$f
done
