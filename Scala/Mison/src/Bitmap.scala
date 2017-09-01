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
  override def toString: String = s"$bits"
}