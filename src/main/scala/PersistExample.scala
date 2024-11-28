import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import scala.util.Random

object PersistExample {
  def main(args: Array[String]): Unit = {
    // Step 1: Create a SparkSession
    val spark = SparkSession.builder()
      .appName("Persistence Example - Local Mode")
      .master("local[*]") // Run locally using all available cores

       .getOrCreate()

    val sc = spark.sparkContext

    // Set log level to DEBUG for detailed output
    sc.setLogLevel("DEBUG")

    // Step 2: Generate a large dataset of random numbers
    val largeDataset = sc.parallelize(1 to 1000000, 10) // 1 million numbers, 10 partitions
      .map(_ => Random.nextInt(100)) // Random integers between 0 and 99

    // Step 3: Perform transformations
    val filteredData = largeDataset.filter(_ % 2 == 0) // Filter even numbers

    // Step 4: Persist the result
    filteredData.persist(StorageLevel.MEMORY_AND_DISK) // Explicit persistence level

    // Method 1: Check if the RDD is marked for persistence (before action)
    println(s"Storage Level (before action): ${filteredData.getStorageLevel}")
    val isPersistedBeforeAction = sc.getPersistentRDDs.values.exists(_.id == filteredData.id)
    println(s"Is the RDD persisted before action? $isPersistedBeforeAction")

    // Step 5: Trigger an action to materialize the persistence
    println(s"Count of even numbers: ${filteredData.count()}")

    // Method 2: Check if the RDD is persisted (after action)
    println(s"Storage Level (after action): ${filteredData.getStorageLevel}")
    val isPersistedAfterAction = sc.getPersistentRDDs.values.exists(_.id == filteredData.id)
    println(s"Is the RDD persisted after action? $isPersistedAfterAction")

    // Step 6: Check Spark logs for persistence details
    println(s"Sum of even numbers: ${filteredData.sum()}")

    // Step 7: Perform another action to verify persistence
    println(s"Distinct even numbers: ${filteredData.distinct().collect().mkString(", ")}")

    // Step 8: Stop the SparkSession

    System.in.read()

    spark.stop()
  }
}
