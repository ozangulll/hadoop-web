package com.sau.hadoopweb.model;

public class ExpenseSummary {

    private long userId;
    private double totalExpenses;

    // Constructor
    public ExpenseSummary(long userId, double totalExpenses) {
        this.userId = userId;
        this.totalExpenses = totalExpenses;
    }

    // Getters and Setters
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}
