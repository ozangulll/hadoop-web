package com.sau.hadoopweb.ExpenseHandler

name := "Expense Handler"

version := "1.0"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.3" % "provided",
  "org.apache.spark" %% "spark-sql" % "3.5.3" % "provided",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.1",// Add Kafka connector
  "com.datastax.spark" %% "spark-cassandra-connector" % "3.5.0",
  "com.datastax.cassandra" % "cassandra-driver-core" % "4.0.0"
)

