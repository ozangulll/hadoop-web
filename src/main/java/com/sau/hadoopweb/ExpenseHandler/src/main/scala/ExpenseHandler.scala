package com.sau.hadoopweb.ExpenseHandler.src.main.scala

import org.apache.spark.sql.{SparkSession, DataFrame, functions => F}
import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.cassandra._
import com.datastax.spark.connector._

object ExpenseHandler {

  case class Expense(userId: Int, dateTime: String, description: String, typeExpense: String, count: Int, payment: Double)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Expense Handler")
      .config("spark.cassandra.connection.host", "localhost")
      .getOrCreate()

    import spark.implicits._

    val inputDF = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribePattern", "expense_*")
      .load()

    val rawDF = inputDF.selectExpr("CAST(value AS STRING)").as[String]
    
    val expandedDF = rawDF.map(row => row.split(","))
      .map(row => Expense(
        row(0).toInt,
        row(1),
        row(2),
        row(3),
        row(4).toInt,
        row(5).toDouble
      ))

    val validExpensesDF = expandedDF.filter(expense => expense.payment > 0)

    val totalExpensesDF = validExpensesDF
      .groupBy("userId")
      .agg(F.sum("payment").alias("total_expenses"))

    val finalDF = totalExpensesDF.select("userId", "total_expenses")

    val query = finalDF.writeStream
      .trigger(Trigger.ProcessingTime("5 seconds"))
      .foreachBatch { (batchDF: DataFrame, batchID: Long) =>
        println(s"Writing to Cassandra $batchID")
        batchDF.write
          .cassandraFormat("user_expenses", "expense")
          .mode("append")
          .save()
      }
      .outputMode("update")
      .start()

    query.awaitTermination()
  }
}

