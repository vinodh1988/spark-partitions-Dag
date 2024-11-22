import org.apache.spark.{SparkConf, SparkContext}
import scala.util.Random

object LargeDataProcessing {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("LargeDataProcessing")
      .setMaster("local[*]")
      .set("spark.executor.memory", "512m") // Simulate limited memory
      .set("spark.storage.memoryFraction", "0.1") // Reduce storage memory fraction for testing spills

    val sc = new SparkContext(conf)

    // Generate a very large dataset with random numbers
    val largeData = sc.parallelize(1 to 100000000, numSlices = 10)
      .map(_ => Random.nextInt(1000)) // Large random numbers

    // Monitor partitions before processing
    println(s"Initial Partitions: ${largeData.getNumPartitions}")

    // Perform a transformation that forces shuffling and spilling
    val groupedData = largeData.groupBy(_ % 100) // Group data by modulo 100

    println("Processing grouped data...")

    // Trigger an action to force computation
    val result = groupedData.mapValues(_.size).collect()

    // Print the results (partial results to avoid overwhelming output)
    result.take(10).foreach { case (key, count) =>
      println(s"Key: $key, Count: $count")
    }

    sc.stop()
  }
}
