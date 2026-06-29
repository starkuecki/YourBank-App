package com.example.bankingapp.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.bankingapp.data.model.Account;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AccountDao_Impl implements AccountDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Account> __insertionAdapterOfAccount;

  public AccountDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAccount = new EntityInsertionAdapter<Account>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `accounts` (`iban`,`balance`,`accountType`,`ownerName`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Account entity) {
        if (entity.getIban() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getIban());
        }
        statement.bindDouble(2, entity.getBalance());
        if (entity.getAccountType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAccountType());
        }
        if (entity.getOwnerName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getOwnerName());
        }
      }
    };
  }

  @Override
  public void saveAccount(final Account account) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfAccount.insert(account);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<Account> getAccountByIban(final String iban) {
    final String _sql = "SELECT * FROM accounts WHERE iban = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (iban == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, iban);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"accounts"}, false, new Callable<Account>() {
      @Override
      @Nullable
      public Account call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIban = CursorUtil.getColumnIndexOrThrow(_cursor, "iban");
          final int _cursorIndexOfBalance = CursorUtil.getColumnIndexOrThrow(_cursor, "balance");
          final int _cursorIndexOfAccountType = CursorUtil.getColumnIndexOrThrow(_cursor, "accountType");
          final int _cursorIndexOfOwnerName = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerName");
          final Account _result;
          if (_cursor.moveToFirst()) {
            final String _tmpIban;
            if (_cursor.isNull(_cursorIndexOfIban)) {
              _tmpIban = null;
            } else {
              _tmpIban = _cursor.getString(_cursorIndexOfIban);
            }
            final double _tmpBalance;
            _tmpBalance = _cursor.getDouble(_cursorIndexOfBalance);
            final String _tmpAccountType;
            if (_cursor.isNull(_cursorIndexOfAccountType)) {
              _tmpAccountType = null;
            } else {
              _tmpAccountType = _cursor.getString(_cursorIndexOfAccountType);
            }
            final String _tmpOwnerName;
            if (_cursor.isNull(_cursorIndexOfOwnerName)) {
              _tmpOwnerName = null;
            } else {
              _tmpOwnerName = _cursor.getString(_cursorIndexOfOwnerName);
            }
            _result = new Account(_tmpIban,_tmpBalance,_tmpAccountType,_tmpOwnerName);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
