package com.example.bankingapp.data.remote;

import com.example.bankingapp.data.model.Account;
import com.example.bankingapp.data.model.Customer;
import com.example.bankingapp.data.model.WithdrawalRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BankApiService {

    // Accounts
    @GET("v1/accounts")
    Call<List<Account>> getAllAccounts();

    @POST("v1/accounts")
    Call<Account> createAccount(@Body Account account);

    @GET("v1/accounts/{iban}")
    Call<Account> getAccountDetails(@Path("iban") String iban);

    @PUT("v1/accounts/{iban}")
    Call<Account> updateAccount(@Path("iban") String iban, @Body Account account);

    @DELETE("v1/accounts/{iban}")
    Call<Void> deleteAccount(@Path("iban") String iban);

    @POST("v1/accounts/{iban}/deposit")
    Call<Void> deposit(@Path("iban") String iban, @Query("amount") double amount);

    @POST("v1/accounts/{iban}/withdrawal")
    Call<Void> makeWithdrawal(@Path("iban") String iban, @Body WithdrawalRequest request);

    // Transactions
    @GET("v1/accounts/{iban}/transactions")
    Call<List<Object>> getTransactions(@Path("iban") String iban);

    // Customers
    @GET("v1/customers")
    Call<List<Customer>> getCustomers();

    @POST("v1/customers")
    Call<Customer> createCustomer(@Body Customer customer);

    @GET("v1/customers/{id}")
    Call<Customer> getCustomer(@Path("id") String id);

    @PUT("v1/customers/{id}")
    Call<Customer> updateCustomer(@Path("id") String id, @Body Customer customer);

    @DELETE("v1/customers/{id}")
    Call<Void> deleteCustomer(@Path("id") String id);
}
