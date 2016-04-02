#!/usr/bin/python

licenseEnds = [
  '*** START OF THIS PROJECT GUTENBERG EBOOK',
  '*END*THE SMALL PRINT! FOR PUBLIC DOMAIN',
  '\*\*\* START OF THIS PROJECT GUTENBERG EBOOK',
  '\*END\*THE SMALL PRINT! FOR PUBLIC DOMAIN'
]

# Remove the Gutenberg license
def removeLicense(fileContent):

  index = -1

  for licenseEnd in licenseEnds:

    # Find where the license ends
    index = fileContent.find( licenseEnd )

    if ( index > 0 ):
      # Find the next newline
      index = fileContent.find( '\n', index )

      # Remove all of the content in the license
      fileContent = fileContent[index+1:]

  return fileContent
