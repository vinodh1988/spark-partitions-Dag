import org.apache.spark.sql.SparkSession

object PartitionExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Partition Example")
      .master("local[2]") // 2 cores
      .getOrCreate()

    val sc = spark.sparkContext

    // Create an RDD with 4 partitions
    val rdd = sc.parallelize(1 to 8, 4)

    // Print which partition is being processed
    val result = rdd.mapPartitionsWithIndex((index, iterator) => {
      println(s"Processing partition: $index")
      iterator.map(x => (index, x)) // Return partition index along with the data
    })

    // Trigger the action to process partitions
    result.collect().foreach(println)

    spark.stop()
  }
}
