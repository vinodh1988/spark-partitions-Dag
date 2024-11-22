import org.apache.spark.{SparkConf, SparkContext}
import scala.util.Random

object SparkAutoIterationExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("SparkAutoIterationExample")
      .setMaster("local[*]") // Use all available cores
      .set("spark.executor.memory", "512m") // Simulate limited executor memory
      .set("spark.memory.fraction", "0.2") // Restrict memory for processing
      .set("spark.memory.storageFraction", "0.1")

    val sc = new SparkContext(conf)

    // Generate a large dataset with random keys
    val largeData = sc.parallelize(1 to 50000000, numSlices = 4) // Split into 4 partitions
      .map(x => (Random.nextInt(1000), x)) // Random keys to force shuffling

    println(s"Initial Partitions: ${largeData.getNumPartitions}")

    // Apply groupByKey (a wide transformation)
    val groupedData = largeData.groupByKey()

    println("Starting groupByKey transformation...")

    // Trigger an action to force computation and observe iterative processing
    val result = groupedData.mapValues(_.size).collect()

    // Display a subset of the result
    result.take(10).foreach { case (key, count) =>
      println(s"Key: $key, Count: $count")
    }

    sc.stop()
  }
}
