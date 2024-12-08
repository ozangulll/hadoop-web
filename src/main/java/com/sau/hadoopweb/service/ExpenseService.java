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

    private static final String[] descriptions = {"Macaroni, food", "Jacket, clothe", "Car, vehicle", "Laptop, electronics", "Book, education"};
    private static final String[] types = {"food", "clothes", "vehicle", "electronics", "education"};

    public void generateExpenses() throws InterruptedException {
        List<Employee> employees = employeeRepository.findAll();
        Random rand = new Random();

        for (Employee employee : employees) {
            String dateTime = generateRandomDateTime();
            String description = descriptions[rand.nextInt(descriptions.length)];
            String type = types[rand.nextInt(types.length)];
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
        // Converts the Expense object to a simple message
        return String.format("UserId: %d, DateTime: %s, Description: %s, Type: %s, Count: %d, Payment: %.2f",
                expense.getEmployee().getId(),  // Access Employee's ID
                expense.getDateTime(),
                expense.getDescription(),
                expense.getType(),
                expense.getCount(),
                expense.getPayment());
    }
}
