#!/usr/bin/python

import logging
import sys
from license import *
from stops import *
from meta import *

USAGE = 'usage: ./preprocess documentId'

logging.basicConfig( level=logging.INFO )
logger = logging.getLogger( __name__ )


if len( sys.argv ) != 2:
  logger.error( USAGE )
  sys.exit(0)

documentId = sys.argv[1]  
fileContent = sys.stdin.read()


# Remove the Gutenberg license
# fileContent = removeLicense( fileContent )

# Lowercase every word
fileContent = fileContent.lower()

# Remove puncuation and stop words
fileContent = removeStops( fileContent )

# Add line numbers and document id
fileContent = lines( fileContent, documentId )

print fileContent