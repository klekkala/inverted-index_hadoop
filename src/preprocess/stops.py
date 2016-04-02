#!/usr/bin/python

import re
import string

def removeStops(fileContent):

  with open('stop-words.txt') as f:
    stopWords = f.read().splitlines()

  stop = '|'.join(stopWords)
  regex = re.compile(r'\b('+stop+r')\b', flags=re.IGNORECASE)
  fileContent = regex.sub(" ", fileContent)

  # http://stackoverflow.com/questions/265960/best-way-to-strip-punctuation-from-a-string-in-python
  # https://docs.python.org/2/library/string.html#string.translate
  # https://docs.python.org/2/library/string.html#string.punctuation
  fileContent = fileContent.translate(string.maketrans("",""), string.punctuation)

  # Remove UTF-8 BOM in file
  # http://stackoverflow.com/a/2459793
  fileContent = fileContent.decode('utf-8-sig')

  return fileContent
