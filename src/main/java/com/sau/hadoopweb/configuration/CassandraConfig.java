package com.sau.hadoopweb.configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class CassandraConfig {

    @Bean
    public CqlSession cqlSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress("127.0.0.1", 9042)) // Sunucu adresi ve portu
                .withLocalDatacenter("datacenter1") // Datacenter adı
                .withKeyspace("expense") // Veritabanı adı
                .build();
    }
}
