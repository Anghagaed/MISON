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
		testFile += "./Test Files/blank.txt";

		var query: ArrayBuffer[String] = new ArrayBuffer();
		// Add queries into array buffer
		query += "user.created_at";
		query += "text";
		query += "user.name";
		query += "user.description";

		MISONParse(query, testFile, DEBUG_STATUS);
	}
	def BitsTest() {
		var x:Bits = new Bits(Int.MaxValue);
	x += 1;
	println(x)
	var y:Bits = new Bits(1);
	//    println("start");
	for(i <- 0 until 32) {
		//      if(x == y){
		//        println(x)
		//      }
		println("extracting 1")
		println("before:"+x)

		var z = x & -x.bits
		//println(new Bits(-x.bits))
		println("after: "+z)

		println("remove 1")
		println("before:"+x)

		z = x & (x - 1)
		//println((x-1))
		println("after: "+z)


		x.set(i,1)

	}
	//    println("end");
	println("extracting 1")
	var z = x & -x.bits
	println(new Bits(-x.bits))
	println(z)

	println("remove 1")
	z = x & (x - 1)
	println((x-1))
	println(z)
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
	  var filePath = "./Test Files/blank.txt"
	  var fileHandler: fileHandler = new fileHandler();
	  fileHandler.setNewFilePath(filePath);
	  fileHandler.getNext;
	  val stringSplitted = fileHandler.getFileArray;
	  var bitmaps = new Bitmaps(
      3,
      0,
      stringSplitted);
	  //println(bitmaps);
	}
	def main(args: Array[String]) {
		//Query3();
		BitmapTest();
		//BitsTest();
		//val x: Char = ' ';
		//println(x == ' ');
	}
}