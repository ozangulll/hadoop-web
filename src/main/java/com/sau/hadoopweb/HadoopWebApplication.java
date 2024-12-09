package com.sau.hadoopweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HadoopWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HadoopWebApplication.class, args);
    }

}
/*

 // Parse the raw data and map it to the ExpenseData case class
    val expandedDF = rawDF.map { row =>
      val fields = row.split(",") // assuming data is comma-separated
      ExpenseData(
        userId = fields(0).split(":")(1).toLong,
        datetime = fields(1).split(":")(1).trim.replaceAll("\"", ""),
        description = fields(2).split(":")(1).trim.replaceAll("\"", ""),
        expenseType = fields(3).split(":")(1).trim.replaceAll("\"", ""),
        count = fields(4).split(":")(1).trim.toInt,
        payment = fields(5).split(":")(1).trim.toDouble
      )
    }

*/
