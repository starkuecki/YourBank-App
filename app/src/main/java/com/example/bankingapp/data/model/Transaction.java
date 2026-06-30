package com.example.bankingapp.data.model;

public class Transaction {
    private double amount;
    private String purpose;
    private String timestamp;
    private String transactionType; // "deposit" oder "withdrawal"

    public Transaction(double amount, String purpose, String timestamp, String transactionType) {
        this.amount = amount;
        this.purpose = purpose;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
    }

    public double getAmount() { return amount; }
    public String getPurpose() { return purpose; }
    public String getTimestamp() { return timestamp; }
    public String getTransactionType() { return transactionType; }
}
