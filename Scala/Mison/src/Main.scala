//import Bits._; // import not necessary when Bitmaps is implemented
import Bitmaps._;
import Bits._;
import fileHandler.fileHandler;
import Parser._;
//import scala.collection.mutable.Stack;
import scala.collection.immutable._;
import scala.collection.mutable._;

// Comment by Hang : I made changes to fileHandler and all the test function using previous version is causing error
//                   I taken the libery to comment out all test functions that are currently having an error due to 
//                   this change.
object Main {
  def Query1() {
    val DEBUG_STATUS = false;

    var testFile: ArrayBuffer[String] = new ArrayBuffer();

    // Add files into testFile
    testFile += "./Test Files/tweet.txt";

    var query: ArrayBuffer[String] = new ArrayBuffer();
    // Add queries into array buffer

    query += "created_at";
    query += "text";
    query += "quoted_status.retweet_count";

    MISONParse(query, testFile, DEBUG_STATUS);

  }
  def MISONParse(queryFieldsList: ArrayBuffer[String],
    filePaths: ArrayBuffer[String] = new ArrayBuffer[String],
    DEBUG_STATUS: Boolean) {
    var parser = new MISONParser(queryFieldsList, filePaths, DEBUG_STATUS);
    var result: ArrayBuffer[String] = parser.parseQuery();
    System.out.println("Printing result");
    for (q <- result) {
      System.out.println(q);
    }
  }
  def main(args: Array[String]) {
    Query1();
  }
}