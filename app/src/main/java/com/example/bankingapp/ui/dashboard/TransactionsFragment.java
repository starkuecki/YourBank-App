package com.example.bankingapp.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankingapp.R;
import com.example.bankingapp.data.model.Transaction;
import com.example.bankingapp.data.repository.BankRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private BankRepository repository;
    private ImageButton btnBack;
    private TextView tvIbanHeader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        rvTransactions = view.findViewById(R.id.rv_transactions);
        btnBack = view.findViewById(R.id.btn_back);
        tvIbanHeader = view.findViewById(R.id.tv_transaction_iban);

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter();
        rvTransactions.setAdapter(adapter);

        repository = new BankRepository(requireActivity().getApplication());

        SharedPreferences prefs = requireActivity().getSharedPreferences("BankPrefs", Context.MODE_PRIVATE);
        String iban = prefs.getString("logged_in_iban", null);

        if (iban != null) {
            tvIbanHeader.setText(iban);
            loadTransactions(iban);
        }

        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void loadTransactions(String iban) {
        repository.getTransactions(iban, new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transaction>> call, @NonNull Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setTransactions(response.body());
                } else {
                    Toast.makeText(getContext(), "Fehler beim Laden der Transaktionen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Transaction>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Netzwerkfehler", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
