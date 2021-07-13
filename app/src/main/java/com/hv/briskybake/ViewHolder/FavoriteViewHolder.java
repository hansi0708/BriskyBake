package com.hv.briskybake.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybake.Interface.ItemClickListener;
import com.hv.briskybake.R;

public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textItemName, textItemPrice;
    public ImageView imageViewItem, fav_image;
    private ItemClickListener itemClickListener;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    public FavoriteViewHolder(@NonNull View itemView) {
        super(itemView);
        textItemName = itemView.findViewById(R.id.item_name);
        imageViewItem = itemView.findViewById(R.id.item_image);
        textItemPrice = itemView.findViewById(R.id.item_price);
        fav_image = itemView.findViewById(R.id.fav);

        view_background=itemView.findViewById(R.id.view_background);
        view_foreground=itemView.findViewById(R.id.view_foreground);

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
