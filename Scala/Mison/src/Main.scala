import scala.collection.mutable.BitSet;
import Bitmap._;
object Main {
  def main(args: Array[String])
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
}