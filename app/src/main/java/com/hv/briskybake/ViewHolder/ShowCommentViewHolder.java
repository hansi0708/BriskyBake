package com.hv.briskybake.ViewHolder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hv.briskybake.R;

import org.jetbrains.annotations.NotNull;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {
    public TextView txtName,txtComment;
    public RatingBar rating_Bar;
    public ShowCommentViewHolder(@NonNull @NotNull View itemView) {

        super(itemView);
        txtName=itemView.findViewById(R.id.txtName);
        txtComment=itemView.findViewById(R.id.txtComment);
        rating_Bar=itemView.findViewById(R.id.rating_bar);
    }
}
