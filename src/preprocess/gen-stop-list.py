#!/usr/bin/python

from numpy import *
import sys

with open(sys.argv[1]) as f:
  lines = f.read().splitlines()

counts = zeros(len(lines), dtype=int)

words = empty(len(lines), dtype=object)

for i in range(len(lines)):
  line = lines[i].replace('\t\n', '').split(", ")
  words[i] = line[0]
  counts[i] = int(line[1])

avg = mean(counts)

stdev = std(counts)

for i in range(len(counts)):
  if counts[i] > avg + 3 * stdev:
    print words[i]
