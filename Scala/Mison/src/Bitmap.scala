package Bitmap {
  class Bitmap;
}

class Bitmap(val bit : Int) {
  var bits : Int = bit;
  def +(operand: Bitmap): Bitmap = {
    new Bitmap(bits + operand.bits);
  }
  def -(operand: Bitmap): Bitmap = {
    new Bitmap(bits - operand.bits);
  }
  def *(operand: Bitmap): Bitmap = {
    new Bitmap(bits * operand.bits);
  }
  def /(operand: Bitmap): Bitmap = {
    new Bitmap(bits / operand.bits);
  }
  def |(operand: Bitmap): Bitmap = {
    new Bitmap(bits | operand.bits);
  }
  def &(operand: Bitmap): Bitmap = {
    new Bitmap(bits & operand.bits);
  }
  def ^(operand: Bitmap): Bitmap = {
    new Bitmap(bits ^ operand.bits);
  }
  def <<(shiftBy: Int): Bitmap = {
    new Bitmap(bits << shiftBy);
  }
  def >>(shiftBy: Int): Bitmap = {
    new Bitmap(bits >> shiftBy);
  }
  def toBinary(i: Int, digits: Int = 32) = //Credit: https://stackoverflow.com/questions/9442381/formatting-binary-values-in-scala
    String.format("%" + digits + "s", i.toBinaryString).replace(' ', '0')
  override def toString: String = toBinary(bits);
}