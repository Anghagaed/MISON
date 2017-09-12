//import Bits._; // import not necessary when Bitmaps is implemented
import Bitmaps._;
import Bits._;
import fileHandler.fileHandler;
import Parser._;
//import scala.collection.mutable.Stack;
import scala.collection.immutable._;
import scala.collection.mutable._;
object Main {
  def BitsTest()
  {
    var test : Bits = new Bits(1);
    var test2 : Bits = new Bits(2);
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
    println("test.get(0) = " + test.get(0)+ "\ttest.get(1) = " + test.get(1));
    test.set(3, 1); println("test.set(3, 1) = " + test);
    print("test: " + test + "  test.flip: "); test.flip(); println(test); 
  }
  def fileHandlerTest()
  {
    println("Begin Testing")
    val fHandler = new fileHandler("./Test Files/jsonTest4.txt");
    fHandler.read
    fHandler.split
    for(a <- 0 until fHandler.vector.size){
      println(fHandler.vector(a))
    }
    println("End Testing")
  }
  def BitmapsTest()
  {
    val fHandler = new fileHandler("./Test Files/jsonTest1.txt");
    fHandler.read
    fHandler.split
    val layers = 2;
    val arrayLayers = 3;
    val bms: Bitmaps = new Bitmaps(layers, arrayLayers, fHandler.vector);
    bms.createBitmap;
    println(bms);
  }
  def ListBufferTest()
  {
    var S: ListBuffer[Tuple2[Int,Int]] = ListBuffer();
    S.insert(0, (3,2));
    S.insert(0, (1,2));
    var temp = S.remove(0);
    print(temp._1);
    println(temp._2);
    temp = S.remove(0);
    print(temp._1);
    println(temp._2);
  }
  def ParserHashTest() {
    var testString = "urls.id.yun.checker.hello";
    System.out.println(testString.hashCode());
    var container : ArrayBuffer[String] = ArrayBuffer();
    container += testString;
    // Index starts at 0. Access with (index)
    System.out.println(container(0));
    var testParser: MISONParser = new MISONParser(container);
    System.out.println(testParser.queryInfo.levels);
    System.out.println(testParser.queryInfo.hashFields.size);
    
  }
  def main(args: Array[String])
  {
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
    //BitmapsTest();
    ParserHashTest();
  }
}