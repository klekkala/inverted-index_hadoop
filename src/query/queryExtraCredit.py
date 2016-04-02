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

def update(current_values, documentId, lineNumber, index):
  if ( documentId in current_values ):
    if ( lineNumber in current_values[ documentId ] ):
      current_values[ documentId ][ lineNumber ].append( index )
    else:
      current_values[ documentId ][ lineNumber ] = [ index ]
  else:
    current_values[ documentId ] = { lineNumber : [ index ] }

  return current_values

# Returns a set that has every document id that is shared between the words in the query
def getSharedDocumentIds(query, words):
  # Get the intersection of documents 
  sharedDocumentIds = None
  for word in query:
    # https://docs.python.org/2/tutorial/datastructures.html#sets
    documentIds = set( words[ word ].keys() )

    if sharedDocumentIds == None:
      sharedDocumentIds = documentIds
    else:
      # Intersection
      sharedDocumentIds = sharedDocumentIds & documentIds

  return sharedDocumentIds

# Returns an object that has every line number that is shared between the words in the query
def getSharedLineNumbers(query, words):

  sharedLineNumbersSet = { }

  # Get the intersection of line numbers 
  sharedDocumentIds = getSharedDocumentIds(query, words)
  for documentId in sharedDocumentIds:

    sharedLineNumbersSet[ documentId ] = None
    
    for word in query:
    
      lineNumbers = set( words[ word ][ documentId ].keys() )

      if sharedLineNumbersSet[ documentId ] == None:
        sharedLineNumbersSet[ documentId ] = lineNumbers
      else:
        sharedLineNumbersSet[ documentId ] = sharedLineNumbersSet[ documentId ] & lineNumbers

  values = { }

  for documentId in sharedLineNumbersSet:
    for lineNumber in sharedLineNumbersSet[ documentId ]:

      isConsecutive = True
      firstWord = query[0]

      print '{0} {1} {2}'.format( firstWord, documentId, lineNumber )
      for index in words[ firstWord ][ documentId ][ lineNumber ]:
        nextIndex = index + 1
        isConsecutive = True
        
        for word in query[1:]:
          if nextIndex not in words[ word ][ documentId ][ lineNumber ]:
            isConsecutive = False
            break
          nextIndex = nextIndex + 1

        if isConsecutive:
          break

      if isConsecutive:
        update(values, documentId, lineNumber, 0)


  return values



print "Type \":q\" to quit."
print "Use \"&&\" for \"and\", \"||\" for \"or\", and \"!\" for \"not\"."
print "These operators require spaces between themselves and the query words."

result = ''
while sys.stdin:

  realQuery = raw_input('Find word: ')
  query = realQuery.lower()

  if query == ":q":
    break

  query = query.split()
  
  # Error check
  for word in query:
    if word not in words:
      result = str( realQuery ) + ' was not found'
      break

  sharedLineNumbers = getSharedLineNumbers(query, words)

  result = str( realQuery ) + '\n'
  for documentId in sharedLineNumbers:
    result += '\t' + str( documentId ) + '\n'

    lineNumbers = sharedLineNumbers[ documentId ].keys()
    lineNumbers = lineNumbers.sort()

    # http://stackoverflow.com/questions/3590165/joining-list-has-integer-values-with-python
    result += '\t\t' + ','.join( str(x) for x in lineNumbers ) + '\n'

  # query = ' '.join( query )
  # if query in words:

  #   result = str( realQuery ) + '\n'
  #   for documentId in words[ query ]:
  #     result += '\t' + str( documentId ) + '\n'

  #     # http://stackoverflow.com/questions/3590165/joining-list-has-integer-values-with-python
  #     result += '\t\t' + ','.join( str(x) for x in words[ query ][ documentId ] ) + '\n'

  # else:
  #   result = str( query ) + ' was not found'

  print str( result )

