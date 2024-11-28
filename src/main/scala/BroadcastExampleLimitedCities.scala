import org.apache.spark.sql.SparkSession

object BroadcastExampleLimitedCities {
  def main(args: Array[String]): Unit = {
    // Initialize SparkSession
    val spark = SparkSession.builder()
      .appName("Broadcast Example with 5 Cities and 30 Users")
      .master("local[*]") // Use local mode for testing
      .getOrCreate()

    // Static data to be broadcasted: City-to-Country mapping (5 cities)
    val cityToCountry = Map(
      "New York" -> "USA",
      "London" -> "UK",
      "Paris" -> "France",
      "Mumbai" -> "India",
      "Sydney" -> "Australia"
    )

    // Broadcast the static data
    val broadcastCityToCountry = spark.sparkContext.broadcast(cityToCountry)

    // Dynamic data: Users dataset with 30 entries
    import spark.implicits._
    val users = Seq(
      (1, "Alice", "New York"),
      (2, "Bob", "London"),
      (3, "Charlie", "Paris"),
      (4, "Dave", "Mumbai"),
      (5, "Eve", "Sydney"),
      (6, "Frank", "New York"),
      (7, "Grace", "London"),
      (8, "Hank", "Paris"),
      (9, "Ivy", "Mumbai"),
      (10, "Jack", "Sydney"),
      (11, "Karen", "New York"),
      (12, "Leo", "London"),
      (13, "Mona", "Paris"),
      (14, "Nick", "Mumbai"),
      (15, "Oscar", "Sydney"),
      (16, "Pam", "New York"),
      (17, "Quinn", "London"),
      (18, "Rose", "Paris"),
      (19, "Sam", "Mumbai"),
      (20, "Tina", "Sydney"),
      (21, "Uma", "New York"),
      (22, "Victor", "London"),
      (23, "Wendy", "Paris"),
      (24, "Xavier", "Mumbai"),
      (25, "Yara", "Sydney"),
      (26, "Zara", "New York"),
      (27, "Adam", "London"),
      (28, "Bella", "Paris"),
      (29, "Chris", "Mumbai"),
      (30, "Diana", "Sydney")
    ).toDF("id", "name", "city")

    println("Original Users Dataset:")
    users.show()

    // Enrich users dataset with country information using the broadcasted variable
    val enrichedUsers = users.map(row => {
      val id = row.getInt(0)
      val name = row.getString(1)
      val city = row.getString(2)
      val country = broadcastCityToCountry.value.getOrElse(city, "Unknown")
      (id, name, city, country)
    }).toDF("id", "name", "city", "country")

    println("Enriched Users Dataset:")
    enrichedUsers.show()

    System.in.read()
    // Stop SparkSession
    spark.stop()
  }
}
