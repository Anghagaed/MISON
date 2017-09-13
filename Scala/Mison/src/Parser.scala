package Parser
import fileHandler._;
import Bitmaps._
import scala.collection.mutable._;

class MISONParser(queryFieldsList: ArrayBuffer[String], 
    filePaths: ArrayBuffer[String] = new ArrayBuffer[String]) {
  // ADT to hold calculate and holds levels of necessary nesting for query
  // and string hashing the query fields
  class queryFields (queryFieldsList: ArrayBuffer[String]) {
    var nestingLevels: Int = 0;
    var hashFields: HashSet[Int] =  createHashField(queryFieldsList);
    
    private def createHashField(queryFieldsList: ArrayBuffer[String]) : HashSet[Int] = {
      hashFields.clear();
      var splitCharacter: String = ".";
      var hashQuery : HashSet[Int] = new HashSet();
      for (i <- 0 until queryFieldsList.length) {
        var fields: String = queryFieldsList(i);
        var index: Int = fields.indexOf(splitCharacter);
        var localFieldLevels: Int = 0;
        while (index != -1) {
          // Has comma
          var subfield: String = fields.substring(0, index - 1);
          hashQuery += subfield.hashCode();
          localFieldLevels += 1;
          index = fields.indexOf(splitCharacter, index + 1);
        }
        hashQuery += fields.hashCode();
        if (localFieldLevels > nestingLevels) {
          nestingLevels = localFieldLevels;
        }
      }
      return hashQuery;
    }
    
  }
  // Constructor: on
  
  private var queryFieldsInfo: queryFields = new queryFields(queryFieldsList);
  private var fileHandler: fileHandler = new fileHandler;
  var result: ArrayBuffer[String] = new ArrayBuffer[String];
  // Constructor Off
  // Main Function that parse the file and return arrayBuilder of String for result
  def parseQuery(): ArrayBuffer[String] = {
    for (i <- 0 until filePaths.length) {
      parseFile(filePaths(i));
    }
    return result;
  }
  
  // Parse one file and add all positive tuples into var result.
  private def parseFile(filePath: String): Unit = {
    fileHandler.setNewFilePath(filePath);
    if (fileHandler.convertFileIntoVector) {
      var fileStrVector = fileHandler.getFileVector;
    }
    else {
      
    }
  }
 }