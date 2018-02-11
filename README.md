# MISON Parser
* A Sequential MISON Parser for proof of concept. MISON is a parser that parses only certain columns to improve performance. Implementation is based on the research paper *Mison: A Fast JSON Parser for Data Analytics*.

## Features
* Extracts JSON data based on specified columns
* Contains a file (*SparkContext.scala*) that integrates MISON into Spark (version 2.1.1)
* Converts JSON data to Spark's Data Frame (enabling testing with Spark's method of extracting JSON data)

## Getting Started
* Go to Spark's [website](https://spark.apache.org/downloads.html) and download Spark source code with version 2.1.1 (May 02 2017) as this is the version we worked on.
* After extracting the *spark-2.1.1.tgz*, install Java, Python, and Scala so that Spark's terminal interface can be run.
* In the Spark folder (spark-2.1.1), type the following into the terminal. Note: There is more detailed information at Spark's [website](https://spark.apache.org/developer-tools.html).
```
$ build/sbt clean package
```

## Testing
* Note: The following files mentioned are in the [Spark](https://github.com/Anghagaed/MISON/tree/master/Spark) folder.
* Move *fileHandler.scala, Bitmaps.scala, Bits.scala, Parser.scala, SparkContext.scala* to *spark-2.1.1/core/src/main/scala/org/apache/spark*
  - *fileHandler.scala*: loads the text files (formatted in JSON) into readable String
  - *Bitmaps.scala*: converts String into bitmaps
  - *Bits.scala*: represents bits and supports bit operations
  - *Parser.scala*: parses through bitmaps and extracts necessary information to create a data frame
  - *SparkContext.scala*: calls loading, bitmap conversion, parse functions and creates a data frame
* Create a folder in *spark-2.1.1/bin* and put *MData.txt, MQuery.txt, Mtweet.txt*. As you can see in *testCorrectness.scala* and *MData.txt*, we named the folder *test*.
  - *MData.txt*: contains path for data files to support multiple file paths
  - *MQuery.txt*: list of columns to extract
  - *Mtweet.txt*: JSON data (fileHandler only supports text files)
  - *tweet_10.json*: JSON data for Spark as Spark supports JSON files
  - *testCorrectness.scala*: compares MISON's data frame and Spark's data frame
* In the terminal, type the following in the directory *spark-2.1.1*. Note: There is more detailed information at Spark's [website](https://spark.apache.org/developer-tools.html).
```
$ export SPARK_PREPEND_CLASSES=true
$ build/sbt compile
```
* In the terminal, type *./spark-shell* in directory *spark-2.1.1/bin* to start running Spark.
* When the Spark interface appears, type the following to run the test.
```
:load test/testCorrectness.scala
```

## Known Bugs/Potential Improvements
* Spark takes account of a case where the column doesn't exist (creates a record and treats the missing columns as null) while this parser doesn't (doesn't create a record). 
* Spark has its own JSON parsing system while this parser uses its own, which may have some issues (ex: it doesn't detect unicode characters, so a string manipulation function *fixString* was implemented in SparkContext). Using built-in Spark classes/functions may boost performance.
* The parser's performance can be dramatically improved with parallel computing.

## Authors
* [List of contributors](https://github.com/Anghagaed/MISON/graphs/contributors):
  - [Yun Chul Chang](https://github.com/ycchang27)
  - [Hang Liang](https://github.com/Anghagaed)
  - [Colin Wong](https://github.com/cwong77)
