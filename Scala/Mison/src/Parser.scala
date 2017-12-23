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
//package org.apache.spark.Parser
package Parser;

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

  //println("IN fking MISON");
  var queryFieldsInfo: queryFields = new queryFields(queryFieldsList);
  private var fileHandler: fileHandler = new fileHandler();
  private var result: ArrayBuffer[String] = new ArrayBuffer[String];
  private var currentRecord: String = "";
  private var defaultArrayLayers: Int = 0;
  private var matchingFieldNumber: Int = 0;
  private var bitmaps: Bitmaps = null;
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
    defaultArrayLayers = 4; // please change this
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
  }

  private def parseLine(curLevel: Int,
    append: String,
    colonPos: ArrayBuffer[Int],
    nextLevelColon: Int = -1): Boolean = {
    for (i <- colonPos.length - 1 to 0 by -1) {
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
    return false;
  }
  // Only one parameter will be have valid input, the other parameter will be -01
  private def parseArray(startInput: Int, endInput: Int, level: Int): String = {
    // Initialize start or end depending on which input is invalid
    // Issues to fix, brackets are not level handled
    // Will need to fix the two functions to handle levels
    var start = -1;
    var end = -1;
    if (startInput == -1) {
      start = bitmaps.getNextLeftBracket(endInput);
      end = endInput;
    }
    if (endInput == -1) {
      start = startInput;
      end = bitmaps.getNextRightBracket(startInput);
    }
    
    var commaPos = bitmaps.generateCommaPositions(start, end, level);
    val output = parseArrayField(level, commaPos, end);
    return "[" + output + "]";
  }
  
  private def parseArrayField(curLevel: Int, commaPos: ArrayBuffer[Int], end: Int): String = {
     var output = "";
     
     // Grab the first field in the array
     output = output + arrayFirstField(curLevel, commaPos(0));
     
     // Grab all field inbetween first and last
     output = output + arrayIntermediate(curLevel, commaPos);
     
     // Grab the final field in the array
     output = output + arrayFinalField(curLevel, commaPos(commaPos.length - 1), end);
     return output;
  }
  
  // Handle index = 0 case
  private def arrayFirstField(curLevel: Int, commaPos: Int): String = {
    var output = "";
    if (DEBUG_FLAG == true) {
      if (commaPos == 0) {
        println("Array Field Error commaPos(i) or commaPos is 0");
        return "";
      }
    }
    val previousChar = currentRecord.charAt(commaPos - 1);
    if (previousChar == ']') {
      val arrayOutput = parseArray(-1, commaPos - 1, curLevel + 1);
      output = output + arrayOutput + ", ";
    } else if (previousChar == '}') {
    
    } else {
      val end = commaPos;
      val start = bitmaps.getFirstStart(end);
      
      val currentField = currentRecord.substring(start, end);  
      output = output + currentField + ", ";
    }
    return output;
  }
  // Handles index = Length - 1 case
  private def arrayFinalField(curLevel: Int, commaPos: Int, endInput: Int): String = {
    var output = "";
    var startTemp = commaPos + 1;
    var nextChar = currentRecord.charAt(startTemp);
    while (nextChar == ' ') {
      startTemp = startTemp + 1;
      nextChar = currentRecord.charAt(startTemp);
    }
    if (nextChar == '[') {
      val arrayOutput = parseArray(startTemp, -1, curLevel + 1);
      output = output + arrayOutput + ", ";
    } else if (nextChar == '{') {
      
    } else {
      //val (start, typeBool) = bitmaps.getFinalEnd(endInput);
      var start = -1;
      var end = -1;
      if (nextChar == '"') {
        // Start a colon
        //start = bitmaps.getFinalStart(end
        start = bitmaps.getFinalStart(endInput) + 1;
        end = endInput - 2;
        var charTemp = currentRecord.charAt(start);
        while (charTemp == ' ' || charTemp == '"') {
          start = start + 1;
          charTemp = currentRecord.charAt(start);
        }
      } else {
        start = startTemp;
        end = endInput;
      }
      val currentField = currentRecord.substring(start, end);  
      output = output + currentField + ", ";
    }
    return output;
  }
  // Handles from index = [1, length - 2]
  private def arrayIntermediate(curLevel: Int, commaPos: ArrayBuffer[Int]): String = {
    var output = "";
    
    for (i <- commaPos.length - 2 to 1 by -1) {
      if (DEBUG_FLAG == true) {
        if (commaPos == 0) {
          println("Array Field Error commaPos(i) or commaPos is 0");
          return "";
        }
      }
      
      val previousChar = currentRecord.charAt(commaPos(i) - 1);
      // Handles internal nesting
      if (previousChar == ']') {
        val arrayOutput = parseArray(-1, commaPos(i) - 1, curLevel + 1);
        output = output + arrayOutput + ", ";
      } else if (previousChar == '}') {
        
      } else {
        // No array nesting, can safely grab values
        val end = commaPos(i);
        val (start, typeBool) = bitmaps.getIntermediateStart(end);
        
        val currentField = currentRecord.substring(start, end);
        output = output + currentField + ", ";
      }
    }
    
    return output;
  }
}

// scalastyle:on println
