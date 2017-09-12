package Parser
import Bitmaps._
import scala.collection.mutable._;

class MISONParser(queryFieldsList: ArrayBuffer[String]) {
  // Constructor: on
  type stringHash = HashSet[Int];
  class queryFields (queryFieldsList: ArrayBuffer[String]) {
    var levels: Int = 0;
    var hashFields: HashSet[Int] =  hashQueryField(queryFieldsList);
    
    def hashQueryField(queryFieldsList: ArrayBuffer[String]) : HashSet[Int] = {
      var splitCharacter: String = ".";
      var hashQuery : HashSet[Int] = new HashSet();
      for (i <- 0 until queryFieldsList.length) {
        var fields: String = queryFieldsList(i);
        var index: Int = fields.indexOf(splitCharacter);
        var fieldLevels: Int = 0;
        while (index != -1) {
          // Has comma
          var subfield: String = fields.substring(0, index - 1);
          hashQuery += subfield.hashCode();
          fieldLevels += 1;
          index = fields.indexOf(splitCharacter, index + 1);
        }
        hashQuery += fields.hashCode();
        if (fieldLevels > levels) {
          levels = fieldLevels;
        }
      }
      return hashQuery;
    }
  }
  
  var queryInfo: queryFields = new queryFields(queryFieldsList);
  
  
}