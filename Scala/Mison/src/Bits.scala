package Bits

class Bits(val bit: Int) {
  var bits: Int = bit;
  private var SIZEOFINT: Int = 32;
  // boolean operators
  def <(operand: Bits): Boolean = bits < operand.bits;
  def >(operand: Bits): Boolean = bits > operand.bits;
  def ==(operand: Bits): Boolean = bits == operand.bits;

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
    new Bits(bits >> shiftBy);
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
    mask &= bits;
    return mask >> index;
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

  // get position of next bit that is a one starting from startingPos right to left.
  // return the position or -1 if failed
  def getNextOnPosition(startingPos: Int): Int = {
    if (bits == 0 || startingPos >= SIZEOFINT) {
      return -1;
    }
    for (i <- startingPos until SIZEOFINT) {
      if (this.get(i) == 1) {
        return i;
      }
    }
    return -1;
  }
}