package com.example.bankingapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.bankingapp.data.model.Account;

@Dao
public interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAccount(Account account);

    // LiveData sorgt dafür, dass die UI sich automatisch aktualisiert, wenn sich Daten ändern
    @Query("SELECT * FROM accounts WHERE iban = :iban")
    LiveData<Account> getAccountByIban(String iban);
}