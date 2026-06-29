package com.example.bankingapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey
    @NonNull
    private String iban;
    private double balance;
    private String accountType;
    private String ownerName;

    // Konstruktor, Getter und Setter
    public Account(@NonNull String iban, double balance, String accountType, String ownerName) {
        this.iban = iban;
        this.balance = balance;
        this.accountType = accountType;
        this.ownerName = ownerName;
    }

    @NonNull public String getIban() { return iban; }
    public void setIban(@NonNull String iban) { this.iban = iban; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}