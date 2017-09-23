package Parser
import fileHandler._;
import Bitmaps._
import scala.collection.mutable._;
import scala.collection.immutable.HashMap;

/* MISON Simple Parser without speculative loading.
 * Argument: 		queryFieldList arrays of query fields. i.e. SELECT [a, b, c]... a, b, c 
 * 							will be in queryFieldList
 * 							filePaths arrays of file paths. More then one if the table is split 
 * 							into multiple files
 */

class MISONParser(queryFieldsList: ArrayBuffer[String],
                  filePaths: ArrayBuffer[String] = new ArrayBuffer[String]) {

  // ADT to hold calculate and holds levels of necessary nesting for query
  // and string hashing the query fields
  class queryFields(queryFieldsList: ArrayBuffer[String]) {
    var nestingLevels: Int = 0;
    var hashFields: HashSet[Int] = createHashField(queryFieldsList);
    var fieldsOrder: scala.collection.immutable.HashMap[String, Int]
    = createFieldsOrder(queryFieldsList);
    
    private def createFieldsOrder(queryFieldsList: ArrayBuffer[String])
    :scala.collection.immutable.HashMap[String, Int]  = {
      var order = new scala.collection.immutable.HashMap[String, Int]();
      for (i <- 0 until queryFieldsList.length) {
        order = order + (queryFieldsList(i) -> i);
      }
      return order;
    }
    
    private def createHashField(queryFieldsList: ArrayBuffer[String]): HashSet[Int] = {
      var splitCharacter: String = ".";
      var hashQuery: HashSet[Int] = new HashSet();
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
    // Gets number of query fields per level
    def getNumQueriedFields(): ArrayBuffer[Int] = {
      var numQueryFieldsList = new ArrayBuffer[Int]();
      for(i <- 0 until nestingLevels) {
        // count number of query fields in level i
        for(e <- queryFieldsList) {
          
        }
      }
      return numQueryFieldsList;
    }
  }

  // Constructor: on
  var queryFieldsInfo: queryFields = new queryFields(queryFieldsList);
  private var fileHandler: fileHandler = new fileHandler();
  private var result: ArrayBuffer[String] = new ArrayBuffer[String];
  private var recordFoundInLine: Int = 0;
  private var currentRecord: String = "";
  private var defaultArrayLayers: Int = 0;
  private var matchingFieldNumber: Int = 0;
  private var bitmaps: Bitmaps = null;
  private var lineRecordValue: String = "";
  // Constructor Off
  // Main Function that parse the file and return arrayBuilder of String for result
  def parseQuery(): ArrayBuffer[String] = {
    result.clear();
    for (i <- 0 until filePaths.length) {
      parseFile(filePaths(i));
    }
    return result;
  }

  // Parse one file and add all positive tuples into var result.
  // Return true for success, false for failure
  private def parseFile(filePath: String): Boolean = {
    fileHandler.setNewFilePath(filePath);

    // Go through entire file one line at a time
    while (fileHandler.getNext) {
      val stringSplitted = fileHandler.getFileVector;
      bitmaps = new Bitmaps(
        queryFieldsInfo.nestingLevels,
        defaultArrayLayers,
        stringSplitted);
      currentRecord = fileHandler.getLineString;
      val initialColonPos = bitmaps.generateColonPositions(0, currentRecord.length - 1, 0);
      matchingFieldNumber = 0;
      val queryResult = parseLine(0, "", initialColonPos);
      if (queryResult) {
       // result += currentRecord;
       // Extract relevant fields value
      }
    }
    return true;
  }

  // Parse one record (line) and determine if the record is part of the query.
  // Return true for success, false for failure
  private def parseLine(curLevel: Int, append: String, colonPos: ArrayBuffer[Int]): Boolean = {
    var recordValue: String = "";
    for (i <- 0 until colonPos.length) {
      // end pos of field name, no - 1 due to quirks of scala string.substring(startIndex, endIndex)
      var endPos = bitmaps.getStartingBoundary(colonPos(i));
      // start pos of field name
      var startPos = bitmaps.getStartingBoundary(endPos - 1) + 1;
      
      // Error Checking - REMOVE FOR FINAL VERSION
      if (endPos == -1 || startPos == -1) {
        System.out.println("startPos: " + startPos + " endPos: " + endPos);
        System.out.println("This record: " + currentRecord + "\n has no quotes at all");
        return false;
      }
      
      val currentField = append + currentRecord.substring(startPos, endPos);

      if (queryFieldsInfo.hashFields.contains(currentField.hashCode())) {
        var nextChar: Char = currentRecord.charAt(colonPos(i) + 1);
        // Entering another nesting level case
        if (nextChar == '{') {

        } 
        // Element is an array
        else if (nextChar == '[') {

        } 
        // Field matches. Add the field element into result
        else {
          endPos = bitmaps.getEndingBoundary(colonPos(i));
          startPos = colonPos(i) + 1;
          if ( currentRecord.charAt(startPos) == '\"' ) {
            // Change startPos and endPos to compensate for extra " character
            startPos = startPos + 1;
            endPos = endPos - 1;
          }
          val fieldValue = currentRecord.substring(startPos, endPos);
          // TODO: Put this fieldValue somewhere for storage
        }

        // Check if all fields were matched
        // Might need to reformat the string currentRecord?
        if (matchingFieldNumber == queryFieldsInfo.hashFields.size) {
          
          return true;
        }
      }
    }
    return false;
  }
}