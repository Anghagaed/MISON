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

// scalastyle:off println

package Bits

class Bits(val bit: Int) {
  var bits: Int = bit;
  private var SIZEOFINT: Int = 32;
  def <(operand: Bits): Boolean = {
    val leftNeg = this.bits < 0;
    val rightNeg = operand.bits < 0;
    if ((!leftNeg && !rightNeg)
      || (leftNeg && rightNeg)) {
      return bits < operand.bits;
    } else {
      return (rightNeg == true);
    }
  }
  def >(operand: Bits): Boolean = {
    val leftNeg = this.bits < 0;
    val rightNeg = operand.bits < 0;
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
    val rightNeg = operand.bits < 0;
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
    val rightNeg = operand.bits < 0;
    if ((!leftNeg && !rightNeg)
      || (leftNeg && rightNeg)) {
      return bits < operand.bits;
    }
    else {
      return (rightNeg == true);
    }
  }
  def <(operand: Int): Boolean = {
    val bitsRight = new Bits(operand);
    return this < bitsRight;
  }
  def <=(operand: Int): Boolean = {
    val bitsRight = new Bits(operand);
    return this <= bitsRight;
  }
  def >(operand: Int): Boolean = {
    val bitsRight = new Bits(operand);
    return this > bitsRight;
  }
  def >=(operand: Int): Boolean = {
    val bitsRight = new Bits(operand);
    return this >= bitsRight;
  }
  def ==(operand: Int): Boolean = {
    val bitsRight = new Bits(operand);
    return this == bitsRight;
  }
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
    if (bits >= 0) {
      return new Bits(bits >> shiftBy);
    } else {
      return new Bits(this.shiftLeft(shiftBy));
    }
  }
  def set(index: Int, value: Int) {
    bits &= ~(1 << index);
    if (value == 1) {
      bits |= 1 << index;
    }
  }
  def get(index: Int): Int = {
    var mask: Int = 1 << index;
    mask = ((mask & bits) >> index);
    return absolute(mask);
  }
  private def absolute(input: Int): Int = {
    if (input >= 0) {
      return input;
    } else {
      return -input;
    }
  }
  def toBinary(i: Int, digits: Int = 32): String =
    String.format("%" + digits + "s", i.toBinaryString).replace(' ', '0')
  override def toString: String = toBinary(bits);
  def count(): Int = bits.toBinaryString.count(_ == '1');
  def flip(): Unit = {
    bits = ~bits;
  }
  def mirror(): Unit = {
    for (i <- 0 until 16) {
      var temp: Int = get(i);
      set(i, get(31 - i));
      set(31 - i, temp);
    }
  }
  private def shiftLeft(shiftNum: Int): Int = {
    var temp: Int = get(31);
    var bit: Int = this.bits;
    bit = (bit & 0x7FFFFFFF) >> 1;
    bit &= ~(0x40000000);
    bit |= temp << 30;
    return bit;
  }
  def getNextOnPosition(startingPos: Int): Int = {
    if ((bits) == 0 || startingPos > SIZEOFINT) {
      return -1;
    }
    for (i <- startingPos until (SIZEOFINT)) {
      if (this.get(i) == 1) {
        return i;
      }
    }
    return -1;
  }
}

// scalastyle:on println
