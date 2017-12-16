/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package Parser

import fileHandler._
import Bitmaps._
import scala.collection.immutable.HashMap
import scala.collection.mutable._

// scalastyle:off println
class MISONParser(
    queryFieldsList: ArrayBuffer[String],
    filePaths: ArrayBuffer[String] = new ArrayBuffer[String],
    DEBUG_STATUS: Boolean = false) {

  class queryFields(queryFieldsList: ArrayBuffer[String]) {
    var nestingLevels: Int = 0;
    var levelCount: Int = 0;
    var hashFields: HashSet[Int] = null;
    createHashField();
    var fieldsOrder: scala.collection.immutable.HashMap[String, Int]
        = createFieldsOrder(queryFieldsList);

    private def createFieldsOrder(queryFieldsList: ArrayBuffer[String]):
      scala.collection.immutable.HashMap[String, Int] = {
      var order = new scala.collection.immutable.HashMap[String, Int]();
      for (i <- 0 until queryFieldsList.length) {
        order = order + (queryFieldsList(i) -> i);
      }
      return order;
    }

    def createHashField(): ArrayBuffer[Int] = {
      hashFields = new HashSet[Int];
      var numQueryFieldsList = new ArrayBuffer[Int]();
      var splitCharacter: String = ".";
      var hashCode = 0;
      for (fields <- queryFieldsList) {
        var index: Int = fields.indexOf(splitCharacter);
        var localFieldLevels: Int = 0;
        while (index != -1) {
          var subfield: String = fields.substring(0, index);
          hashCode = subfield.hashCode();
          if (!hashFields.contains(hashCode)) {
            hashFields += hashCode;
            if (localFieldLevels < numQueryFieldsList.size) {
              numQueryFieldsList(localFieldLevels) = numQueryFieldsList(localFieldLevels) + 1;
            } else {
              numQueryFieldsList.insert(localFieldLevels, 1);
            }
          }
          localFieldLevels += 1;
          index = fields.indexOf(splitCharacter, index + 1);
        }


        hashCode = fields.hashCode();
        if (!hashFields.contains(hashCode)) {
          hashFields += hashCode;
          if (localFieldLevels < numQueryFieldsList.size) {
            numQueryFieldsList(localFieldLevels) = numQueryFieldsList(localFieldLevels) + 1;
          } else {
            numQueryFieldsList.insert(localFieldLevels, 1);
          }
        }
        localFieldLevels += 1;
        if (localFieldLevels > nestingLevels) {
          nestingLevels = localFieldLevels;
        }
      }
      return numQueryFieldsList;
    }

    def getNumQueriedFields(): ArrayBuffer[Int] = {
      var numQueryFieldsList = new ArrayBuffer[Int]();
      var splitCharacter: Char = '.';
      for (i <- 0 to nestingLevels) {

        var uniqueQueryFields = new HashSet[String];
        for (e <- queryFieldsList) {
          val split = e.split(splitCharacter);
          if (i < split.size) {
            uniqueQueryFields.add(split(i));
          }
        }
        numQueryFieldsList.insert(i, uniqueQueryFields.size);
      }
      return numQueryFieldsList;
    }
  }

  println("IN fking MISON");
  var queryFieldsInfo: queryFields = new queryFields(queryFieldsList);
  private var fileHandler: fileHandler = new fileHandler();
  private var result: ArrayBuffer[String] = new ArrayBuffer[String];
  private var recordFoundInLine: Int = 0;
  private var currentRecord: String = "";
  private var defaultArrayLayers: Int = 0;
  private var matchingFieldNumber: Int = 0;
  private var bitmaps: Bitmaps = null;
  private var lineRecordValue: String = "";
  private val DEBUG_FLAG = DEBUG_STATUS;
  private var lineOutput: Array[String] = null;
  def parseQuery(): ArrayBuffer[String] = {
    result.clear();
    for (i <- 0 until filePaths.length) {
      parseFile(filePaths(i));
    }
    return result;
  }
  private def parseFile(filePath: String): Boolean = {
    fileHandler.setNewFilePath(filePath);

    while (fileHandler.getNext) {
      initLineParse();
      val initialColonPos = bitmaps.generateColonPositions(0, currentRecord.length - 1, 0);
      val queryResult = parseLine(0, "", initialColonPos);
      if (queryResult) {
        var output: String = lineOutput(0);
        for (i <- 1 until lineOutput.length) {
          output += "  ,  " + lineOutput(i);
        }
        result += output;
      }
      if (DEBUG_FLAG == true) {
        if (queryResult) {
          System.out.println("Record Matches");
        } else {
          System.out.println("Record does not match");
        }
      }
    }
    return true;
  }

  private def initLineParse() {
    val stringSplitted = fileHandler.getFileArray;
    currentRecord = fileHandler.getLineString;
    bitmaps = new Bitmaps(
      queryFieldsInfo.nestingLevels,
      defaultArrayLayers,
      stringSplitted,
      DEBUG_FLAG);

    if (DEBUG_FLAG == true) {
      System.out.println("CurrentRecord: " + currentRecord);
    }
    matchingFieldNumber = 0;
    lineOutput = new Array[String](queryFieldsInfo.fieldsOrder.size);
    defaultArrayLayers = 0;
  }

  private def parseLine(curLevel: Int,
    append: String,
    colonPos: ArrayBuffer[Int],
    nextLevelColon: Int = -1): Boolean = {
    println(curLevel + " Before Loop: " + append);
    println("colonPos.length " + colonPos.length);
    val test: String = "TestingString";
    println("testString: " + test);
    for (i <- colonPos.length - 1 to 0 by -1) {
      println(curLevel + " After Loop: " + append);
      println("colonPos.length " + colonPos.length);
      println("testString: " + test);
      var endPos = bitmaps.getStartingBoundary(colonPos(i));
      var startPos = bitmaps.getStartingBoundary(endPos - 1) + 1;

      if (DEBUG_FLAG == true) {
        System.out.println("startPos: " + startPos + " endPos: " + endPos);
        System.out.println("Colon Pos is: " + colonPos(i));
        if (endPos == -1 || startPos == -1) {
          System.out.println("startPos: " + startPos + " endPos: " + endPos);
          System.out.println("This record: " + currentRecord + "\n has no quotes at all");
          return false;
        }
      }

      val currentField = append + currentRecord.substring(startPos, endPos);

      if (DEBUG_FLAG == true) {
        System.out.println("currentField is " + currentField);
      }

      if (queryFieldsInfo.hashFields.contains(currentField.hashCode())) {
        var j = 1;
        var nextChar: Char = currentRecord.charAt(colonPos(i) + j);
        while (nextChar == ' ') {
          j += 1;
          nextChar = currentRecord.charAt(colonPos(i) + j);
        }
        if (nextChar == '{') {
          if (true) {
            var newColonPos: ArrayBuffer[Int] = null;
            if (i != 0) {
              newColonPos =
              bitmaps.generateColonPositions(colonPos(i), colonPos(i - 1), curLevel + 1);
            } else {
              println("At the final colon Position");
              println("colonPos at 0 is " + colonPos(i) + " Current level is " + curLevel);
              println("currentRecord final length is: " + currentRecord.length);
              if (nextLevelColon == -1) {
                newColonPos =
                bitmaps.generateColonPositions(colonPos(i), currentRecord.length - 1, curLevel + 1);
              } else {
                newColonPos =
                bitmaps.generateColonPositions(colonPos(i), nextLevelColon, curLevel + 1);
              }
            }
            var nextNextLevelColon: Int = -1;
            if (i != 0) {
              nextNextLevelColon = colonPos(i - 1);
            }
            val newAppend: String = currentField + '.';
            if (DEBUG_FLAG == true) {
              System.out.println(newAppend);
              System.out.println("newColonPosition size " + newColonPos.size + " and its num");
              for (i <- 0 until newColonPos.size) {
                System.out.print(newColonPos(i) + " ");
              }
              println("");
              System.out.println("Next level is " + (curLevel + 1));
              System.out.println("\nGoing to the next level and beyond");
            }
            matchingFieldNumber += 1;
            parseLine(curLevel + 1, newAppend, newColonPos, nextNextLevelColon);
          }
        } else if (nextChar == '[') {
          val valid = queryFieldsInfo.fieldsOrder.get(currentField);
          if (valid == None) {
            if (DEBUG_FLAG == true) {
              System.out.println("ISSUES WITH INPUTTED QUERY");
              System.out.println("Field " + currentField + " is not in the hashmap");
              System.out.println("THIS OCCURS WHEN TRYING TO GET AN ARRAY FIELD");
            }
          } else {
            val output: String = bitmaps.getArraySubString(colonPos(i), curLevel, currentRecord);
            if (DEBUG_FLAG == true) {
              System.out.println("Array Field output is " + output);
            }
            System.out.println(currentField);
            val pos = valid.get;
            lineOutput(pos) = output;
            matchingFieldNumber += 1;
          }
        } else {
          val valid = queryFieldsInfo.fieldsOrder.get(currentField);
          if (valid == None) {
            if (DEBUG_FLAG == true) {
              System.out.println("ISSUES WITH INPUTTED QUERY");
              System.out.println("Field " + currentField + " is not in the hashmap");
              System.out.println("THIS OCCURS WHEN TRYING TO GET SOMETHING");
            }
          } else {
            endPos = bitmaps.getEndingBoundary(colonPos(i));
            startPos = colonPos(i) + 1;
            while (currentRecord.charAt(startPos) == ' ') {
              startPos += 1;
            }
            if (currentRecord.charAt(startPos) == '\"') {
              startPos = startPos + 1;
              endPos = endPos - 1;
            }
            val fieldValue = currentRecord.substring(startPos, endPos);
            if (DEBUG_FLAG == true) {
              System.out.println("Match found");
              System.out.println(fieldValue + " is associated with " + currentField);
            }
            val pos = valid.get;
            lineOutput(pos) = fieldValue;
            matchingFieldNumber += 1;
          }
        }
        // println("Matching Field Number is " + matchingFieldNumber);
        // println("queryFieldsInfo.hashFields.size " + queryFieldsInfo.hashFields.size);
        if (matchingFieldNumber == queryFieldsInfo.hashFields.size) {
          if (DEBUG_FLAG == true) {
            System.out.println("Number is matching, all matches found");
          }
          return true;
        }

      }
    }
    println("Done with the loop " + curLevel);
    return false;
  }
}

// scalastyle:on println
