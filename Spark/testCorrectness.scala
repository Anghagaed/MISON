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

val DF2 = sqlContext.read.json("Example/tweet2.json");

DF2.createOrReplaceTempView("testDifference");

val SQLString = "SELECT " + sqlQuery.substring(0, sqlQuery.length - 2) + " FROM testDifference";

println(SQLString);

//val DF3 = sqlContext.sql("SELECT created_at, favorite_count ,id_str, retweet_count, user.created_at FROM testDifference");

val DF3 = sqlContext.sql(SQLString);

DFadf.show()
DF3.show();
DFadf.except(DF3).show();
