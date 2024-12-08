package com.sau.hadoopweb.controller;

import com.sau.hadoopweb.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/generate-expenses")
    @ResponseBody
    public String generateExpenses() throws InterruptedException {
        expenseService.generateExpenses();
        return "Expenses generated successfully!";
    }
}
