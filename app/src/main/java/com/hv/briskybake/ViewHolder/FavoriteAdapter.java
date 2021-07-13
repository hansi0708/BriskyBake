package com.hv.briskybake.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybake.FoodDetail;
import com.hv.briskybake.Interface.ItemClickListener;
import com.hv.briskybake.Model.Favourite;
import com.hv.briskybake.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder>{

    private Context context;
    private List<Favourite> favoritesList;

    public FavoriteAdapter(Context context, List<Favourite> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @NonNull
    @NotNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context)
                .inflate(R.layout.favorite_item,parent,false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FavoriteViewHolder holder, int position) {
                holder.textItemName.setText(favoritesList.get(position).getFoodName());
                holder.textItemPrice.setText(String.format("₹ %s", favoritesList.get(position).getFoodPrice()));



                Picasso.get().load(favoritesList.get(position).getFoodImage()).into(holder.imageViewItem, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                final Favourite local = favoritesList.get(position);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail = new Intent(context, FoodDetail.class);
                        foodDetail.putExtra("FoodId", favoritesList.get(position).getFoodId()); //Send food id to new activity
                        context.startActivity(foodDetail);
                    }
                });


    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void removeItem(int position)
    {
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favourite item, int position)
    {
        favoritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favourite getItem(int position)
    {
        return favoritesList.get(position);
    }
}
