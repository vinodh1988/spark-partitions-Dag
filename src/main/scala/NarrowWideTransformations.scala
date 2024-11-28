import org.apache.spark.{SparkConf, SparkContext}

object NarrowWideTransformations {
  def main(args: Array[String]): Unit = {
    // Configure Spark
    val conf = new SparkConf().setAppName("Narrow vs Wide Transformations using Take").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // Create an RDD of numbers from 1 to 1000
    val numbers = sc.parallelize(1 to 1000)

    // Apply narrow transformations
    val mappedNumbers = numbers.map(_ * 2) // Each number is doubled
    val filteredNumbers = mappedNumbers.filter(_ % 3 == 0) // Keep numbers divisible by 3

    // Apply a wide transformation (groupByKey or reduceByKey)
    val keyValuePairs = filteredNumbers.map(num => (num % 10, num)) // Create key-value pairs
    val groupedByKey = keyValuePairs.groupByKey() // Wide transformation

    // Use take to print the results
    val results = groupedByKey.take(10) // Get the first 10 key-value pairs
    results.foreach { case (key, values) =>
      println(s"Key: $key, Values: ${values.mkString(", ")}")
    }

    // Stop the SparkContext
    Thread.currentThread().join()
    sc.stop()
  }
}
