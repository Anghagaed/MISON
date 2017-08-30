package fileHandler

import scala.collection.immutable.Vector
import scala.io.Source._
import java.io.IOException

class fileHandler(filePath: String) {
  var text: String = ""
  var vec = Vector.empty[String]
  def fp = filePath
  
  def vector = vec
  
  def read{
    val source = io.Source.fromFile(filePath)
    text = try source.mkString 
    catch {case e: IOException => 
             e.printStackTrace()
             "Couldn't read file"
    }
    finally source.close()
  }
  
  def getSize = text.length()
  
  def getText = text
  
  def split{
    for(a <- 0 until text.length() if a % 32 == 0){
      if(a + 32 <= text.length()){
        vec = vec:+(text.substring(a, a+32))
      }
      else{
        vec = vec:+(text.substring(a, text.length()))

      }
    }
  }
}