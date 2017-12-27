//import Bits._; // import not necessary when Bitmaps is implemented
import Bitmaps._;
import Bits._;
import fileHandler.fileHandler;
import Parser._;
//import scala.collection.mutable.Stack;
import scala.collection.immutable._;
import scala.collection.mutable._;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException
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
	def Query2() {
		val DEBUG_STATUS = true;

		var testFile: ArrayBuffer[String] = new ArrayBuffer();

		// Add files into testFile
		testFile += "./Test Files/tweet.txt";

		var query: ArrayBuffer[String] = new ArrayBuffer();
		// Add queries into array buffer
		query += "entities.user_mentions";

		MISONParse(query, testFile, DEBUG_STATUS);
	}
	def Query3() {
		val DEBUG_STATUS = true;

		var testFile: ArrayBuffer[String] = new ArrayBuffer();

		// Add files into testFile
		testFile += "./Test Files/tweet.txt";

		var query: ArrayBuffer[String] = new ArrayBuffer();
		// Add queries into array buffer
		query += "user.created_at";
		query += "text";
		query += "user.name";
		query += "user.description";

		MISONParse(query, testFile, DEBUG_STATUS);
	}
	def Query4() {
	  val DEBUG_STATUS = true;

		var testFile: ArrayBuffer[String] = new ArrayBuffer();

		// Add files into testFile
		testFile += "./Test Files/tweet.txt";

		var query: ArrayBuffer[String] = new ArrayBuffer();
		// Add queries into array buffer
		query += "quoted_status.entities.urls";
		query += "quoted_status.text";
		query += "created_at";
		query += "user.description";
		query += "quoted_status.user.description";
		
		MISONParse(query, testFile, DEBUG_STATUS);
	}
	def Query5() {
	  val DEBUG_STATUS = false;

		var testFile: ArrayBuffer[String] = new ArrayBuffer();

		// Add files into testFile
		testFile += "./Test Files/tweet.txt";

		var query: ArrayBuffer[String] = new ArrayBuffer();
		// Add queries into array buffer
		query += "extended_entities.media";
		
		MISONParse(query, testFile, DEBUG_STATUS);
	}
	def Query6() {
	  val DEBUG_STATUS = false;

		var testFile: ArrayBuffer[String] = new ArrayBuffer();

		// Add files into testFile
		testFile += "./Test Files/tweet.txt";

		var query: ArrayBuffer[String] = new ArrayBuffer();
		// Add queries into array buffer
		query += "extended_entities.media";
		
		MISONParse(query, testFile, DEBUG_STATUS);
	}
	def Query7() {
	  val DEBUG_STATUS = true;

		var testFile: ArrayBuffer[String] = new ArrayBuffer();

		// Add files into testFile
		testFile += "./Test Files/tweet1.json";

		var query: ArrayBuffer[String] = new ArrayBuffer();
		// Add queries into array buffer
		query += "created_at";
		query += "text";
		query += "entities.user_mentions";
		
		MISONParse(query, testFile, DEBUG_STATUS);
	}
	def BitsTest() {
		// Testing union operation
	  var x:Bits = new Bits(Int.MaxValue);
	  println("x=   "+x);
	  x.flip();
	  println("x(f)="+x);
	  println("x+1= "+(x+1));
	  var y:Bits = new Bits(12345);
	  println("y=   "+y);
	  println("x+y= "+(x+y));
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
	def BitmapTest() {
	  var filePath = "./Test Files/jsonTest1.txt"
	  var fileHandler: fileHandler = new fileHandler();
	  fileHandler.setNewFilePath(filePath);
	  fileHandler.getNext;
	  val stringSplitted = fileHandler.getFileArray;
	  var bitmaps = new Bitmaps(
      2,
      stringSplitted);
	  println(bitmaps);
	  for (i <- 0 until stringSplitted.size) {
	    val start = 32*i;
	    val end = (32*(i+1)-1);
	    println("Generating all colons for " + i + "th row...");
	    val ab = bitmaps.generateAllColonPositions(start,end);
	    print("AB:  ");
	    ab.foreach(x => print(s"$x "));
	    println("\nGenerating commas for " + i + "th row...");
	    val ab1 = bitmaps.generateCommaPositions(start,end,0);
	    val ab2 = bitmaps.generateCommaPositions(start,end,1);
	    print("AB1: ");
	    ab1.foreach(x => print(s"$x "));
	    print("\nAb2: ");
	    ab2.foreach(x => print(s"$x "));
	    println();
	  }
	}
	def UnicodeTest() {
	  var strToFix = "{\"id\":\"\\u2026 <-Unicode Str\"}";
	  println(FixString(strToFix));
	}
	def FixString(string: String) : String = {
	  var newString = string;
	  // Find the 1st problematic string
	  var start = string.indexOf("\\u");
	  while(start != -1) {
	    // Extract the problematic string
	    val end = start + 6;
	    val wrongString = string.substring(start,end);
	    // Convert to unicode
	    val hexCode = wrongString.substring(2);
	    val intCode = Integer.parseInt(hexCode, 16);
	    val finalString = new String(Character.toChars(intCode));
	    // Replace
	    newString = string.replace(wrongString,finalString);
	    // Find next problematic string
	    start = string.indexOf("\\u", end);
	  }
	  return newString;
	}
	def main(args: Array[String]) {
		UnicodeTest();
		//BitmapTest();
		//BitsTest();
		//val x: Char = ' ';
		//println(x == ' ');
	  //println((Some(false)));
	  //Query1();
	}
}