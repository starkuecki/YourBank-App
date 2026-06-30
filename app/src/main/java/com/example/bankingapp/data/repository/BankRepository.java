package com.example.bankingapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import com.example.bankingapp.data.local.AccountDao;
import com.example.bankingapp.data.local.CustomerDao;
import com.example.bankingapp.data.local.AppDatabase;
import com.example.bankingapp.data.model.Account;
import com.example.bankingapp.data.model.Customer;
import com.example.bankingapp.data.model.WithdrawalRequest;
import com.example.bankingapp.data.remote.ApiClient;
import com.example.bankingapp.data.remote.BankApiService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankRepository {
    private final AccountDao accountDao;
    private final CustomerDao customerDao;
    private final ExecutorService executorService;
    private final SharedPreferences prefs;

    public BankRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.accountDao = db.accountDao();
        this.customerDao = db.customerDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.prefs = application.getSharedPreferences("BankPrefs", Context.MODE_PRIVATE);
    }

    private BankApiService getAuthApiService() {
        String user = prefs.getString("auth_user", null);
        String passHash = prefs.getString("auth_pass_hash", null);
        return ApiClient.getApiService(user, passHash);
    }

    public void login(String username, String password, LoginCallback callback) {
        String passwordHash = sha256(password);
        
        executorService.execute(() -> {
            // 1. Offline-Check
            Customer localCustomer = customerDao.getCustomerByName(username);
            if (localCustomer != null) {
                if (localCustomer.getPassword().equals(passwordHash)) {
                    // Login-Daten speichern
                    saveLoginPrefs(username, passwordHash);
                    
                    // WICHTIG: IBAN für diesen User aktualisieren (Hintergrund)
                    updateActiveIbanForUser(localCustomer.getId());
                    
                    callback.onSuccess();
                    return;
                } else {
                    callback.onError("Benutzername oder Passwort falsch");
                    return;
                }
            }

            // 2. Online-Check
            BankApiService apiService = ApiClient.getApiService(username, passwordHash);
            apiService.getCustomers().enqueue(new Callback<List<Customer>>() {
                @Override
                public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                    if (response.code() == 401) {
                        callback.onError("Benutzername oder Passwort falsch");
                        return;
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        for (Customer customer : response.body()) {
                            if (customer.getName().equalsIgnoreCase(username)) {
                                executorService.execute(() -> {
                                    customerDao.saveCustomer(customer);
                                    // Wir speichern den offiziellen Namen aus der API (Groß/Kleinschreibung!)
                                    saveLoginPrefs(customer.getName(), passwordHash);
                                    fetchAndSaveAccounts(customer.getName(), passwordHash, customer.getId());
                                });
                                callback.onSuccess();
                                return;
                            }
                        }
                    }
                    callback.onError("Benutzername oder Passwort falsch");
                }

                @Override
                public void onFailure(Call<List<Customer>> call, Throwable t) {
                    callback.onError("Keine Verbindung zum Server");
                }
            });
        });
    }

    // Sucht lokal nach dem primären Konto des Users und setzt die IBAN
    private void updateActiveIbanForUser(String ownerId) {
        // Wir triggern einfach ein schnelles Background-Update
        String user = prefs.getString("auth_user", null);
        String passHash = prefs.getString("auth_pass_hash", null);
        if (user != null && passHash != null) {
            fetchAndSaveAccounts(user, passHash, ownerId);
        }
    }

    private void fetchAndSaveAccounts(String user, String passHash, String ownerId) {
        ApiClient.getApiService(user, passHash).getAllAccounts().enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        String primaryIban = null;
                        for (Account acc : response.body()) {
                            accountDao.saveAccount(acc);
                            // Filter auf den eingeloggten User + Typ 'current'
                            if (ownerId.equals(acc.getOwnerId()) && "current".equalsIgnoreCase(acc.getAccountType())) {
                                if (primaryIban == null) primaryIban = acc.getIban();
                            }
                        }
                        
                        // Wenn wir ein passendes Konto gefunden haben, setzen wir es als aktiv
                        if (primaryIban != null) {
                            prefs.edit().putString("logged_in_iban", primaryIban).apply();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {}
        });
    }

    private void saveLoginPrefs(String user, String passHash) {
        prefs.edit()
                .putString("auth_user", user)
                .putString("auth_pass_hash", passHash)
                .apply();
    }

    public LiveData<Account> getAccount(String iban) {
        if (iban != null) {
            refreshAccountFromApi(iban);
        }
        return accountDao.getAccountByIban(iban);
    }

    public LiveData<Customer> getCustomer(String name) {
        return customerDao.getCustomerLiveDataByName(name);
    }

    private void refreshAccountFromApi(String iban) {
        getAuthApiService().getAccountDetails(iban).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> accountDao.saveAccount(response.body()));
                }
            }
            @Override
            public void onFailure(Call<Account> call, Throwable t) {}
        });
    }

    public void sendTransfer(String iban, double amount, String purpose, Callback<Void> callback) {
        String currentTimestamp = java.time.ZonedDateTime.now().toString();
        WithdrawalRequest request = new WithdrawalRequest(amount, purpose, currentTimestamp);
        getAuthApiService().makeWithdrawal(iban, request).enqueue(callback);
    }

    private String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public interface LoginCallback {
        void onSuccess();
        void onError(String message);
    }
}
