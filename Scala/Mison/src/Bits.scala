package Bits

/*
 * Mutable Container that emulates unsigned int for supports with bitwise operation
 */

class Bits(val bit: Int) {
  var bits: Int = bit;
  private var SIZEOFINT: Int = 32;
  // boolean operators
  def <(operand: Bits): Boolean = {
    val leftNeg = this.bits < 0;
    val rightNeg = this.bits < 0;
    if ((!leftNeg && !rightNeg)
      || (leftNeg && rightNeg)) {
      return bits < operand.bits;
    } else {
      return (rightNeg == true);
    }
  }
  def >(operand: Bits): Boolean = {
    val leftNeg = this.bits < 0;
    val rightNeg = this.bits < 0;
    if ((!leftNeg && !rightNeg)
      || (leftNeg && rightNeg)) {
      return bits > operand.bits;
    } else {
      return (leftNeg == true);
    }
  }
  def ==(operand: Bits): Boolean = {
    bits == operand.bits;
  }
  def >=(operand: Bits): Boolean = {
    if (bits == operand.bits) {
      return true;
    }
    val leftNeg = this.bits < 0;
    val rightNeg = this.bits < 0;
    if ((!leftNeg && !rightNeg)
      || (leftNeg && rightNeg)) {
      return bits > operand.bits;
    }
    else {
      return (leftNeg == true);
    }
  }
  def <=(operand: Bits): Boolean = {
    if (bits == operand.bits) {
      return true;
    }
    val leftNeg = this.bits < 0;
    val rightNeg = this.bits < 0;
    if ((!leftNeg && !rightNeg)
      || (leftNeg && rightNeg)) {
      return bits < operand.bits;
    }
    else {
      return (rightNeg == true);
    }
  }

  // other operators
  def +(operand: Bits): Bits = {
    new Bits(bits + operand.bits);
  }
  def -(operand: Bits): Bits = {
    new Bits(bits - operand.bits);
  }
  def *(operand: Bits): Bits = {
    new Bits(bits * operand.bits);
  }
  def /(operand: Bits): Bits = {
    new Bits(bits / operand.bits);
  }
  def |(operand: Bits): Bits = {
    new Bits(bits | operand.bits);
  }
  def &(operand: Bits): Bits = {
    new Bits(bits & operand.bits);
  }
  def ^(operand: Bits): Bits = {
    new Bits(bits ^ operand.bits);
  }
  def ==(operand: Int): Boolean = {
    bits == operand;
  }
  def +(operand: Int): Bits = {
    new Bits(bits + operand);
  }
  def -(operand: Int): Bits = {
    new Bits(bits - operand);
  }
  def *(operand: Int): Bits = {
    new Bits(bits * operand);
  }
  def /(operand: Int): Bits = {
    new Bits(bits / operand);
  }
  def |(operand: Int): Bits = {
    new Bits(bits | operand);
  }
  def &(operand: Int): Bits = {
    new Bits(bits & operand);
  }
  def ^(operand: Int): Bits = {
    new Bits(bits ^ operand);
  }
  def <<(shiftBy: Int): Bits = {
    new Bits(bits << shiftBy);
  }
  def >>(shiftBy: Int): Bits = {
    if (bits >= 0) {
      return new Bits(bits >> shiftBy);
    } else {
      return new Bits(this.shiftLeft(shiftBy));
    }
  }

  // (bitwise) setter
  def set(index: Int, value: Int) {
    bits &= ~(1 << index);
    if (value == 1)
      bits |= 1 << index;
  }

  // (bitwise) getter
  def get(index: Int): Int = {
    var mask: Int = 1 << index;
    mask = ((mask & bits) >> index);
    return absolute(mask);
  }
  // return absolute value
  private def absolute(input: Int): Int = {
    if (input >= 0) {
      return input;
    } else {
      return -input;
    }
  }
  // toString
  def toBinary(i: Int, digits: Int = 32) = //Credit: https://stackoverflow.com/questions/9442381/formatting-binary-values-in-scala
    String.format("%" + digits + "s", i.toBinaryString).replace(' ', '0')
  override def toString: String = toBinary(bits);

  // Etc...
  def count(): Int = bits.toBinaryString.count(_ == '1');
  def flip(): Unit = {
    bits = ~bits;
  }
  // Assume int is 32 bit.
  def mirror(): Unit = {
    for (i <- 0 until 16) {
      var temp: Int = get(i);
      set(i, get(31 - i));
      set(31 - i, temp);
    }
  }

  // shiftLeft function for negative scenerio

  private def shiftLeft(shiftNum: Int): Int = {
    var temp: Int = get(31);
    var bit: Int = this.bits;
    bit = (bit & 0x7FFFFFFF) >> 1;
    bit &= ~(0x40000000);
    bit |= temp << 30;
    return bit;
  }

  // get position of next bit that is a one starting from startingPos right to left.
  // return the position or -1 if failed
  def getNextOnPosition(startingPos: Int): Int = {
    if ((bits) == 0 || startingPos > SIZEOFINT) {
      //System.out.println("0 or pos too big");
      return -1;
    }
    for (i <- startingPos until (SIZEOFINT)) {
      //System.out.println("i is " + i + " with this.get(i) is " + this.get(i));
      if (this.get(i) == 1) {
        return i;
      }
    }
    //System.out.println("Found Nothing");
    return -1;
  }
}