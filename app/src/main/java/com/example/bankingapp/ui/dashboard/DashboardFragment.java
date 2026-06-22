package com.example.bankingapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bankingapp.R;
import com.example.bankingapp.data.repository.BankRepository;

public class DashboardFragment extends Fragment {
    private TextView tvBalance;
    private BankRepository repository;
    private final String testIban = "DE12123456789012345678"; // Deine Test-IBAN aus der API

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tvBalance = view.findViewById(R.id.tv_balance);

        repository = new BankRepository(requireActivity().getApplication());

        // LiveData lauschen: Aktualisiert sich live bei Internet,
        // behält den alten Wert bei Offline-Modus!
        repository.getAccount(testIban).observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                tvBalance.setText(String.format("$ %,.2f", account.getBalance()));
            }
        });

        return view;
    }
}