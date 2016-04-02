#!/usr/bin/python

def update(current_values, documentId, lineNumber, index):
  if ( documentId in current_values ):
    if ( lineNumber in current_values[ documentId ] ):
      current_values[ documentId ][ lineNumber ].append( index )
    else:
      current_values[ documentId ][ lineNumber ] = [ index ]
  else:
    current_values[ documentId ] = { lineNumber : [ index ] }

  return current_values

current_values = { }
current_values = update(current_values, 'Macbeth.txt', '0', '1')
current_values = update(current_values, 'Romeo-and-Juliet.txt', '1000', '5')
current_values = update(current_values, 'Romeo-and-Juliet.txt', '33', '2')
current_values = update(current_values, 'Macbeth.txt', '0', '3')

print '{0}'.format(current_values)