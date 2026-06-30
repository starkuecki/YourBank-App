package com.example.bankingapp.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private TextView tvName, tvIban, tvCity;
    private Button btnSwitchAccount;
    private BankRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvName = view.findViewById(R.id.tv_name);
        tvCity = view.findViewById(R.id.tv_city);
        tvIban = view.findViewById(R.id.tv_iban);
        btnSwitchAccount = view.findViewById(R.id.btn_switch_account);

        repository = new BankRepository(requireActivity().getApplication());

        SharedPreferences prefs = requireActivity().getSharedPreferences("BankPrefs", Context.MODE_PRIVATE);
        String loggedInIban = prefs.getString("logged_in_iban", null);
        String authUser = prefs.getString("auth_user", null);

        // Name und Stadt vom Customer laden
        if (authUser != null) {
            repository.getCustomer(authUser).observe(getViewLifecycleOwner(), customer -> {
                if (customer != null) {
                    tvName.setText(customer.getName());
                    tvCity.setText(customer.getCity());
                }
            });
        }

        // IBAN vom Account laden
        if (loggedInIban != null) {
            repository.getAccount(loggedInIban).observe(getViewLifecycleOwner(), account -> {
                if (account != null) {
                    tvIban.setText(account.getIban());
                }
            });
        }

        btnSwitchAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}
