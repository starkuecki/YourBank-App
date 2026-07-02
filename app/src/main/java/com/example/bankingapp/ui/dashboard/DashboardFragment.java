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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankingapp.R;
import com.example.bankingapp.data.model.Account;
import com.example.bankingapp.data.repository.BankRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment {
    private TextView tvWelcomeName;
    private ProgressBar pbLoading;
    private RecyclerView rvAccounts;
    private AccountAdapter adapter;
    private BankRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tvWelcomeName = view.findViewById(R.id.tv_welcome_name);
        pbLoading = view.findViewById(R.id.pb_dashboard_loading);
        rvAccounts = view.findViewById(R.id.rv_accounts);

        rvAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AccountAdapter(this::openTransactions);
        rvAccounts.setAdapter(adapter);

        repository = new BankRepository(requireActivity().getApplication());

        SharedPreferences prefs = requireActivity().getSharedPreferences("BankPrefs", Context.MODE_PRIVATE);
        String authUser = prefs.getString("auth_user", null);
        String userId = prefs.getString("logged_in_user_id", null);

        pbLoading.setVisibility(View.VISIBLE);

        // Name vom Customer laden
        if (authUser != null) {
            repository.getCustomer(authUser).observe(getViewLifecycleOwner(), customer -> {
                if (customer != null && customer.getName() != null) {
                    tvWelcomeName.setText(getString(R.string.welcome_user, customer.getName()));
                }
            });
        }

        // Alle Konten des Users laden
        if (userId != null) {
            repository.getAccountsForUser(userId).observe(getViewLifecycleOwner(), accounts -> {
                if (accounts != null && !accounts.isEmpty()) {
                    // Sortierung: Current zuerst, dann Savings
                    List<Account> sortedAccounts = new ArrayList<>(accounts);
                    Collections.sort(sortedAccounts, (a1, a2) -> {
                        if ("current".equalsIgnoreCase(a1.getAccountType()) && !"current".equalsIgnoreCase(a2.getAccountType())) return -1;
                        if (!"current".equalsIgnoreCase(a1.getAccountType()) && "current".equalsIgnoreCase(a2.getAccountType())) return 1;
                        return 0;
                    });
                    adapter.setAccounts(sortedAccounts);
                    pbLoading.setVisibility(View.GONE);
                }
            });
        }

        return view;
    }

    private void openTransactions(String iban) {
        // IBAN in Prefs speichern, falls andere Fragmente sie brauchen (optional)
        requireActivity().getSharedPreferences("BankPrefs", Context.MODE_PRIVATE)
                .edit().putString("logged_in_iban", iban).apply();

        Fragment transactionsFragment = new TransactionsFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, transactionsFragment)
                .addToBackStack(null)
                .commit();
    }
}
