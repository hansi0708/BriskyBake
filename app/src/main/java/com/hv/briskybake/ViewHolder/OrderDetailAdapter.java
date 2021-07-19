package com.hv.briskybake.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybake.Model.Order;
import com.hv.briskybake.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView product_name,quantity,price,discount,orderUnit;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        product_name=itemView.findViewById(R.id.product_name);
        quantity=itemView.findViewById(R.id.product_quantity);
        price=itemView.findViewById(R.id.product_price);
        discount=itemView.findViewById(R.id.product_discount);
        orderUnit=itemView.findViewById(R.id.product_unit);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>{

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order=myOrders.get(position);
        holder.product_name.setText(String.format("Name : %s",order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.price.setText(String.format("Price : %s",order.getPrice()));
        holder.discount.setText(String.format("Discount : %s",order.getDiscount()));
        holder.orderUnit.setText(String.format("%s (%s)",order.getOrderUnit(),order.getOrderValue()));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
