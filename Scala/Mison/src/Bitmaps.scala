package Bitmaps
import Bits._
//import scala.util.control.Breaks._
import scala.collection.mutable.ArrayBuffer
/* https://www.scala-lang.org/api/current/scala/collection/immutable/Stack.html
 * (Since version 2.11.0) Stack is an inelegant and potentially
 * poorly-performing wrapper around List. Use List instead: 
 * stack push x becomes x :: list; stack.pop is list.tail.
 * 
 * List is immutable, so in order to use .pop() functionality,
 * ListBuffer is used as an alternative.
 */
import scala.collection.mutable.ListBuffer;
import scala.math.ceil;
class Bitmaps(layers: Int, arrayLayers: Int, wordSplit: ArrayBuffer[String], DEBUG_FLAG: Boolean = false) {

  // constructor:on
  class mapContainer(layers: Int, arrayLayers: Int) {
    var escapeBitset, quoteBitset, colonBitset, commaBitset, lbracketBitset, rbracketBitset, arraylbracketBitset, arrayrbracketBitset, structQBitset, strBitset, structCBitset, structLBitset, structRBitset, structCMBitset, structALBBitset, structARBBitset: Bits = new Bits(0);
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
  // constructor:off

  // methods:on
  // createBitmap - Creates the bitmap with given split string arrays
  def createBitmap: Unit = {
    fillBits(); // phase 1 & 2
    convertToStruct(); // phase 3 (includes commas)
    fillColonBits(); // phase 4
    fillCommaBits(); // phase 4 (but with commas)
    
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
            if (prev(0) != '\\' || (prev(0) == '\\' && prev(1) == '\\'))
              map(j).structQBitset.set(i, 1);
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
          case _ => // do nothing for others
        }
        prev(1) = prev(0);
        prev(0) = word(j)(i);
      }
    }
  }
  def convertToStruct(): Unit = {
    var n: Int = 0; // Number of quotes count
    for (j <- 0 until map.size) {
      var mQuote: Bits = map(j).structQBitset;
      var mString: Bits = new Bits(0);
      // Loop until the number of quote in mQuote is 0
      for (i <- map(j).structQBitset.count until 0 by -1) {
        // Extract and smear the rightmost 1  m = S(mQuote) = mQuote ^ (mQuote - 1)
        val m: Bits = mQuote ^ (mQuote - 1);
        // Extend mstring to the rightmost 1 mString = mString ^| m
        mString = mString ^ m;
        // remove the rightmost 1 mQuote = R(mQuote) = mQuote & (mQuote - 1)
        mQuote &= (mQuote - 1);
        n += 1;
      }
      // Flip mString if necessary given n mod 2 = 1
      if (n % 2 == 1)
        mString.flip();
      map(j).strBitset = mString;
    }
    for (i <- 0 until map.size) {
      var strMask = map(i).strBitset;
      var temp: Bits = new Bits(0);
      // Colon
      temp = map(i).colonBitset;
      temp = temp - (temp & strMask);
      map(i).structCBitset = temp;
      // left Brace
      temp = map(i).lbracketBitset;
      temp = temp - (temp & strMask);
      map(i).structLBitset = temp;
      // right Brace
      temp = map(i).rbracketBitset;
      temp = temp - (temp & strMask);
      map(i).structRBitset = temp;

      //for support of array fields
      // comma
      temp = map(i).commaBitset;
      temp = temp - (temp & strMask);
      map(i).structCMBitset = temp;
      // left bracket
      temp = map(i).arraylbracketBitset;
      temp = temp - (temp & strMask);
      map(i).structALBBitset = temp;
      // right bracket
      temp = map(i).arrayrbracketBitset;
      temp = temp - (temp & strMask);
      map(i).structARBBitset = temp;
    }
  }
  def printMapSPECword(i: Int) = {
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
  // fillColonBits - Creates leveled colons
  def fillColonBits(): Unit = {
    // copy colon bitmap to leveled colon bitmaps
    for (i <- 0 until map.size) {
      for (j <- 0 until map(i).levels.size)
        map(i).levels(j) = map(i).structCBitset;
    }
    var mLeft, mRight: Bits = new Bits(0); // m(left), m(right)
    var mLbit, mRbit: Bits = new Bits(0); // m(left bit), m(right bit)

    val lvls: Int = map(0).levels.size; // Number of nesting levels

    // ListBuffer: Stack alternative
    var S: ListBuffer[Tuple2[Int, Bits]] = ListBuffer();

    for (i <- 0 until map.size) {
      mLeft = map(i).lbracketBitset;
      mRight = map(i).rbracketBitset;
      do // iterate over each right brace
      {
        // extract the rightmost 1 
        mRbit = mRight & -mRight.bits; 
        mLbit = mLeft & -mLeft.bits; 
        while (!(mLbit == 0) && (mRbit == 0 || mLbit < mRbit)) { 
          // 1 = "j", 2 = mLbit
          S.insert(0, (i, mLbit));
          mLeft = mLeft & (mLeft - 1); // remove the rightmost 1
          mLbit = mLeft & -mLeft.bits; // extract the rightmost 1        
        }
        if (!(mRbit == 0)) { 
          val pop = S.remove(0); // 0 = "j", 1 = mLbit
          val j = pop._1;
          mLbit = pop._2;
          if (0 < S.size && S.size <= lvls) // clear bits at the upper level
          {
            var flip: Bits = new Bits(0);
            if (i == j) // nested object is inside the word
            {
              flip = mRbit - mLbit;
              flip.flip();
              map(i).levels(S.size - 1) &= flip;
            } else // nested object is across multiple words
            {
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
        mRight &= mRight - 1; // remove the rightmost 1
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
  // fillCommaBits - creates leveled commas
  def fillCommaBits(): Unit = {
    // copy Comma bitmap to leveled Comma bitmaps
    for (i <- 0 until map.size) {
      for (j <- 0 until map(i).CMlevels.size)
        map(i).CMlevels(j) = map(i).structCMBitset;
    }
    var mLeft, mRight: Bits = new Bits(0); // m(left), m(right)
    var mLbit, mRbit: Bits = new Bits(0); // m(left bit), m(right bit)

    val lvls: Int = map(0).CMlevels.size; // Number of nesting CMlevels

    // ListBuffer: Stack alternative
    var S: ListBuffer[Tuple2[Int, Bits]] = ListBuffer();

    for (i <- 0 until map.size) {
      mLeft = map(i).arraylbracketBitset;
      mRight = map(i).arrayrbracketBitset;
      do // iterate over each right brace
      {
        // extract the rightmost 1 
        mRbit = mRight & -mRight.bits; 
        mLbit = mLeft & -mLeft.bits; 
        while (!(mLbit == 0) && (mRbit == 0 || mLbit < mRbit)) { 
          // 1 = "j", 2 = mLbit
          S.insert(0, (i, mLbit));
          mLeft = mLeft & (mLeft - 1); // remove the rightmost 1
          mLbit = mLeft & -mLeft.bits; // extract the rightmost 1        
        }
        if (!(mRbit == 0)) { 
          val pop = S.remove(0); // 0 = "j", 1 = mLbit
          val j = pop._1;
          mLbit = pop._2;
          if (0 <= S.size && S.size <= lvls) // clear bits at the upper level
          {
            var flip: Bits = new Bits(0);
            if (i == j) // nested object is inside the word
            {
              flip = mRbit - mLbit;
              flip.flip();
              map(i).CMlevels(S.size) &= flip;
            } else // nested object is across multiple words
            {
              map(j).CMlevels(S.size) &= mLbit - 1;
              flip = mRbit - 1;
              flip.flip();
              map(i).CMlevels(S.size) &= flip;
              for (k <- j + 1 until i-1) {
                map(k).CMlevels(S.size) = new Bits(0);
              }
            }
          }
        }
        mRight &= mRight - 1; // remove the rightmost 1
      } while (!(mRbit == 0));
    }
    for (a <- 0 until map.size) {
      for (b <- lvls - 1 until 0 by -1) {
        val temp1 = map(a).CMlevels(b);
        val temp2 = map(a).CMlevels(b - 1);
        map(a).CMlevels(b) = temp1 - (temp1 & temp2);
      }
    }
  }
  def generateAllColonPositions(start: Int, end: Int): ArrayBuffer[Int] = {
    var colonPositions = new ArrayBuffer[Int]();
    for (i <- (start / B_INT) until ceil(end.toDouble / B_INT).toInt) {
      var mcolon = map(i).structCBitset;
      while(!(mcolon == 0)) {
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
    if (level >= this.arrayLayers) {
      if (DEBUG_FLAG == true) {
        System.out.println("Bitmaps: GENERATE COMMA POS ERROR");
        System.out.println("Input level is: " + level + " but max arrayLayers is: " + (this.arrayLayers - 1));
      }
      return new ArrayBuffer[Int]();
    } else {
      var commaPositions = new ArrayBuffer[Int]();
      var mComma: Bits = new Bits(0);
      for (i <- (start / B_INT) until ceil(end.toDouble / B_INT).toInt) {
        mComma = map(i).CMlevels(level);
        while (!(mComma == 0)) { 
          val mBit = (mComma & -mComma.bits) - 1; 
          var offset: Int = i * B_INT + mBit.count();
          if (start <= offset && offset <= end) {
            commaPositions = (offset) +: commaPositions;
          }
          mComma = mComma & (mComma - 1);
        }
      }
      return commaPositions;
    }
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
      output += "\nSCM bitset: " + map(i).structCMBitset;
      for (j <- 0 until map(i).CMlevels.size) {
        output += "\nL" + j + ":         ";
        output += map(i).CMlevels(j);
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
  // Given a colon position to an array field, it generates a substring enclosed by array field
  // Returns null if enclosing the whole substring is impossible
  // Assumes that there is the end array bracket and colon position is valid
//  def getArraySubString(colonPos: Int, level: Int): String = {
//    var start = colonPos + 1; // start range of substring
//    var end: Int = -1; // end range of substring in endWord
//    var startWord: Int = (colonPos / 32); // word to begin searching
//    var endWord: Int = -1; // end word
//    var currentLevel: Int = level; // used to find the end bracket
//    var index = colonPos % 32; // index used to help searching for the end
//    // start search
//    var i = startWord;
//    while (i < map.size && currentLevel != 0) {
//      var bothnegative = false;
//      // extract next location for both '[' bracket and ']' bracket
//      while (currentLevel != 0 && !bothnegative) {
//        val arraylbracketpos = map(i).arraylbracketBitset.getNextOnPosition(index);
//        val arrayrbracketpos = map(i).arrayrbracketBitset.getNextOnPosition(index);
//
//        // left bracket is closer
//        if (arraylbracketpos != -1 && arraylbracketpos < arrayrbracketpos) {
//          currentLevel += 1;
//          index = arraylbracketpos;
//        } // right bracket is closer
//        else if (arrayrbracketpos != -1 && arrayrbracketpos < arraylbracketpos) {
//          currentLevel -= 1;
//          index = arrayrbracketpos;
//        } else
//          bothnegative = true;
//        // check for end condition
//        if (currentLevel == 0) {
//          endWord = i;
//          end = index;
//        }
//      }
//      // reset index
//      index = 0;
//    }
//    // generate the result
//    var subStr: String = "";
//    for (i <- startWord until endWord)
//      subStr += word(i);
//    subStr += word(endWord).substring(0, end);
//    return subStr;
//  }
  def getArraySubString(colonPos: Int, level: Int, str: String): String = {
    var start = colonPos + 2; // start range of loop
    var end: Int = -1; // end range of substring in endWord
    var startWord: Int = start / 32; // word to begin searching
    var count: Int = 1; // used to find the end bracket
    var index = start % 32; // index used to help searching for the end
    // start search
    var i = startWord;
    var foundEndpt = false;

    while (i < map.size && !foundEndpt) {
      // extract next location for both '[' bracket and ']' bracket
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
        // check for end condition
        if (count == 1) {
          end = i * B_INT + index;
          foundEndpt = true;
        }
      }
      // reset index
      index = 0;
      i += 1;
    }
    // generate the result
    println(start + " " + end);
    var subStr: String = str.substring(colonPos + 1, end);
    return subStr;
  }
  def testBitsScala() = {
    //System.out.//println("Testing testBitsScala");
    //Testing getNextOnPosition. Conclusion: It works
    ////System.out.//println("SQ bitset: " + map(0).structQBitset); 
    var x = -1;
    var y: Bits = new Bits(0x80000001);
    //System.out.//println("y bitset: " + y);
    do {
      //System.out.print("Call with " + (x + 1) + ": ");
      //x = map(0).structQBitset.getNextOnPosition(x+1);
      x = y.getNextOnPosition(x + 1);
      //System.out.//println(x);
    } while (x != -1);

  }
  // For {} Parsing
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
  // For {} Parsing
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
  
  def getNextLeftBracket(position: Int) = {
    
  }
  
  // for [] parsing
  def getIntermediateStart(commaPosition: Int): Int = {
    var startingLevel: Int = commaPosition / 32;
    var pos = commaPosition % 32;
    for (i <- startingLevel until map.length by 1) {
      var commaPos = map(i).structCMBitset.getNextOnPosition(pos);
      var colonPos = map(i).structCBitset.getNextOnPosition(pos);
      if (commaPos != -1 && colonPos != -1) {
        val returnVal = if (commaPos < colonPos) commaPos else colonPos;
        return returnVal + (32 * i);
      } else if (commaPos != -1) {
        return commaPos + (32 * i);
      } else if (colonPos != -1) {
        return colonPos + (32 * i);
      }
      pos = 0;
    }
    return -1;
  }
  
  // methods:off
  createBitmap;
  toString()
}