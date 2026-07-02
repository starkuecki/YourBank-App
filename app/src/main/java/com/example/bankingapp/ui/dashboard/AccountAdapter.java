package com.example.bankingapp.ui.dashboard;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankingapp.R;
import com.example.bankingapp.data.model.Account;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<Account> accounts = new ArrayList<>();
    private final OnAccountClickListener listener;

    public interface OnAccountClickListener {
        void onAccountClick(String iban);
    }

    public AccountAdapter(OnAccountClickListener listener) {
        this.listener = listener;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.tvBalance.setText(String.format("$ %,.2f", account.getBalance()));
        holder.tvIban.setText(account.getIban());
        
        if ("current".equalsIgnoreCase(account.getAccountType())) {
            holder.cardAccount.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.bg_dark));
            holder.tvLabel.setText("CHECKING");
            holder.tvLabel.setTextColor(Color.parseColor("#B3FFFFFF"));
            holder.tvBalance.setTextColor(Color.WHITE);
            holder.tvIban.setTextColor(Color.parseColor("#B3FFFFFF"));
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText("current");
            holder.tvTag.setTextColor(Color.WHITE);
        } else {
            holder.cardAccount.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.booby_blue));
            holder.tvLabel.setText("SAVINGS");
            holder.tvLabel.setTextColor(Color.parseColor("#80000000")); // Semi-transparent black
            holder.tvBalance.setTextColor(Color.BLACK);
            holder.tvIban.setTextColor(Color.parseColor("#80000000"));
            holder.tvTag.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onAccountClick(account.getIban()));
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAccount;
        TextView tvLabel, tvBalance, tvIban, tvTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAccount = itemView.findViewById(R.id.card_account);
            tvLabel = itemView.findViewById(R.id.tv_account_label);
            tvBalance = itemView.findViewById(R.id.tv_account_balance);
            tvIban = itemView.findViewById(R.id.tv_account_iban);
            tvTag = itemView.findViewById(R.id.tv_account_type_tag);
        }
    }
}
