import org.apache.spark.{SparkConf, SparkContext}

object GroupByExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GroupByExample").setMaster("local[2]")
    val sc = new SparkContext(conf)

    // Sample data with keys and values
    val data = Seq(
      ("a", 1), ("b", 2), ("a", 3),
      ("c", 4), ("b", 5), ("a", 6),
      ("c", 7), ("b", 8)
    )

    // Parallelize the data into 2 partitions
    val rdd = sc.parallelize(data, 2)

    // Perform groupByKey (causes shuffle)
    val groupedRdd = rdd.groupByKey()

    // Collect and print the grouped data
    val result = groupedRdd.collect()

    println("Grouped Data:")
    result.foreach { case (key, values) =>
      println(s"Key: $key, Values: ${values.mkString(", ")}")
    }

    sc.stop()
  }
}
