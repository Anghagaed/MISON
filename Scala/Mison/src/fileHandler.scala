package fileHandler

import scala.collection.immutable.Vector
import scala.io.Source._
import java.io.IOException

// Comment by Hang : Vector is immutable and would be slower in performance compare to a mutable container 
// Need to handle multiple lines in file case. We want fileHandler to return one line at a time
// Need to change read and split and/or implement new functions
class fileHandler() {
  private var text: String = "";                //full text
  //private var currentLine = "";      
  private var vec = Vector.empty[String];        //full text split up into 32 character sections
  private var filePath: String = "";
  
  private var source = io.Source.fromFile(filePath)
  
  /*
  def fp = filePath
  def vector = vec
  def getSize = text.length()
  def getText = text
  */
  
  def setNewFilePath(newPath: String): Unit = {
    filePath = newPath;
    source = io.Source.fromFile(filePath)
  }
  
  //def getNextLine: Boolean = {
  //  return (read && split);
  //}
  
  // Return true if there is still another line to get. Return false if at end of file
  def hasNext:Boolean = {
    return source.hasNext;
  }
  
  def getNext: String = {
      return source.getLines().mkString;
  }
  
  def getFileVector: Vector[String] = {
    return vec;
  }
 
  // Colin code with some modification
  private def read: Boolean = {
    //val source = io.Source.fromFile(filePath)
    text = try source.mkString 
    catch {case e: IOException => 
             e.printStackTrace();
             return false;
    }
    finally println("donereading")//source.close()
    return true;
  }
  
  // Colin code with some modification
  private def split: Boolean = {
    for(a <- 0 until text.length() if a % 32 == 0){
      if(a + 32 <= text.length()){
        vec = vec:+(text.substring(a, a+32))
      }
      else{
        vec = vec:+(text.substring(a, text.length()))

      }
    }
    return true;
  }
}