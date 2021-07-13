package com.hv.briskybake;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Database.Database;
import com.hv.briskybake.Helper.RecyclerItemTouchHelper;
import com.hv.briskybake.Interface.RecyclerItemTouchHelperListener;
import com.hv.briskybake.Model.Favourite;
import com.hv.briskybake.ViewHolder.FavoriteAdapter;
import com.hv.briskybake.ViewHolder.FavoriteViewHolder;

public class FavoriteActivity extends AppCompatActivity
        implements RecyclerItemTouchHelperListener
{

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference list;

    FavoriteAdapter adapter;

    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        rootLayout=findViewById(R.id.rootLayout);

        recyclerView = findViewById(R.id.recycler_fav);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        loadFavorites();
    }

    private void loadFavorites() {
        adapter=new FavoriteAdapter(this,new Database(this).getAllFavorites(Common.currentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoriteViewHolder) {
            String name = ((FavoriteAdapter) recyclerView.getAdapter()).getItem(position).getFoodName();

            Favourite deleteItem = ((FavoriteAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAbsoluteAdapterPosition());
            int deleteIndex = viewHolder.getAbsoluteAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromFavorites(deleteItem.getFoodId(), Common.currentUser.getPhone());

            //Make Snackbar
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), name + " removed from Favorites!!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToFavorites(deleteItem);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }


}