import org.apache.spark.{SparkConf, SparkContext}

object WordCountInMemory {
  def main(args: Array[String]): Unit = {
    // Step 1: Set up Spark Configuration and Context
    val conf = new SparkConf()
      .setAppName("Word Count In Memory")
      .setMaster("local[*]") // Run in local mode

    val sc = new SparkContext(conf)

    try {
      // Step 2: Create an RDD from an in-memory collection
      val data = Seq(
        "hello world",
        "hello spark",
        "spark is powerful",
        "spark spark spark"
      )
      val inputRDD = sc.parallelize(data) // Parallelize the collection

      // Step 3: Perform word count
      val wordCounts = inputRDD
        .flatMap(line => line.split("\\s+")) // Split each line into words
        .map(word => (word, 1))             // Map each word to a key-value pair (word, 1)
        .reduceByKey(_ + _)                 // Reduce by key to count occurrences of each word

      // Step 4: Print results to the console
      wordCounts.collect().foreach { case (word, count) =>
        println(s"$word: $count")
      }
    } finally {
      // Step 5: Stop the SparkContext
      sc.stop()
    }
  }
}
