package com.sau.hadoopweb.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id") // This references the foreign key to the Employee
    private Employee employee;

    private String dateTime;
    private String description;
    private String type;
    private int count;
    private float payment;

    public Expense(Long userId, String dateTime, String description, String type, int count, float payment) {
        this.id=userId;
        this.dateTime=dateTime;
        this.description=description;
        this.type=type;
        this.count=count;
        this.payment=payment;

    }

    public Expense() {

    }
}
