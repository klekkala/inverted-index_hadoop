#!/usr/bin/python

import logging
import sys
import ast

USAGE = 'usage: ./query dataFile'

logging.basicConfig( level=logging.INFO )
logger = logging.getLogger( __name__ )


if len( sys.argv ) != 2:
  logger.error( USAGE )
  sys.exit(0)

with open( sys.argv[1], 'r' ) as f:
  fileContent = f.read()

fileContent = fileContent.split( '\n' )


words = { }


for line in fileContent:
  if not line:
    continue
  # http://stackoverflow.com/questions/11026959/python-writing-dict-to-txt-file-and-reading-dict-from-txt-file
  word = ast.literal_eval( line )
  words.update( word )
print len(words)

print "Type \":q\" to quit."

result = ''
while sys.stdin:

  realQuery = raw_input('Query: ')
  query = realQuery.lower()

  if query == ":q":
    break

  if query not in words:
    result = str( realQuery ) + ' was not found'
    continue

  result = str( realQuery ) + '\n'
  for documentId in words[ query ]:
    result += '\t' + str( documentId ) + '\n'

    lineNumbers = words[ query ][ documentId ].keys()
    lineNumbers.sort()

    # http://stackoverflow.com/questions/3590165/joining-list-has-integer-values-with-python
    result += '\t\t' + ', '.join( str(x) for x in lineNumbers ) + '\n'

  print str( result )

