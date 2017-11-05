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
  def createBitmap: Unit = {
    fillBits(); // phase 1 & 2
    convertToStruct(); // phase 3/(pre)4
    fillColonBits(); // phase 4
    //println(toString())
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
      //println("i = " + i + "mRight: " + mRight+ " bits = " + mRight.bits);
      do // iterate over each right brace
      {
        //println("i = " + i);
        // extract the rightmost 1
        //        if(i == 36) {
        //          println("Before:\nmLbit =       " + mLbit + " bits = " + mLbit.bits);
        //          println("mRbit =       " + mRbit + " bits = " + mRbit.bits);
        //          println("mLeft =       " + mLeft + " bits = " + mLeft.bits);
        //          println("mRight =      " + mRight + " bits = " + mRight.bits);
        //        } 
        mRbit = mRight & -mRight.bits; // MARKED
        mLbit = mLeft & -mLeft.bits; // MARKED
        //        if(i == 36) {
        //          println("After:\nmLbit =       " + mLbit + " bits = " + mLbit.bits);
        //          println("mRbit =       " + mRbit + " bits = " + mRbit.bits);
        //          println("mLeft =       " + mLeft + " bits = " + mLeft.bits);
        //          println("mRight =      " + mRight + " bits = " + mRight.bits + "\n");
        //        } 
        while (!(mLbit == 0) && (mRbit == 0 || mLbit < mRbit)) { // MARKED
          // 1 = "j", 2 = mLbit
          S.insert(0, (i, mLbit));
          //println("i = " + i + " mLBit " + mLbit);
          mLeft = mLeft & (mLeft - 1); // remove the rightmost 1
          mLbit = mLeft & -mLeft.bits; // extract the rightmost 1        // MARKED
          //println("loop i = " +i);
        }
        //        if (i == 36) {
        //          println("i is 36 36 36 36 36");
        //          printMapSPECword(i);
        //          S.foreach(println);
        //        }
        if (!(mRbit == 0)) { // MARKED
          val pop = S.remove(0); // 0 = "j", 1 = mLbit
          val j = pop._1;
          mLbit = pop._2;
          //          println("i = " + i);
          //          println("j = " + j + " mLbit = " + mLbit);
          //          println("mRbit =       " + mRbit + "\n");
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
        //println("After while loop:\nBefore\nmRight =      " + mRight + " bits = " + mRight.bits)
        mRight &= mRight - 1; // remove the rightmost 1
        //println("After\nmRight =      " + mRight + " bits = " + mRight.bits + "\n")
      } while (!(mRbit == 0));

    }

    //    println(word(36));
    //    for (x <- 0 until lvls) {
    //      println("L" + x + ":" + map(36).levels(x));
    //    }

    for (a <- 0 until map.size) {
      for (b <- lvls - 1 until 0 by -1) {
        val temp1 = map(a).levels(b);
        val temp2 = map(a).levels(b - 1);
        map(a).levels(b) = temp1 - (temp1 & temp2);

      }

    }
  }
  //def generateColonPositions(start: Int, end: Int, level: Int): Vector[Int] = {
  //  var colonPositions = Vector.empty[Int];
  def generateColonPositions(start: Int, end: Int, level: Int): ArrayBuffer[Int] = {
    if (level >= this.layers) {
      if (DEBUG_FLAG == true) {
        System.out.println("Bitmaps: GENERATE COLON POS ERROR");
        System.out.println("Input level is: " + level + " but max layers is: " + (this.layers - 1));
      }
      return new ArrayBuffer[Int]();
    } else {
      //println("generate colon pos is called");
      var colonPositions = new ArrayBuffer[Int]();
      var mcolon: Bits = new Bits(0);
      for (i <- (start / B_INT) until ceil(end.toDouble / B_INT).toInt) {
        //        if (i == 36) {
        //          println("level: " + level);
        //          println("word:   " + word(i));
        //          println("wordr:  " + word(i).reverse);
        //        }
        mcolon = map(i).levels(level);
        while (!(mcolon == 0)) { // MARKED
          val mBit = (mcolon & -mcolon.bits) - 1; // MARKED
          //          if (i == 36) {
          //            println("mcolon: " + mcolon)
          //          }
          var offset: Int = i * B_INT + mBit.count();
          if (start <= offset && offset <= end) {
            colonPositions = (offset) +: colonPositions;
          }
          mcolon = mcolon & (mcolon - 1);
        }
      }
      ////println("Colon Position is: ");
      //for (i <- 0 until colonPositions.length) {
      //  print(colonPositions(i) + " ");
      //}
      //colonPositions.foreach(x => print(s"${x} "));
      ////println();
      //println("ccccccc");
      return colonPositions;
    }
  }
  def generateCommaPositions(start: Int, end: Int, level: Int): ArrayBuffer[Int] = {
    var commaPositions = new ArrayBuffer[Int]();
    var mcomma: Bits = new Bits(0);
    for (i <- (start / B_INT) until ceil(end.toDouble / B_INT).toInt) {
      mcomma = map(i).CMlevels(level);
      while (!(mcomma == 0)) { // MARKED
        val mBit = (mcomma & -mcomma.bits) - 1; // MARKED
        var offset: Int = i * B_INT + mBit.count();
        if (start <= offset && offset <= end) {
          commaPositions = (offset) +: commaPositions;
        }
        mcomma = mcomma & (mcomma - 1);
      }
    }
    ////println("Comma Position is: ");
    //for (i <- 0 until colonPositions.length) {
    //  print(colonPositions(i) + " ");
    //}
    commaPositions.foreach(x => print(s"${x} "));
    //println();
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
    /*
    //System.out.//println("colonPosition: " + colonPosition);
    //System.out.//println("StartingLevel: " + startingLevel);
    //System.out.//println("Pos " + pos);
    //System.out.//println("Starting loop");
    * 
    */
    for (i <- startingLevel to 0 by -1) {
      ////System.out.//println("Pos is " + pos);
      ////System.out.//println(map(i).structQBitset);
      map(i).structQBitset.mirror();
      ////System.out.//println(map(i).structQBitset);
      output = map(i).structQBitset.getNextOnPosition(pos);
      ////System.out.//println("i " + i + " " + output);
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
  def getArraySubString(colonPos: Int, level: Int): String = {
    var start = colonPos + 1; // start range of substring
    var end: Int = -1; // end range of substring in endWord
    var startWord: Int = (colonPos / 32); // word to begin searching
    var endWord: Int = -1; // end word
    var currentLevel: Int = level; // used to find the end bracket
    var index = colonPos % 32; // index used to help searching for the end
    // start search
    var i = startWord;
    while (i < map.size && currentLevel != 0) {
      var bothnegative = false;
      // extract next location for both '[' bracket and ']' bracket
      while (currentLevel != 0 && !bothnegative) {
        val arraylbracketpos = map(i).arraylbracketBitset.getNextOnPosition(index);
        val arrayrbracketpos = map(i).arrayrbracketBitset.getNextOnPosition(index);

        // left bracket is closer
        if (arraylbracketpos != -1 && arraylbracketpos < arrayrbracketpos) {
          currentLevel += 1;
          index = arraylbracketpos;
        } // right bracket is closer
        else if (arrayrbracketpos != -1 && arrayrbracketpos < arraylbracketpos) {
          currentLevel -= 1;
          index = arrayrbracketpos;
        } else
          bothnegative = true;
        // check for end condition
        if (currentLevel == 0) {
          endWord = i;
          end = index;
        }
      }
      // reset index
      index = 0;
    }
    // generate the result
    var subStr: String = "";
    for (i <- startWord until endWord)
      subStr += word(i);
    subStr += word(endWord).substring(0, end);
    return subStr;
  }
  def getArraySubString(colonPos: Int, level: Int, str: String): String = {
    //println(str);
    var start = colonPos + 2; // start range of loop
    var end: Int = -1; // end range of substring in endWord
    var startWord: Int = start / 32; // word to begin searching
    //var endWord:Int = -1;                // end word
    var count: Int = 1; // used to find the end bracket
    var index = start % 32; // index used to help searching for the end
    // start search
    var i = startWord;
    //println(index);
    //println(colonPos + 1);
    //println(str(start));
    var foundEndpt = false;
    //println("Starting while loop 1");

    while (i < map.size && !foundEndpt) {
      // extract next location for both '[' bracket and ']' bracket
      var bothnegative = false;
      //println("Starting while loop 2 at i " + i);
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
          println("Found EndPt");
          //endWord = i;
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

  def getEndingBoundary(colonPosition: Int): Int = {
    var startingLevel: Int = colonPosition / 32;
    var pos = colonPosition % 32;
    for (i <- startingLevel until map.length by 1) {
      ////System.out.//println("Pos is " + pos + " for iteration " + i);
      ////System.out.//println("struct CM Bitset: " + map(i).structCMBitset);
      ////System.out.//println("struct R Bitset: " + map(i).structRBitset);
      var commaPos = map(i).structCMBitset.getNextOnPosition(pos);
      var bracketPos = map(i).structRBitset.getNextOnPosition(pos);
      ////System.out.//println("commaPos is " + commaPos);
      ////System.out.//println("bracketPos is " + bracketPos);

      if (commaPos != -1 && bracketPos != -1) {
        ////System.out.//println("Output Code 0");
        val returnVal = if (commaPos < bracketPos) commaPos else bracketPos;
        ////System.out.//println("returnVal is " + returnVal);
        return returnVal + (32 * i);
      } else if (commaPos != -1) {
        ////System.out.//println("Output Code 1");
        return commaPos + (32 * i);
      } else if (bracketPos != -1) {
        ////System.out.//println("Output Code 2");
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
  // methods:off
  createBitmap;
  toString()
}