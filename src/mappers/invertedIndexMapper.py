#!/usr/bin/python

# http://www.michael-noll.com/tutorials/writing-an-hadoop-mapreduce-program-in-python/

import sys

# input comes from STDIN (standard input)
for line in sys.stdin:

    # remove leading and trailing whitespace
    line = line.strip()

    # split the line into words
    words = line.split()

    documentId = words.pop(0)
    lineNumber = words.pop(0)

    for index in range( len(words) ):
        word = words[ index ]
        print '{0}, {1}, {2}, {3}'.format( word, documentId, lineNumber, index )
    