package com.sau.hadoopweb.service;

import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.model.Expense;
import com.sau.hadoopweb.producer.KafkaProducer;
import com.sau.hadoopweb.repository.EmployeeRepository;
import com.sau.hadoopweb.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

@Service
public class ExpenseService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    private static final String[] descriptions = {"Macaroni", "Jacket", "Car", "Laptop", "Book"};
    private static final String[] types = {"food", "clothes", "vehicle", "electronics", "education"};

    public void generateExpenses() throws InterruptedException {
        List<Employee> employees = employeeRepository.findAll();
        Random rand = new Random();

        for (Employee employee : employees) {
            String dateTime = generateRandomDateTime();

            // Randomly select an index and ensure description and type match
            int index = rand.nextInt(descriptions.length);
            String description = descriptions[index];
            String type = types[index];  // Corresponding type

            int count = rand.nextInt(5) + 1;
            float payment = rand.nextFloat() * 1000;
            payment = Math.round(payment * 100.0) / 100.0f;

            Expense existingExpense = expenseRepository.findByEmployee(employee);
            Expense expense;

            if (existingExpense != null) {
                existingExpense.setDateTime(dateTime);
                existingExpense.setDescription(description);
                existingExpense.setType(type);
                existingExpense.setCount(count);
                existingExpense.setPayment(payment);
                expense = existingExpense;
            } else {
                expense = new Expense();
                expense.setEmployee(employee);  // Set Employee reference
                expense.setDateTime(dateTime);
                expense.setDescription(description);
                expense.setType(type);
                expense.setCount(count);
                expense.setPayment(payment);
            }

            // Save the expense (either new or updated)
            expenseRepository.save(expense);
            Thread.sleep(1000);
            // Send the expense to Kafka
            String topic = "expense_" + employee.getId();  // Each user has a dedicated Kafka topic
            String message = expenseToMessage(expense);
            kafkaProducer.sendMessage(topic, message);
        }
    }

    private String generateRandomDateTime() {
        long currentTime = System.currentTimeMillis();
        long randomTime = currentTime - (long) (Math.random() * 1000000000L); // Random time in the past
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new java.util.Date(randomTime));
    }

    private String expenseToMessage(Expense expense) {
        return String.format("%d,%s,%s,%s,%d,%.2f",
                expense.getEmployee().getId(),  // userId
                expense.getDateTime(),           // dateTime
                expense.getDescription(),       // description
                expense.getType(),              // typeExpense
                expense.getCount(),             // count
                expense.getPayment());          // payment
    }
}
/*
import org.apache.spark.sql.{SparkSession, DataFrame, functions => F}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.cassandra._
import com.datastax.spark.connector._
import com.datastax.oss.driver.api.core.uuid.Uuids

import java.io._

object ExpenseHandler {
  case class Expense(userid: Int, dateTime: String, description: String, typeExpense: String, count: Int, payment: Double)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Expense Handler")
      .config("spark.cassandra.connection.host", "localhost")
      .getOrCreate()

    import spark.implicits._

    // Kafka stream to read data
    val inputDF = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribePattern", "expense_*")
      .load()

    // Converting Kafka data to a string format
    val rawDF = inputDF.selectExpr("CAST(value AS STRING)").as[String]

    // Parsing the raw data into Expense case class
    val expandedDF = rawDF.flatMap(row => {
      val fields = row.split(",")
      if (fields.length == 6) {
        try {
          Some(Expense(
            fields(0).toInt,
            fields(1),
            fields(2),
            fields(3),
            fields(4).toInt,
            fields(5).toDouble
          ))
        } catch {
          case e: Exception =>
            println(s"Skipping invalid row: $row, error: ${e.getMessage}")
            None
        }
      } else {
        println(s"Skipping invalid row: $row, wrong number of fields.")
        None
      }
    })

    // Aggregating total expenses by userId
    val totalExpensesDF = expandedDF
      .groupBy("userid")  // Use 'userid' instead of 'userId'
      .agg(F.sum("payment").alias("total_expenses"))

    // UDF to generate UUID
    val makeUUID = F.udf(() => Uuids.timeBased().toString)

    // Adding UUID column and renaming 'total_expenses' to 'total_amount'
    val finalDF = totalExpensesDF
      .withColumn("uuid", makeUUID())  // Adding UUID column

    // Write the streaming data to Cassandra
    val query = finalDF.writeStream
      .trigger(Trigger.ProcessingTime("5 seconds"))
      .foreachBatch { (batchDF: DataFrame, batchID: Long) =>
        println(s"Writing batch $batchID to Cassandra...")

        // Write to a TXT file for debugging
        batchDF
          .collect()  // Collect the batch as an array
          .foreach(row => {
            val writer = new PrintWriter(new FileWriter("debug_output.txt", true))
            writer.write(row.mkString(", ") + "\n")  // Write each row to the file
            writer.close()
          })

        batchDF.show()  // Show data being processed in the batch
        batchDF.printSchema()  // Show schema of the batch data

        // Write to Cassandra
        batchDF.write
          .cassandraFormat("user_expenses", "expense")  // Write to the Cassandra table
          .mode("append")
          .save()
      }
      .outputMode("update")
      .start()

    query.awaitTermination()
  }
}
spark-submit --class ExpenseHandler --master local[*]   --packages "org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.3,com.datastax.spark:spark-cassandra-connector_2.12:3.5.0,com.datastax.cassandra:cassandra-driver-core:4.0.0"   target/scala-2.12/expense-handler_2.12-1.0.jar


 */
