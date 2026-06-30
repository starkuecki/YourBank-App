package com.example.bankingapp.ui.transfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bankingapp.R;
import com.example.bankingapp.data.repository.BankRepository;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferFragment extends Fragment {

    private EditText etRecipientIban, etAmount, etPurpose;
    private MaterialButton btnSendMoney;
    private BankRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        etRecipientIban = view.findViewById(R.id.et_recipient_iban);
        etAmount = view.findViewById(R.id.et_amount);
        etPurpose = view.findViewById(R.id.et_purpose);
        btnSendMoney = view.findViewById(R.id.btn_send_money);

        repository = new BankRepository(requireActivity().getApplication());

        btnSendMoney.setOnClickListener(v -> executeTransfer());

        return view;
    }

    private void executeTransfer() {
        String recipientIban = etRecipientIban.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();

        if (recipientIban.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Bitte Empfänger-IBAN und Betrag eingeben!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // Eigene IBAN aus SharedPreferences holen
        SharedPreferences prefs = requireActivity().getSharedPreferences("BankPrefs", Context.MODE_PRIVATE);
        String loggedInIban = prefs.getString("logged_in_iban", "DE12123456789012345678");

        // Die Notiz könnte die Empfänger-IBAN enthalten, da die API aktuell nur Betrag/Zweck/Zeitstempel im Body hat
        String combinedPurpose = "An: " + recipientIban + " - " + purpose;

        repository.sendTransfer(loggedInIban, amount, combinedPurpose, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Überweisung erfolgreich gebucht!", Toast.LENGTH_LONG).show();
                    etAmount.setText("");
                    etPurpose.setText("");
                    etRecipientIban.setText("");
                } else {
                    Toast.makeText(getContext(), "Fehler beim Buchen (z.B. Deckung unzureichend)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Keine Internetverbindung oder API-Fehler.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
