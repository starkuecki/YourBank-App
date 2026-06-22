package com.example.bankingapp.ui.transfer;

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

    private EditText etIban, etAmount, etPurpose;
    private MaterialButton btnSendMoney;
    private BankRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Verbindet die Java-Klasse mit der fragment_transfer.xml Datei aus dem res/layout-Ordner
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        // UI Elemente aus dem XML-Layout heraussuchen
        etIban = view.findViewById(R.id.et_recipient_iban);
        etAmount = view.findViewById(R.id.et_amount);
        etPurpose = view.findViewById(R.id.et_purpose);
        btnSendMoney = view.findViewById(R.id.btn_send_money);

        // Repository initialisieren
        repository = new BankRepository(requireActivity().getApplication());

        // Klick-Aktion für den "Send Money" Button definieren
        btnSendMoney.setOnClickListener(v -> executeTransfer());

        return view;
    }

    private void executeTransfer() {
        String iban = etIban.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();

        // Einfache Validierung vor dem Absenden
        if (iban.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Bitte IBAN und Betrag eingeben!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // Überweisung über das Repository an die API senden
        repository.sendTransfer(iban, amount, purpose, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Überweisung erfolgreich gebucht! ", Toast.LENGTH_LONG).show();
                    // Felder nach Erfolg leeren
                    etAmount.setText("");
                    etPurpose.setText("");
                } else {
                    Toast.makeText(getContext(), "Fehler beim Buchen (z.B. Deckung unzureichend)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                //Fallback
                Toast.makeText(getContext(), "Keine Internetverbindung. Überweisung abgebrochen.", Toast.LENGTH_LONG).show();
            }
        });
    }
}