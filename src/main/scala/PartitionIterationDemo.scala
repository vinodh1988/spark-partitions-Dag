import org.apache.spark.{SparkConf, SparkContext}
import scala.util.Random

object PartitionIterationDemo {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("PartitionIterationDemo")
      .setMaster("local[*]")
      .set("spark.executor.memory", "512m") // Simulating limited memory
      .set("spark.memory.fraction", "0.2") // Restrict memory available for storage
      .set("spark.memory.storageFraction", "0.1")

    val sc = new SparkContext(conf)

    // Generate a large dataset with a single partition
    val largeData = sc.parallelize(1 to 100000000, numSlices = 1) // Single partition

    println(s"Initial Partitions: ${largeData.getNumPartitions}")

    // Perform a transformation that forces iteration within a partition
    val largeGroupedData = largeData
      .map(x => (x % 10, x)) // Create key-value pairs
      .groupByKey() // Group by key (forces shuffle and large partition processing)

    println("Starting processing...")

    // Action to trigger computation and show how partitions are processed iteratively
    largeGroupedData.foreachPartition { partition =>
      println(s"Processing partition of size: ${partition.size}")
      var iterationCount = 0

      // Simulate processing in chunks (forcing Spark to spill and iterate)
      partition.foreach { case (key, values) =>
        iterationCount += 1
        println(s"Processing key: $key with ${values.size} values in iteration $iterationCount")
      }
    }

    sc.stop()
  }
}
