package com.example.bankingapp.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson:GsonConverterFactory;

public class ApiClient {
    // 10.0.2.2 ist der Localhost-Tunnel des Android-Emulators
    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";
    private static Retrofit retrofit = null;

    public static BankApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(BankApiService.class);
    }
}