import Bitmap._;
import fileHandler.fileHandler;
object Main {
  def bitmapTest()
  {
    var test : Bitmap = new Bitmap(1);
    var test2 : Bitmap = new Bitmap(2);
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
  def main(args: Array[String])
  {
    println("bitmap test");
    bitmapTest();
    println("filehandler test");
    fileHandlerTest();
  }
}