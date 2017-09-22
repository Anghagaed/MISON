package Parser
import fileHandler._;
import Bitmaps._
import scala.collection.mutable._;

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

  }

  // Constructor: on
  var queryFieldsInfo: queryFields = new queryFields(queryFieldsList);
  private var fileHandler: fileHandler = new fileHandler();
  private var result: ArrayBuffer[String] = new ArrayBuffer[String];
  private var recordFoundInLine: Int = 0;
  private var currentRecord: String = "";
  private var defaultArrayLayers: Int = 0;
  private var matchingFieldNumber: Int = 0;

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
      var stringSplitted = fileHandler.getFileVector;
      var bitmaps: Bitmaps = new Bitmaps(
        queryFieldsInfo.nestingLevels,
        defaultArrayLayers,
        stringSplitted);
      currentRecord = fileHandler.getLineString;
      var initialColonPos = bitmaps.generateColonPositions(0, currentRecord.length - 1, 0);
      matchingFieldNumber = 0;
      var queryResult = parseLine(0, "", initialColonPos);
      if (queryResult) {
        result += currentRecord;
      }
    }
    return true;
  }

  // Parse one record (line) and determine if the record is part of the query.
  // Return true for success, false for failure
  private def parseLine(curLevel: Int, append: String, colonPos: ArrayBuffer[Int]): Boolean = {
    var recordValue: String = "";
    for (i <- 0 until colonPos.length) {
      var pos = currentRecord.indexOf("\"", colonPos(i) - 2);
      // Error Checking - REMOVE FOR FINAL VERSION
      if (pos == -1) {
        System.out.println("This record: " + currentRecord + "\n has no quotes at all");
        return false;
      }
      var currentField: String = append + currentRecord.substring(pos, colonPos(i) - 2);

      if (queryFieldsInfo.hashFields.contains(currentField.hashCode())) {
        var nextChar: Char = currentRecord.charAt(colonPos(i) + 1);
        // Entering another nesting level case
        if (nextChar == '{') {

        } // Element is an array
        else if (nextChar == '[') {

        } else {
        }

        // Check if all fields were matched
        if (matchingFieldNumber == queryFieldsInfo.hashFields.size) {
          return true;
        }
      }
    }
    return false;
  }
}