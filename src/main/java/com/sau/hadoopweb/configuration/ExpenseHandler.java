package com.sau.hadoopweb.configuration;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.DataTypes;

public class ExpenseHandler {

    public static void main(String[] args) throws Exception {
        // Initialize Spark session
        SparkSession spark = SparkSession.builder()
                .appName("Expense Handler")
                .master("local[*]") // Use local for testing. Replace with your cluster URL in production
                .config("spark.driver.extraJavaOptions", "--add-opens java.base/sun.nio.ch=ALL-UNNAMED")
                .config("spark.executor.extraJavaOptions", "--add-opens java.base/sun.nio.ch=ALL-UNNAMED")
                .config("spark.cassandra.connection.host", "localhost")  // Replace with your Cassandra host
                .getOrCreate();

        // Define the schema of the JSON message sent by the producer
        StructType expenseSchema = new StructType(new StructField[] {
                new StructField("userId", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("dateTime", DataTypes.StringType, true, Metadata.empty()),
                new StructField("description", DataTypes.StringType, true, Metadata.empty()),
                new StructField("type", DataTypes.StringType, true, Metadata.empty()),
                new StructField("count", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("payment", DataTypes.FloatType, true, Metadata.empty())
        });

        // Read data from Kafka topic
        Dataset<Row> rawDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")  // Kafka server URL
                .option("subscribePattern", "expense_*")  // Listen to all topics starting with "expense_"
                .load();

        // Convert Kafka 'value' column to String and filter out null messages
        Dataset<Row> messageDF = rawDF.selectExpr("CAST(value AS STRING) as message")
                .filter("message IS NOT NULL");

        // Parse JSON data into a structured DataFrame
        Dataset<Row> parsedDF = messageDF.select(functions.from_json(functions.col("message"), expenseSchema).alias("expense"));

        // Check if 'expense' column is not null before processing
        Dataset<Row> expenseDataDF = parsedDF.filter("expense.userId IS NOT NULL")
                .select("expense.userId", "expense.dateTime", "expense.description", "expense.type", "expense.count", "expense.payment");

        // Calculate the total expenses for each userId
        Dataset<Row> totalExpensesDF = expenseDataDF.groupBy("userId")
                .agg(functions.sum("payment").alias("total_expenses"));
        totalExpensesDF.printSchema();

        // Write the result to Cassandra
        totalExpensesDF.writeStream()
                .format("org.apache.spark.sql.cassandra")
                .option("checkpointLocation", "/tmp/spark_checkpoint")
                .option("keyspace", "expense")
                .option("table", "user_expenses")
                .outputMode("update")  // Use update mode for streaming aggregation
                .start()
                .awaitTermination();
    }
}