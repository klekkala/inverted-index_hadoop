#!/usr/bin/python

def lines( fileContent, documentId ):

  fileContent = fileContent.split( '\n' )

  for i in range( len( fileContent ) ):
    # Add doc id and line number
    fileContent[i] = '{0} {1} {2}'.format( documentId, i, fileContent[i] )

  return '\n'.join( fileContent )