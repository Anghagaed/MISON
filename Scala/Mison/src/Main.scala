import fileHandler.fileHandler

object Main {
  def main(args: Array[String])
  {
    println("Begin Testing")
    val fHandler = new fileHandler("./Test Files/jsonTest4.txt");
    fHandler.read
    fHandler.split
    for(a <- 0 until fHandler.vector.size){
      println(fHandler.vector(a))
    }
    println("End Testing")
  }
}