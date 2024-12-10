package com.sau.hadoopweb;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CassandraExample {
    public static void main(String[] args) {
        // Cassandra sunucusuna bağlanma
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("127.0.0.1", 9042)) // Sunucu adresi ve portu
                .withLocalDatacenter("datacenter1") // Datacenter adını buraya yazın
                .withKeyspace("expense") // Veritabanı adını buraya yazın
                .build()) {

            // Sorgu: her userid için total_expenses toplamını al
            String query = "SELECT userid, total_expenses FROM user_expenses"; // Tablo ismini buraya yazın

            // Sorguyu çalıştırma
            ResultSet resultSet = session.execute(query);

            // Her userid için total_expenses toplamını hesaplamak
            Map<Integer, Double> expenseMap = new HashMap<>();

            // ResultSet üzerinde iterator kullanarak döngü
            Iterator<Row> iterator = resultSet.iterator();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                int userid = row.getInt("userid");
                double totalExpenses = row.getDouble("total_expenses");

                // Eğer userid zaten var, mevcut değeri ekle, yoksa yeni bir giriş ekle
                expenseMap.put(userid, expenseMap.getOrDefault(userid, 0.0) + totalExpenses);
            }

            // Sonuçları ekrana yazdırma
            for (Map.Entry<Integer, Double> entry : expenseMap.entrySet()) {
                System.out.println("UserID: " + entry.getKey() + ", Total Expenses: " + entry.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
