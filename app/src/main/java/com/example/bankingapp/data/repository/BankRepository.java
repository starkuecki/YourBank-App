package com.example.bankingapp.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.bankingapp.data.local.AccountDao;
import com.example.bankingapp.data.local.AppDatabase;
import com.example.bankingapp.data.model.Account;
import com.example.bankingapp.data.model.WithdrawalRequest;
import com.example.bankingapp.data.remote.ApiClient;
import com.example.bankingapp.data.remote.BankApiService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankRepository {
    private final AccountDao accountDao;
    private final BankApiService apiService;
    private final ExecutorService executorService; // Für Hintergrund-Tasks (Datenbank)

    public BankRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.accountDao = db.accountDao();
        this.apiService = ApiClient.getApiService();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Gibt LiveData aus der DATENBANK zurück (Single Source of Truth)
    public LiveData<Account> getAccount(String iban) {
        // Stoße asynchron das Laden neuer API-Daten an
        refreshAccountFromApi(iban);
        // Gib das Datenbank-Objekt zurück. Wenn die API fertig geladen hat,
        // aktualisiert sich dieses LiveData-Objekt vollautomatisch!
        return accountDao.getAccountByIban(iban);
    }

    private void refreshAccountFromApi(String iban) {
        apiService.getAccountDetails(iban).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API erfolgreich -> Speicher die Daten im Offline-Cache
                    executorService.execute(() -> accountDao.saveAccount(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                // Internet abgebrochen! Keine Aktion nötig.
                // Die UI zeigt einfach den letzten Stand aus der DB, der bereits geladen ist.
            }
        });
    }

    // Überweisung absenden
    public void sendTransfer(String iban, double amount, String purpose, Callback<Void> callback) {
        String currentTimestamp = java.time.ZonedDateTime.now().toString();
        WithdrawalRequest request = new WithdrawalRequest(amount, purpose, currentTimestamp);

        apiService.makeWithdrawal(iban, request).enqueue(callback);
    }
}