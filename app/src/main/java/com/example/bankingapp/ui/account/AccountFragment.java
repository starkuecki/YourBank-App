package com.example.bankingapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bankingapp.R;
import com.example.bankingapp.data.repository.BankRepository;
import com.example.bankingapp.ui.login.LoginActivity;

public class AccountFragment extends Fragment {

    private TextView tvName, tvAddress, tvIban;
    private Button btnSwitchAccount;
    private BankRepository repository;
    private final String testIban = "DE12123456789012345678";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvName = view.findViewById(R.id.tv_name);
        tvIban = view.findViewById(R.id.tv_iban);
        btnSwitchAccount = view.findViewById(R.id.btn_switch_account);

        repository = new BankRepository(requireActivity().getApplication());

        repository.getAccount(testIban).observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                tvIban.setText(account.getIban());
                // Name und Adresse sind aktuell nicht im Modell, daher Platzhalter oder Erweiterung nötig
                // tvName.setText(account.getOwnerName()); 
            }
        });

        btnSwitchAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Optional: MainActivity schließen
        });

        return view;
    }
}