import org.apache.spark.{SparkConf, SparkContext}
import scala.util.Random

object IterativeProcessingExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("IterativeProcessingExample")
      .setMaster("local[*]") // Use all available cores for demonstration
      .set("spark.executor.memory", "512m") // Simulating limited memory
      .set("spark.memory.fraction", "0.2") // Limit memory for storage
      .set("spark.memory.storageFraction", "0.1") // Simulating constraints

    val sc = new SparkContext(conf)

    // Generate a large dataset
    val largeData = sc.parallelize(1 to 10000000, numSlices = 1) // Force single partition

    println(s"Initial Partitions: ${largeData.getNumPartitions}")

    // Simulate transformation to force large intermediate data
    val groupedData = largeData.map(x => (x % 100, x)) // Key-value pairs

    // Use mapPartitionsWithIndex to simulate iterative processing
    val partitionedData = groupedData.mapPartitionsWithIndex { (index, iter) =>
      println(s"Processing Partition $index...")

      // Simulate chunk-based processing within the partition
      iter.grouped(100000).flatMap { chunk =>
        println(s"Partition $index processing chunk of size ${chunk.size}")
        chunk.map { case (key, value) => (key, value * 2) } // Dummy transformation
      }
    }

    // Trigger an action to execute the transformations
    val result = partitionedData.count()

    println(s"Final Result Count: $result")

    sc.stop()
  }
}
