#!/usr/bin/python

# Produces a dictionary object as output
# {word: {documentId: {lineNumber: indices}}}
# Notice: indices is an array of indexes within a line that a word appears
# 
# Example output
#   {'bob': {'Macbeth.txt': {'0': ['1', '3']}, 'Romeo-and-Juliet.txt': {'33': ['2'], '1000': ['5']}}}
# 
# http://www.michael-noll.com/tutorials/writing-an-hadoop-mapreduce-program-in-python/

from operator import itemgetter
import sys

# Checks that keys exist before inserting into dictionaries
def update(current_values, documentId, lineNumber, index):
  if ( documentId in current_values ):
    if ( lineNumber in current_values[ documentId ] ):
      current_values[ documentId ][ lineNumber ].append( index )
    else:
      current_values[ documentId ][ lineNumber ] = [ index ]
  else:
    current_values[ documentId ] = { lineNumber : [ index ] }

  return current_values


current_word = None
current_values = { }

word = None

# input comes from STDIN
for line in sys.stdin:
  # remove leading and trailing whitespace
  line = line.strip()

  # parse the input we got from mapper.py
  word, documentId, lineNumber, index = line.split(',', 3)

  try:
    lineNumber = int( lineNumber )
    index = int( index )
  except ValueError:
    continue

  if current_word == word:
    current_values = update(current_values, documentId, lineNumber, index)
  else:
    if current_word:
        # write result to STDOUT
        print '{0}'.format( { current_word : current_values } )

    current_word = word
    current_values = { }
    current_values = update(current_values, documentId, lineNumber, index)

# do not forget to output the last word if needed!
if current_word == word:
  print '{0}'.format( { current_word : current_values } )

