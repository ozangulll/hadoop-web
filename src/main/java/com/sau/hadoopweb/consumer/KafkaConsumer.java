package com.sau.hadoopweb.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "expenses", groupId = "expense_group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
        // Logic to save the message to the database can be added here
    }
}