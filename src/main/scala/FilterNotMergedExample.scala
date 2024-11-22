import org.apache.spark.{SparkConf, SparkContext}

object FilterNotMergedExample {
  def main(args: Array[String]): Unit = {
    // Spark Configuration and Context
    val conf = new SparkConf()
      .setAppName("FilterNotMergedExample")
      .setMaster("local[2]")
      .set("spark.driver.host", "localhost") // Explicitly set the host as localhost

    val sc = new SparkContext(conf)

    // Input data
    val data = sc.parallelize(Seq(("a", 1), ("b", 2), ("a", 3), ("b", 4)), 2)

    // Shuffle operation: reduceByKey
    val reduced = data.reduceByKey(_ + _)

    // Filter after the shuffle
    val filtered = reduced.filter { case (key, value) => value > 3 }

    // Collect and print the result
    val result = filtered.collect()
    println("Filtered Result: " + result.mkString(", "))

    // Hold the Spark UI
    println("Application is running. Press Enter to exit.")
    scala.io.StdIn.readLine()

    // Stop SparkContext
    sc.stop()
  }
}
