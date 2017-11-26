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
package fileHandler

import java.io.IOException

import scala.collection.mutable.ArrayBuffer
import scala.io.Source._

// scalastyle:off println

class fileHandler() {
  private var text: String = "";
  private var arr = ArrayBuffer.empty[String];
  private var filePath: String = "";
  private var it: Option[Iterator[String]] = None;
  def setNewFilePath(newPath: String): Unit = {
    filePath = newPath;
    it = Some(scala.io.Source.fromFile(filePath).getLines)
  }
  def getNext: Boolean = {
    if (it.get.hasNext) {
      text = it.get.next()
      split
      return true;
    }
    return false;
  }
  def getFileArray: ArrayBuffer[String] = {
    return arr;
  }
  def getLineString: String = {
    return text;
  }
  private def split: Boolean = {
    arr = ArrayBuffer.empty[String];
    for (a <- 0 until text.length() if a % 32 == 0) {
      if (a + 32 <= text.length()) {
        arr = arr :+ (text.substring(a, a + 32))
      } else {
        arr = arr :+ (text.substring(a, text.length()))
      }
    }
    return true;
  }
}

// scalastyle:on println
