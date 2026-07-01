package com.example.bankingapp.data.remote;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static final String BASE_URL = "http://100.89.37.106:8080/";

    private static BankApiService service;
    private static String cachedUser;
    private static String cachedPassHash;

    public static BankApiService getApiService() {
        return getApiService(null, null);
    }

    public static synchronized BankApiService getApiService(String username, String passwordHash) {
        // Wenn die Zugangsdaten gleich geblieben sind, geben wir den existierenden Service zurück
        if (service != null) {
            boolean sameUser = (username == null && cachedUser == null) || (username != null && username.equals(cachedUser));
            boolean samePass = (passwordHash == null && cachedPassHash == null) || (passwordHash != null && passwordHash.equals(cachedPassHash));
            
            if (sameUser && samePass) {
                return service;
            }
        }

        // Neue Instanz bauen
        cachedUser = username;
        cachedPassHash = passwordHash;

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (username != null && passwordHash != null) {
            clientBuilder.addInterceptor(chain -> {
                okhttp3.Request request = chain.request().newBuilder()
                        .addHeader("Authorization", Credentials.basic(username, passwordHash))
                        .build();
                return chain.proceed(request);
            });
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        service = retrofit.create(BankApiService.class);
        return service;
    }
}
