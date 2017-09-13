package fileHandler

import scala.collection.immutable.Vector
import scala.io.Source._
import java.io.IOException

// Comment by Hang : Vector is immutable and would be slower in performance compare to a mutable container 

class fileHandler() {
  var text: String = "";
  var vec = Vector.empty[String];
  var filePath: String = "";
  
  /*
  def fp = filePath
  def vector = vec
  def getSize = text.length()
  def getText = text
  */
  
  def setNewFilePath(newPath: String): Unit = {
    filePath = newPath;
    convertFileIntoVector;
  }
  
  private def convertFileIntoVector: Boolean = {
    return (read && split);
  }
  
  def getFileVector: Vector[String] = {
    return vec;
  }
  
  // Collin code with some modification
  private def read: Boolean = {
    val source = io.Source.fromFile(filePath)
    text = try source.mkString 
    catch {case e: IOException => 
             e.printStackTrace();
             return false;
    }
    finally source.close()
    return true;
  }
  
  // Collin code with some modification
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