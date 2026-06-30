package com.example.bankingapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customers")
public class Customer {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String city;
    private String password;

    public Customer(@NonNull String id, String name, String city, String password) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.password = password;
    }

    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
