package fileHandler

import scala.io.Source._
import java.io.IOException
import scala.collection.mutable.ArrayBuffer

// Comment by Hang : Array is immutable and would be slower in performance compare to a mutable container 
// Need to handle multiple lines in file case. We want fileHandler to return one line at a time
// Need to change read and split and/or implement new functions
class fileHandler() {
  private var text: String = "";
  //private var currentLine = "";      
  private var arr = ArrayBuffer.empty[String];
  private var filePath: String = "";

  private var it: Option[Iterator[String]] = None

  def setNewFilePath(newPath: String): Unit = {
    filePath = newPath;
    it = Some(io.Source.fromFile(filePath).getLines)
  }

  //def getNextLine: Boolean = {
  //  return (read && split);
  //}

  /*
  // Return true if there is still another line to get. Return false if at end of file
  def hasNext:Boolean = {
    return io.Source.fromFile(filePath).hasNext;
  }
  * */

  // Reads next line
  def getNext: Boolean = {
    if (it.get.hasNext) {
      text = it.get.next()
      split
      return true;
    }
    return false;
  }

  def getFileArray: ArrayBuffer[String] = {
    return arr;
  }

  def getLineString: String = {
    return text;
  }

  /*
  // Colin code with some modification
  private def read: Boolean = {
    //val source = io.Source.fromFile(filePath)
    text = try io.Source.fromFile(filePath).mkString 
    catch {case e: IOException => 
             e.printStackTrace();
             return false;
    }
    finally io.Source.fromFile(filePath).close()
    return true;
  }
  */

  // Colin code with some modification
  private def split: Boolean = {
    arr = ArrayBuffer.empty[String];
    for (a <- 0 until text.length() if a % 32 == 0) {
      if (a + 32 <= text.length()) {
        arr = arr :+ (text.substring(a, a + 32))
      } else {
        arr = arr :+ (text.substring(a, text.length()))

      }
    }
    return true;
  }
}