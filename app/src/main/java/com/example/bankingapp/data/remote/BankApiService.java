package com.example.bankingapp.data.remote;

import com.example.bankingapp.data.model.Account;
import com.example.bankingapp.data.model.WithdrawalRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BankApiService {
    @GET("accounts/{iban}")
    Call<Account> getAccountDetails(@Path("iban") String iban);

    @POST("accounts/{iban}/withdrawal")
    Call<Void> makeWithdrawal(@Path("iban") String iban, @Body WithdrawalRequest request);
}