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
package Bitmaps

import Bits._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.math.ceil

// scalastyle:off println

class Bitmaps(layers: Int, arrayLayers: Int, wordSplit: ArrayBuffer[String],
DEBUG_FLAG: Boolean = false) {
  class mapContainer(layers: Int, arrayLayers: Int) {
    var escapeBitset, quoteBitset, colonBitset, commaBitset, lbracketBitset,
rbracketBitset, arraylbracketBitset, arrayrbracketBitset, structQBitset,
strBitset, structCBitset, structLBitset, structRBitset, structCMBitset,
structALBBitset, structARBBitset: Bits = new Bits(0);
    var levels: Array[Bits] = new Array[Bits](layers);
    var CMlevels: Array[Bits] = new Array[Bits](arrayLayers);
    for (i <- 0 until layers)
      levels(i) = new Bits(0);
    for (i <- 0 until arrayLayers)
      CMlevels(i) = new Bits(0);
  }
  private val B_INT = 32;
  private val B_ZERO: Bits = new Bits(0);
  private val B_ONE: Bits = new Bits(1);
  private var word: ArrayBuffer[String] = wordSplit;
  var map: Array[mapContainer] = new Array[mapContainer](word.size);
  for (i <- 0 until word.size)
    map(i) = new mapContainer(layers, arrayLayers);

  def createBitmap: Unit = {
    fillBits();
    convertToStruct();
    fillColonBits();
  }
  def fillBits(): Unit = {
    var prev: Array[Char] = new Array[Char](2);
    for (j <- 0 until map.size) {
      for (i <- 0 until word(j).size) {
        val currentChar = word(j)(i);
        currentChar match {
          case '\\' =>
            map(j).escapeBitset.set(i, 1);
          case '\"' =>
            map(j).quoteBitset.set(i, 1);
            if (prev(0) != '\\' || (prev(0) == '\\' && prev(1) == '\\')) {
              map(j).structQBitset.set(i, 1);
            }
          case ':' =>
            map(j).colonBitset.set(i, 1);
          case ',' =>
            map(j).commaBitset.set(i, 1);
          case '{' =>
            map(j).lbracketBitset.set(i, 1);
          case '}' =>
            map(j).rbracketBitset.set(i, 1);
          case '[' =>
            map(j).arraylbracketBitset.set(i, 1);
          case ']' =>
            map(j).arrayrbracketBitset.set(i, 1);
          case _ =>
        }
        prev(1) = prev(0);
        prev(0) = word(j)(i);
      }
    }
  }
  def convertToStruct(): Unit = {
    var n: Int = 0;
    for (j <- 0 until map.size) {
      var mQuote: Bits = map(j).structQBitset;
      var mString: Bits = new Bits(0);
      for (i <- map(j).structQBitset.count until 0 by -1) {
        val m: Bits = mQuote ^ (mQuote - 1);
        mString = mString ^ m;
        mQuote &= (mQuote - 1);
        n += 1;
      }
      if (n % 2 == 1) {
        mString.flip();
      }
      map(j).strBitset = mString;
    }
    for (i <- 0 until map.size) {
      var strMask = map(i).strBitset;
      var temp: Bits = new Bits(0);
      temp = map(i).colonBitset;
      temp = temp - (temp & strMask);
      map(i).structCBitset = temp;
      temp = map(i).lbracketBitset;
      temp = temp - (temp & strMask);
      map(i).structLBitset = temp;
      temp = map(i).rbracketBitset;
      temp = temp - (temp & strMask);
      map(i).structRBitset = temp;
      temp = map(i).commaBitset;
      temp = temp - (temp & strMask);
      map(i).structCMBitset = temp;
      temp = map(i).arraylbracketBitset;
      temp = temp - (temp & strMask);
      map(i).structALBBitset = temp;
      temp = map(i).arrayrbracketBitset;
      temp = temp - (temp & strMask);
      map(i).structARBBitset = temp;
    }
  }
  def printMapSPECword(i: Int): Unit = {
    var output: String = "";
    output += "String is:  " + word(i);
    output += "\nStringR is: " + word(i).reverse;
    output += "\nPhase 1: ";
    output += "\n\\  bitset:  " + map(i).escapeBitset;
    output += "\n\"  bitset:  " + map(i).quoteBitset;
    output += "\n:  bitset:  " + map(i).colonBitset;
    output += "\n{  bitset:  " + map(i).lbracketBitset;
    output += "\n}  bitset:  " + map(i).rbracketBitset;
    output += "\n,  bitset:  " + map(i).commaBitset;
    output += "\n[  bitset:  " + map(i).arraylbracketBitset;
    output += "\n]  bitset:  " + map(i).arrayrbracketBitset;
    output += "\nPhase 2: ";
    output += "\nSQ bitset:  " + map(i).structQBitset;
    output += "\nPhase 3: ";
    output += "\nstrbitset:  " + map(i).strBitset;
    output += "\nPhase 4: ";
    output += "\nSC bitset:  " + map(i).structCBitset;
    for (j <- 0 until map(i).levels.size) {
      output += "\nL" + j + ":         ";
      output += map(i).levels(j);
    }
    output += "\n\n";
    println(output);
  }

  def fillColonBits(): Unit = {
    for (i <- 0 until map.size) {
      for (j <- 0 until map(i).levels.size)
        map(i).levels(j) = map(i).structCBitset;
    }
    var mLeft, mRight: Bits = new Bits(0);
    var mLbit, mRbit: Bits = new Bits(0);

    val lvls: Int = map(0).levels.size;

    var S: ListBuffer[Tuple2[Int, Bits]] = ListBuffer();

    for (i <- 0 until map.size) {
      mLeft = map(i).lbracketBitset;
      mRight = map(i).rbracketBitset;
      do {
        while (!(mLbit == 0) && (mRbit == 0 || mLbit < mRbit)) {
          S.insert(0, (i, mLbit));
          mLeft = mLeft & (mLeft - 1);
          mLbit = mLeft & -mLeft.bits;
        }
        if (!(mRbit == 0)) {
          val pop = S.remove(0);
          val j = pop._1;
          mLbit = pop._2;
          if (0 < S.size && S.size <= lvls) {
            var flip: Bits = new Bits(0);
            if (i == j) {
              flip = mRbit - mLbit;
              flip.flip();
              map(i).levels(S.size - 1) &= flip;
            } else {
              map(j).levels(S.size - 1) &= mLbit - 1;
              flip = mRbit - 1;
              flip.flip();
              map(i).levels(S.size - 1) &= flip;
              for (k <- j + 1 until i) {
                map(k).levels(S.size - 1) = new Bits(0);
              }
            }
          }
        }
        mRight &= mRight - 1;
      } while (!(mRbit == 0));
    }
    for (a <- 0 until map.size) {
      for (b <- lvls - 1 until 0 by -1) {
        val temp1 = map(a).levels(b);
        val temp2 = map(a).levels(b - 1);
        map(a).levels(b) = temp1 - (temp1 & temp2);
      }
    }
  }
  def generateColonPositions(start: Int, end: Int, level: Int): ArrayBuffer[Int] = {
    if (level >= this.layers) {
      if (DEBUG_FLAG == true) {
        System.out.println("Bitmaps: GENERATE COLON POS ERROR");
        System.out.println("Input level is: " + level + " but max layers is: " + (this.layers - 1));
      }
      return new ArrayBuffer[Int]();
    } else {
      var colonPositions = new ArrayBuffer[Int]();
      var mcolon: Bits = new Bits(0);
      for (i <- (start / B_INT) until ceil(end.toDouble / B_INT).toInt) {
        mcolon = map(i).levels(level);
        val tempColPos = colonPositions;
        while (!(mcolon == 0)) {
          val mBit = (mcolon & -mcolon.bits) - 1;
          var offset: Int = i * B_INT + mBit.count();
          if (start <= offset && offset <= end) {
            colonPositions = (offset) +: colonPositions;
          }
          mcolon = mcolon & (mcolon - 1);
        }
      }
      return colonPositions;
    }
  }
  def generateCommaPositions(start: Int, end: Int, level: Int): ArrayBuffer[Int] = {
    var commaPositions = new ArrayBuffer[Int]();
    var mcomma: Bits = new Bits(0);
    for (i <- (start / B_INT) until ceil(end.toDouble / B_INT).toInt) {
      mcomma = map(i).CMlevels(level);
      while (!(mcomma == 0)) {
        val mBit = (mcomma & -mcomma.bits) - 1;
        var offset: Int = i * B_INT + mBit.count();
        if (start <= offset && offset <= end) {
          commaPositions = (offset) +: commaPositions;
        }
        mcomma = mcomma & (mcomma - 1);
      }
    }
    commaPositions.foreach(x => print(s"${x} "));
    return commaPositions;
  }
  override def toString: String = {
    var output: String = "";
    for (i <- 0 until word.size) {
      output += "String is:  " + word(i);
      output += "\nStringR is: " + word(i).reverse;
      output += "\nPhase 1: ";
      output += "\n\\  bitset:  " + map(i).escapeBitset;
      output += "\n\"  bitset:  " + map(i).quoteBitset;
      output += "\n:  bitset:  " + map(i).colonBitset;
      output += "\n{  bitset:  " + map(i).lbracketBitset;
      output += "\n}  bitset:  " + map(i).rbracketBitset;
      output += "\n,  bitset:  " + map(i).commaBitset;
      output += "\n[  bitset:  " + map(i).arraylbracketBitset;
      output += "\n]  bitset:  " + map(i).arrayrbracketBitset;
      output += "\nPhase 2: ";
      output += "\nSQ bitset:  " + map(i).structQBitset;
      output += "\nPhase 3: ";
      output += "\nstrbitset:  " + map(i).strBitset;
      output += "\nPhase 4: ";
      output += "\nSC bitset:  " + map(i).structCBitset;
      for (j <- 0 until map(i).levels.size) {
        output += "\nL" + j + ":         ";
        output += map(i).levels(j);
      }
      output += "\n\n";
    }
    return output;
  }
  def getStartingBoundary(colonPosition: Int): Int = {
    var output: Int = -1;
    var startingLevel: Int = colonPosition / 32;
    var pos = 31 - (colonPosition % 32);
    for (i <- startingLevel to 0 by -1) {
      map(i).structQBitset.mirror();
      output = map(i).structQBitset.getNextOnPosition(pos);
      if (output != -1) {
        map(i).structQBitset.mirror();
        return (31 - output) + (32 * i);
      }
      map(i).structQBitset.mirror();
      pos = 0;
    }
    return -1;
  }
  def getArraySubString(colonPos: Int, level: Int): String = {
    var start = colonPos + 1;
    var end: Int = -1;
    var startWord: Int = (colonPos / 32);
    var endWord: Int = -1;
    var currentLevel: Int = level;
    var index = colonPos % 32;
    var i = startWord;
    while (i < map.size && currentLevel != 0) {
      var bothnegative = false;
      while (currentLevel != 0 && !bothnegative) {
        val arraylbracketpos = map(i).arraylbracketBitset.getNextOnPosition(index);
        val arrayrbracketpos = map(i).arrayrbracketBitset.getNextOnPosition(index);
        if (arraylbracketpos != -1 && arraylbracketpos < arrayrbracketpos) {
          currentLevel += 1;
          index = arraylbracketpos;
        }
        else if (arrayrbracketpos != -1 && (arrayrbracketpos < arraylbracketpos)) {
          currentLevel -= 1;
          index = arrayrbracketpos;
        } else {
          bothnegative = true;
        }
        if (currentLevel == 0) {
          endWord = i;
          end = index;
        }
      }
      index = 0;
    }
    var subStr: String = "";
    for (i <- startWord until endWord)
      subStr += word(i);
    subStr += word(endWord).substring(0, end);
    return subStr;
  }
  def getArraySubString(colonPos: Int, level: Int, str: String): String = {
    var start = colonPos + 2;
    var end: Int = -1;
    var startWord: Int = start / 32;
    var count: Int = 1;
    var index = start % 32;
    var i = startWord;
    var foundEndpt = false;

    while (i < map.size && !foundEndpt) {
      var bothnegative = false;
      while (!foundEndpt && !bothnegative) {
        val arraylbracketpos = map(i).arraylbracketBitset.getNextOnPosition(index);
        val arrayrbracketpos = map(i).arrayrbracketBitset.getNextOnPosition(index);
        if (arraylbracketpos != -1 && arrayrbracketpos != -1) {
          if (arraylbracketpos > arrayrbracketpos) {
            count -= 1;
            index = arrayrbracketpos + 1;
            println("a");
          } else {
            count += 1;
            index = arraylbracketpos + 1;
            println("b");
          }
        } else if (arraylbracketpos != -1) {
          count += 1;
          index = arraylbracketpos + 1;
          println("c");
        } else if (arrayrbracketpos != -1) {
          count -= 1;
          index = arrayrbracketpos + 1;
          println("d");
        } else {
          println("Both are Negative " + count);
          bothnegative = true;
        }
        if (count == 1) {
          println("Found EndPt");
          end = i * B_INT + index;
          foundEndpt = true;
        }
      }
      index = 0;
      i += 1;
    }
    println(start + " " + end);
    var subStr: String = str.substring(colonPos + 1, end);
    return subStr;
  }
  def testBitsScala(): Unit = {
    var x = -1;
    var y: Bits = new Bits(0x80000001);
    do {
      x = y.getNextOnPosition(x + 1);
    } while (x != -1);

  }

  def getEndingBoundary(colonPosition: Int): Int = {
    var startingLevel: Int = colonPosition / 32;
    var pos = colonPosition % 32;
    for (i <- startingLevel until map.length by 1) {
      var commaPos = map(i).structCMBitset.getNextOnPosition(pos);
      var bracketPos = map(i).structRBitset.getNextOnPosition(pos);

      if (commaPos != -1 && bracketPos != -1) {
        val returnVal = if (commaPos < bracketPos) commaPos else bracketPos;
        return returnVal + (32 * i);
      } else if (commaPos != -1) {
        return commaPos + (32 * i);
      } else if (bracketPos != -1) {
        return bracketPos + (32 * i);
      }

      pos = 0;
    }
    return -1;
  }

  def getEndRightBraces: Int = {
    var pos = -1;
    var temp = map(map.length - 1).structRBitset.getNextOnPosition(0);
    while (temp != -1) {
      pos = temp;
      temp = map(map.length - 1).structRBitset.getNextOnPosition(temp + 1);
    }
    pos = pos + 32 * (map.length - 2);
    println(pos);
    return pos;
  }
  createBitmap;
  toString()
}

// scalastyle:on println
