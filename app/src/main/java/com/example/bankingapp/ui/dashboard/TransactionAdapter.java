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
import com.example.bankingapp.data.model.Transaction;
import java.util.ArrayList;
import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.tvPurpose.setText(transaction.getPurpose());
        holder.tvDate.setText(transaction.getTimestamp());
        
        double amount = transaction.getAmount();
        // Wir prüfen das Feld "transactionType", wie es vom Backend geliefert wird
        if ("withdrawal".equalsIgnoreCase(transaction.getTransactionType())) {
            holder.tvAmount.setText(String.format("- $ %,.2f", amount));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.transaktion_red));
        } else {
            holder.tvAmount.setText(String.format("+ $ %,.2f", amount));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.transaktion_green));
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPurpose, tvDate, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPurpose = itemView.findViewById(R.id.tv_transaction_purpose);
            tvDate = itemView.findViewById(R.id.tv_transaction_date);
            tvAmount = itemView.findViewById(R.id.tv_transaction_amount);
        }
    }
}
