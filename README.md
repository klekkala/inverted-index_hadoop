# Map-Reduce

The goal of this project is to implement an inverted index, a mapping from a word to its location within a file, using Hadoop MapReduce. A query program is provided that accepts a word and produces its location within the input files.

Several of Shakespeare's works from Project Gutenberg were used as test data and are include in the data/dirty directory.

## Usage

To preprocess the data, execute the inverted index MapReduce program, and run the query program on the inverted index MapReduce output, use the do_mapred.sh script.

Usage: ./do_mapred.sh [-options]

Options:  
   -c Directory to put cleaned files into on HDFS (default: /user/\<netid\>/input)  
   -h Display this help summary  
   -i Input directory (containing files to use)  
   -o Output directory (where results should be placed)  
   -p Hadoop prefix (path to hadoop directory)  
   -r Directory to use for output in HDFS (default: /user/\<netid\>/output)  

Ex: For ~/hadoop-1.2.1, input files in ./data/dirty, and output in ./results:  
  ./do_mapred.sh -p ~/hadoop-1.2.1 -i ./data/dirty -o ./results
  
---
  
To run the query program on a inverted index MapReduce output, use the run_query.sh script.  
 
Usage: ./run_query.sh -i \<data_file\>

Options:  
  -h Display this help summary  
  -i Input file  

Ex: For \<out_dir\>/\<run\>/part-00000:  
  ./run_query.sh -i \<out_dir\>/\<run\>/part-00000
  
## Directory Structure

* data
  * clean
    * Stores cleaned input files
    * Created and populated by process.sh
  * dirty
    * Our provided Shakespeare input files
* doc
   * Just contains the project description
   * This README is the main source of documentation
* do_mapred.sh
  * Preprocesses data
  * Runs inverted index MapReduce program
  * Runs the query program on inverted index MapReduce program output
* run_query.sh
  * Runs the query program on an invertedIndex MapReduce program output
* src
  * mappers
    * wordCountMapper.py
      * The word count mapper provided in the tutorial
    * invertedIndexMapper.py
      * A modified version of the word count mapper for our project
      * Reads input files from standard input and writes intermediate keys to standard output
  * preprocess
    * gen-stop-list.py
      * Removes stop words from a stop list file provided on the command line
    * license.py
      * A dirty program to remove the Gutenberg license.
    * meta.py
      * Provides a function to prepend a document id and the line number to every line in a file
    * preprocess.py
      * Our preprocessing driver
        * Expects a document id on the command line
        * Expects a file on standard input
        * Writes the cleaned data to standard output
        * Lowercases the data
        * Uses the functions provided in meta.py and stop.py
    * stops.py
      * Removes punctuation
  * query
    * query.py
      * Expects an output file from a inverted index MapReduce program
      * Provides a shell interface for users to query for a single word
      * Produces the line numbers in every file that a query word appears in
      * Use ":q" to quit
    * queryExtraCredit.py
      * Our attempt at the extra credit
  * reducers
    * wordCountReducer.py
      * The word count reducer provided in the tutorial
    * invertedIndexReducer.py
      * Reads intermediate keys from standard input and writes a dictionary out for every intermediate key
      * Each dictionary output contains the document id for every document the word is contained in along with the corresponding lines and line indices it appears at
* test_out
  * Our test output from running the inverted index MapReduce program on the files in data/dirty directory after they had been cleaned
  * Used as input to the query program

## Design Decisions

Hadoop can be used in any programming language by using Hadoop's Streaming library. The library only requires that the mapper and reducer executables must be able to read from standard input and write to standard output. Therefore, for ease of use and programmer time, we decided to write our mappers and reducers in Python and provide those as input to the Hadoop Streaming library.

Before we provided our data as input to our inverted index MapReduce program, we preprocessed our data by prepending the line number and a document id, lowercasing the words, removing the punctuation, and removing the commonly used words (stop words).

### Using Hadoop with Python

Notice: We found a very helpful tutorial for running Hadoop using Python.

[Using Hadoop with Python Tutorial](http://www.michael-noll.com/tutorials/writing-an-hadoop-mapreduce-program-in-python/ "Using Hadoop with Python Tutorial")

### Preprocessing

Before we ran our inverted index MapReduce program on the data, we preprocessed it:

1. We ran the word count MapReduce program provided in the above tutorial.
  1. We selected the words that were three standard deviations above the average from the output and wrote those to a stop list file.
2. For every file in the input data directory
  1. For every line
    1. Prepend the document id (the file name) and the line number (zero indexed)
  2. Lowercase ASCII in the file
  3. Remove stop words that were provided from a stop list file
  4. Remove punctuation

Notice: We also added code to remove the license from the top of the files. However, the license was inconsistent between files and its removal would have likely produced inconsistent results from other similar inverted index programs (ex. line numbers could be off if the license is removed before the line numbers are prepended).
