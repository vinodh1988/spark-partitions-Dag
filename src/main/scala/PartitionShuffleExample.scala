import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.TaskContext

object PartitionShuffleExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("PartitionAndTaskExample")
      .setMaster("local[1]")
      .set("spark.driver.host", "localhost")
    val sc = new SparkContext(conf)

    // Input data
    val data = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // Parallelize the data into 2 partitions
    val rdd = sc.parallelize(data, numSlices = 2)

    // Map operation with prints
    val processedRdd = rdd.map(x => {
      val partitionId = TaskContext.getPartitionId()
      val taskAttemptId = TaskContext.get.taskAttemptId()
      println(s"Processing $x in Partition $partitionId by Task $taskAttemptId")
      (x, x * x)
    })

    // Collect the results
    val result = processedRdd.collect()

    println("\nProcessed Output:")
    result.foreach { case (key, value) =>
      println(s"Key: $key, Value: $value")
    }


    Thread.currentThread().join()
    sc.stop()
  }

}
