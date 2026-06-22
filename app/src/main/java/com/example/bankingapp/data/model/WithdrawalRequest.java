package com.example.bankingapp.data.model;

public class WithdrawalRequest {
    private double amount;
    private String purpose;
    private String timestamp;

    public WithdrawalRequest(double amount, String purpose, String timestamp) {
        this.amount = amount;
        this.purpose = purpose;
        this.timestamp = timestamp;
    }
}