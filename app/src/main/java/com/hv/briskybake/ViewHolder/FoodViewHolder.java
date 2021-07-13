package com.hv.briskybake.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybake.Interface.ItemClickListener;
import com.hv.briskybake.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textItemName, textItemPrice,dis,off;
    public ImageView imageViewItem, fav_image,cart_quick;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        textItemName = itemView.findViewById(R.id.item_name);
        imageViewItem = itemView.findViewById(R.id.item_image);
        textItemPrice = itemView.findViewById(R.id.item_price);
        fav_image = itemView.findViewById(R.id.fav);
        cart_quick=itemView.findViewById(R.id.quick_cart);
        dis=itemView.findViewById(R.id.discount);
        off=itemView.findViewById(R.id.off);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
