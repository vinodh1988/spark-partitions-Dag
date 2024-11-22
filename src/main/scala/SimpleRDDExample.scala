import org.apache.spark.{SparkConf, SparkContext}

object SimpleRDDExample {
  def main(args: Array[String]): Unit = {
    // Step 1: Set up Spark Configuration and Context
    val conf = new SparkConf()
      .setAppName("Simple RDD Example")
      .setMaster("local[*]") // Run in local mode

    val sc = new SparkContext(conf)

    try {
      // Step 2: Create an RDD from a list
      val numbers = Seq(1, 2, 3, 4, 5)
      val numbersRDD = sc.parallelize(numbers) // Create RDD from the list

      // Step 3: Transform the RDD (double each number)
      val doubledRDD = numbersRDD.map(_ * 2)

      // Step 4: Collect and Print the Results
      val result = doubledRDD.collect() // Collect results to driver
      println(result.mkString(", "))    // Print: 2, 4, 6, 8, 10
    } finally {
      // Step 5: Stop the SparkContext
      sc.stop()
    }
  }
}
