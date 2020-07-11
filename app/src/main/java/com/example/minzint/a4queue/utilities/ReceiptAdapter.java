package com.example.minzint.a4queue.utilities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.minzint.a4queue.R;
import com.example.minzint.a4queue.ReceiptDesc;
import com.example.minzint.a4queue.models.ReceiptInfo;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptHolder> {

    private List<ReceiptInfo> receiptInfoList;
    private Context context;

    public ReceiptAdapter(List<ReceiptInfo> receiptInfoList, Context context) {
        this.receiptInfoList = receiptInfoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReceiptAdapter.ReceiptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipts_list, parent, false );
        return new ReceiptHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptAdapter.ReceiptHolder holder, final int position) {

        holder.address.setText("Address: "+receiptInfoList.get(position).getAddress());
        holder.total.setText("Total paid: "+receiptInfoList.get(position).getTotal_paid());
        holder.date.setText("Date: "+receiptInfoList.get(position).getDate());

        holder.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReceiptDesc.class);
                intent.putExtra("ReceiptInfo", receiptInfoList.get(position));
                context.startActivities(new Intent[]{intent});
            }
        });
    }

    @Override
    public int getItemCount() {
        return receiptInfoList.size();
    }

    public class ReceiptHolder extends RecyclerView.ViewHolder{

        private TextView address;
        private TextView total;
        private TextView date;
        private View view;

        public ReceiptHolder(View itemView) {
            super(itemView);
            view = itemView;
            address = itemView.findViewById(R.id.textViewAddressReceipt);
            total = itemView.findViewById(R.id.textViewTotalReceipt);
            date = itemView.findViewById(R.id.textViewDateReceipt);
        }
    }
}
