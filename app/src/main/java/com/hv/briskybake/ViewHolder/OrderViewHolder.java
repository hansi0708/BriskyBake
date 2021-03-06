package com.hv.briskybake.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybake.Interface.ItemClickListener;
import com.hv.briskybake.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress,txtOrderDate;

    private ItemClickListener itemClickListener;

    public ImageView btn_delete;
    Button track;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId=itemView.findViewById(R.id.order_id);
        txtOrderStatus=itemView.findViewById(R.id.order_status);
        txtOrderPhone=itemView.findViewById(R.id.order_phone);
        txtOrderAddress=itemView.findViewById(R.id.order_address);
        txtOrderDate=itemView.findViewById(R.id.order_date);
        btn_delete=itemView.findViewById(R.id.btn_delete);
        track=itemView.findViewById(R.id.map);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
