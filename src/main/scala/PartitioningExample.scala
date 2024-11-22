import org.apache.spark.{SparkConf, SparkContext}

object PartitioningExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("PartitioningExample")
      .setMaster("local[*]") // Using all available cores for the example

    val sc = new SparkContext(conf)

    // Simulate a large dataset with 1 million numbers
    val data = 1 to 1000000

    // Parallelize the dataset into 4 partitions
    val rdd = sc.parallelize(data, numSlices = 4)

    // Show the number of partitions
    println(s"Initial Partitions: ${rdd.getNumPartitions}")

    // Perform an operation that causes multiple iterations
    // E.g., Compute sum of squares of even numbers
    val result = rdd
      .filter(_ % 2 == 0) // Keep only even numbers
      .map(x => x * x) // Square each number
      .reduce(_ + _) // Sum them up

    println(s"Result (Sum of Squares of Even Numbers): $result")

    // Repartition the RDD to reduce partitions
    val repartitionedRDD = rdd.repartition(2)

    println(s"Partitions After Repartitioning: ${repartitionedRDD.getNumPartitions}")

    // Perform an action after repartitioning
    val repartitionedResult = repartitionedRDD
      .filter(_ % 2 == 0)
      .map(x => x * x)
      .reduce(_ + _)

    println(s"Result After Repartitioning: $repartitionedResult")

    // Perform coalescing to optimize for narrow transformations
    val coalescedRDD = rdd.coalesce(2)
    println(s"Partitions After Coalescing: ${coalescedRDD.getNumPartitions}")

    sc.stop()
  }
}
