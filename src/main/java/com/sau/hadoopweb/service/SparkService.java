package com.sau.hadoopweb.service;

import com.sau.hadoopweb.model.ExpenseSummary;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;
import static org.apache.spark.sql.functions.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class SparkService {

    private final SparkSession spark;

    public SparkService() {
        // SparkSession başlatma
        this.spark = SparkSession.builder()
                .appName("CassandraTotalExpenseCalculation")
                .config("spark.cassandra.connection.host", "localhost")
                .master("local[*]")
                .getOrCreate();
    }

    public List<ExpenseSummary> getTotalExpensesByUser() {
        // Cassandra'dan veri yükle
        Dataset<Row> df = spark.read()
                .format("org.apache.spark.sql.cassandra")
                .option("table", "user_expenses")  // Kullanılan Cassandra tablo adını buraya yazın
                .option("keyspace", "expense")  // Kullanılan Cassandra keyspace adını buraya yazın
                .load();

        // Kullanıcıya göre grupla ve toplam harcamaları hesapla
        Dataset<Row> totalExpensesByUser = df.groupBy("userid")
                .agg(sum("total_expenses").alias("total_expenses"));

        // Sonuçları ExpenseSummary modeline dönüştür
        List<ExpenseSummary> expenseSummaryList = new ArrayList<>();
        totalExpensesByUser.collectAsList().forEach(row -> {
            long userId = ((Number) row.getAs("userid")).longValue(); // Handle casting properly
            double totalExpenses = row.getAs("total_expenses");
            expenseSummaryList.add(new ExpenseSummary(userId, totalExpenses));
        });

        return expenseSummaryList;
    }

}
