import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.TaskContext

object WordCountWithTaskInfo {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("WordCountWithTaskInfo")
      .setMaster("local[2]")
      .set("spark.driver.host", "localhost")
    val sc = new SparkContext(conf)

    // Sample text data
    val textData = Seq(
      "Hello world",
      "This is an example",
      "Apache Spark is great",
      "Spark is fast and scalable",
      "Hello Hello where are you going",
      "Scalable things are not good , once upon a time there was good thing",
      "fast is fast, slow is slow"
    )

    // Parallelize the text data with 2 partitions
    val textRDD = sc.parallelize(textData, numSlices = 2)

    // FlatMap to split lines into words
    val wordsRDD = textRDD.flatMap(line => {
      val partitionId = TaskContext.getPartitionId()
      val taskAttemptId = TaskContext.get.taskAttemptId()
      println(s"Processing line: '$line' in Partition $partitionId by Task $taskAttemptId")
      line.split(" ")
    })

    // Map and Reduce for word count
    val wordCounts = wordsRDD
      .map(word => {
        val partitionId = TaskContext.getPartitionId()
        val taskAttemptId = TaskContext.get.taskAttemptId()
        println(s"Processing word: '$word' in Partition $partitionId by Task $taskAttemptId")
        (word, 1)
      })
      .reduceByKey(_ + _)

    // Collect and print the results
    val result = wordCounts.collect()

    println("\nWord Counts:")
    result.foreach { case (word, count) =>
      println(s"$word: $count")
    }

    Thread.currentThread().join()

    sc.stop()
  }
}
