package com.sau.hadoopweb.repository;

import com.sau.hadoopweb.model.Employee;
import com.sau.hadoopweb.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Expense findByEmployee(Employee employee);
}
