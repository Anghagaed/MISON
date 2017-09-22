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
  def BitsTest() {
    var test: Bits = new Bits(1);
    var test2: Bits = new Bits(2);
    println("test = " + test + "\ntest2 = " + test2);
    println("test + test2 = " + (test + test2));
    println("test - test2 = " + (test - test2));
    println("test * test2 = " + (test * test2));
    println("test / test2 = " + (test / test2));
    println("test & test2 = " + (test & test2));
    println("test | test2 = " + (test | test2));
    println("test ^ test2 = " + (test ^ test2));
    println("test << 1 = " + (test << 1));
    println("test2 >> 1 = " + (test >> 1));
    println("test.get(0) = " + test.get(0) + "\ttest.get(1) = " + test.get(1));
    test.set(3, 1); println("test.set(3, 1) = " + test);
    print("test: " + test + "  test.flip: "); test.flip(); println(test);
  }
  def fileHandlerTest() {

    println("Begin Testing")
    val fHandler = new fileHandler();
    fHandler.setNewFilePath("./Test Files/jsonTest2.txt");
    while (fHandler.getNext) {
      for (a <- 0 until fHandler.getFileVector.size) {
        println(fHandler.getFileVector(a))
      }
    }
    println("End Testing")

  }
  def BitmapsTest() {

    val fHandler = new fileHandler();
    fHandler.setNewFilePath("./Test Files/jsonTest2.txt");
    while (fHandler.getNext) {
      // temp test values
      val layers = 2;
      val arrayLayers = 3;

      // begin testing
      val bms: Bitmaps = new Bitmaps(layers, arrayLayers, fHandler.getFileVector);
      bms.createBitmap;
      println(bms);
      val start: Int = 0;
      val end: Int = 31;
      val level: Int = 0;
      println("generating colon positions...");
      
      // Hang Test stuff
      // Start Boundary
      var tempArr = bms.generateColonPositions(start, end, level);
      var stringtemp = fHandler.getLineString;
      System.out.println(stringtemp);
      System.out.println("StartBoundary for colonPosition 10 is " 
          + bms.getStartingBoundary(10) + " char: "
          + stringtemp.charAt(bms.getStartingBoundary(10)) + " and "
          + bms.getStartingBoundary(8) + " char: " 
          + stringtemp.charAt(bms.getStartingBoundary(8)));
      var temp = "" + stringtemp.substring(bms.getStartingBoundary(bms.getStartingBoundary(10) - 1) + 1, bms.getStartingBoundary(10));
      System.out.println(temp);
      System.out.println(temp.length());
      System.out.println( (stringtemp.charAt(bms.getStartingBoundary(10)) == '\"') );
      // End Boundary
      System.out.println(stringtemp);
      System.out.println("EndBoundary for colonPostion 10 is "
      + bms.getEndingBoundary(10) + " char: "
      + stringtemp.charAt(bms.getEndingBoundary(10)));
      //bms.testBitsScala();
    }

  }
  def ListBufferTest() { /*
    var S: ListBuffer[Tuple2[Int,Int]] = ListBuffer();
    S.insert(0, (3,2));
    S.insert(0, (1,2));
    var temp = S.remove(0);
    print(temp._1);
    println(temp._2);
    temp = S.remove(0);
    print(temp._1);
    println(temp._2);
    * 
    */
  }
  def ParserHashTest() {
    var testString = "urls.id.yun.checker.hello";
    System.out.println(testString.hashCode());
    var container: ArrayBuffer[String] = ArrayBuffer();
    container += testString;
    // Index starts at 0. Access with (index)
    System.out.println(container(0));
    var testParser = new MISONParser(container);
    System.out.println(testParser.queryFieldsInfo.nestingLevels);
    System.out.println(testParser.queryFieldsInfo.hashFields.size);
    var test = testParser.queryFieldsInfo.hashFields;
    var TestString2 = "urls.id.yun.checker.hello";
    System.out.println(test.contains(TestString2.hashCode()));
    System.out.println(TestString2.hashCode());
    System.out.println(testString.hashCode());
  }
  def ParseStringTest() {
    var testString = "urls.id.yun.checker.hello";
    System.out.println(testString.charAt(0) == 'u');
  }
  def main(args: Array[String]) {
    /*
    var mLeft, mRight: Bits = new Bits(41);
		var mLbit, mRbit: Bits = new Bits(1);
		println("Before: " + mLeft + " " + mRight);
    mRbit = mRight & -mRight.bits;
	  mLbit = mLeft & -mLeft.bits;
	  println("After : " + mLbit + " " + mRbit);
	  * 
	  */
    //println("List Buffer test");
    //ListBufferTest();
    //println("Bits test");
    //BitsTest();
    //println("filehandler test");
    //fileHandlerTest();
    //println("Bitmaps test");
    BitmapsTest();
    //fileHandlerTest()

    // Hang Test Function Calls
    //ParseStringTest();
    //ParserHashTest();
    /*
    var x: Int = 1 << 31;
    var y: Int = ~x;
    var z: Int = -10;
    // Goal: z in binary: 11111111 11111111 11111111 11110110 -> 01111111 11111111 11111111 11111011
    // for operation z >> 1 when z < 0;
    var mask: Int = 0x7FFFFFFF;

    var bits = new Bits(0x58855885);
    System.out.println(bits);
    bits.mirror();
    System.out.println(bits);
    bits.mirror();
    System.out.println(bits);
    * */
  }
}