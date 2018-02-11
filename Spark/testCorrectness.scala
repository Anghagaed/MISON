val query = "test/MQuery.txt";
val file = "test/MData.txt";
val fff = sc.readFile(file);
val qqq = sc.readFile(query);

val adf = sc.MISONParse(fff, qqq);
import org.apache.spark.sql._
import org.apache.spark.sql.types._

val rADF = adf.map(_.split("  ,  ")).map(a => Row.fromSeq(a));

var schemaString = "";
var sqlQuery = "";
for (q <- qqq) {
        schemaString = schemaString + q + " ";
        sqlQuery = sqlQuery + q + ", ";
}
schemaString = schemaString.substring(0, schemaString.length - 1);

val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = true))
val schema = StructType(fields);
val DFadf = spark.createDataFrame(rADF, schema)

val sqlContext = new org.apache.spark.sql.SQLContext(sc)

val DF2 = sqlContext.read.json("test/tweet_10.json");

// Non relational query
val DF3 = DF2.select("quoted_status.source","quoted_status.contributors");
DFadf.except(DF3).show();