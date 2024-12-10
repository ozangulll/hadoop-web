package com.sau.hadoopweb.model;

public class ExpenseDTO {
    private int userId;
    private double totalExpenses;

    // Constructor
    public ExpenseDTO(int userId, double totalExpenses) {
        this.userId = userId;
        this.totalExpenses = totalExpenses;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}
