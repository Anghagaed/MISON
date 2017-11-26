val query = "MQuery.txt";
val file = "MData.txt";
val fff = sc.readFile(file);
val qqq = sc.readFile(query);
val adf = sc.MISONParse(fff, qqq);
import org.apache.spark.sql._
import org.apache.spark.sql.types._

val rADF = adf.map(_.split("  ,  ")).map(a => Row.fromSeq(a));
rADF.collect().foreach(println);

var schemaString = "";
for (q <- qqq) {
        schemaString = schemaString + q + " ";
}
schemaString = schemaString.substring(0, schemaString.length - 1);

val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = true))
val schema = StructType(fields);
val DFadf = spark.createDataFrame(rADF, schema)
DFadf.show()

