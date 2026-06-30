package com.example.bankingapp.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bankingapp.R;
import com.example.bankingapp.data.repository.BankRepository;
import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {
    private TextView tvBalance, tvWelcomeName;
    private ProgressBar pbLoading;
    private MaterialCardView cardBalance;
    private BankRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvWelcomeName = view.findViewById(R.id.tv_welcome_name);
        pbLoading = view.findViewById(R.id.pb_balance_loading);
        cardBalance = view.findViewById(R.id.card_balance);

        repository = new BankRepository(requireActivity().getApplication());

        SharedPreferences prefs = requireActivity().getSharedPreferences("BankPrefs", Context.MODE_PRIVATE);
        String loggedInIban = prefs.getString("logged_in_iban", null);
        String authUser = prefs.getString("auth_user", null);

        // Initialer Ladezustand
        tvBalance.setAlpha(0.3f);
        pbLoading.setVisibility(View.VISIBLE);

        // Name vom Customer laden
        if (authUser != null) {
            repository.getCustomer(authUser).observe(getViewLifecycleOwner(), customer -> {
                if (customer != null && customer.getName() != null) {
                    tvWelcomeName.setText(getString(R.string.welcome_user, customer.getName()));
                }
            });
        }

        // Kontostand laden
        if (loggedInIban != null) {
            repository.getAccount(loggedInIban).observe(getViewLifecycleOwner(), account -> {
                if (account != null) {
                    tvBalance.setText(String.format("$ %,.2f", account.getBalance()));
                    tvBalance.setAlpha(1.0f);
                    pbLoading.setVisibility(View.GONE);
                }
            });
        }

        // Klick auf den schwarzen Kasten -> Transaktionen öffnen
        cardBalance.setOnClickListener(v -> {
            Fragment transactionsFragment = new TransactionsFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, transactionsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
