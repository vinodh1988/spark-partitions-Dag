import org.apache.spark.{SparkConf, SparkContext}

object PartitionSpillExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("PartitionSpillExample")
      .setMaster("local[*]")
      .set("spark.executor.memory", "512m") // Limit executor memory
      .set("spark.memory.fraction", "0.2") // Further restrict memory for processing

    val sc = new SparkContext(conf)

    // Generate a large dataset with a single partition
    val largeData = sc.parallelize(1 to 10000000, numSlices = 1) // Single partition

    println(s"Initial Partitions: ${largeData.getNumPartitions}")

    // Perform a transformation that requires large intermediate data
    val groupedData = largeData.map(x => (x % 100, x)).groupByKey()

    println("Starting groupByKey...")

    // Trigger an action and observe spilling
    groupedData.foreachPartition { partition =>
      println(s"Processing partition...")
      var count = 0
      partition.foreach { case (key, values) =>
        count += 1
        println(s"Key: $key, Values: ${values.size}")
      }
      println(s"Partition processing completed with $count keys.")
    }

    sc.stop()
  }
}
