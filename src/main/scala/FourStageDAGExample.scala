import org.apache.spark.{SparkConf, SparkContext}

object FourStageDAGExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("RDD DAG Example")
      .setMaster("local[*]")
      .set("spark.driver.host","localhost")// Use all available cores
    val sc = new SparkContext(conf)

    // Stage 1: Load data
    val lines = sc.parallelize(Seq(
      "hello spark hello scala",
      "big data spark rdd example",
      "scala functional programming spark"
    ), numSlices = 2) // Parallelize the data into 2 partitions

    // Stage 2: FlatMap - Split lines into words
    val words = lines.flatMap(line => line.split(" "))

    // Stage 3: Map - Create a pair RDD for word counts
    val wordPairs = words.map(word => (word, 1))

    // Stage 4: ReduceByKey - Aggregate counts for each word
    val wordCounts = wordPairs.reduceByKey(_ + _)

    // Stage 5: Filter - Keep only words with a count > 1
    val filteredCounts = wordCounts.filter { case (_, count) => count > 1 }

    // Stage 6: Sort - Sort the RDD by count in descending order
    val sortedCounts = filteredCounts.sortBy { case (_, count) => count }

    // Collect and print the results
    println("Word counts with more than one occurrence:")
    sortedCounts.collect().foreach(println)

    // Keep the application alive for Spark UI inspection
    println("Press Ctrl+C to exit the application.")
    Thread.currentThread().join()

    sc.stop()
  }
}
