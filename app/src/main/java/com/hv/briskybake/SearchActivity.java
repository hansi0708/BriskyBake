package com.hv.briskybake;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Database.Database;
import com.hv.briskybake.Interface.ItemClickListener;
import com.hv.briskybake.Model.Favourite;
import com.hv.briskybake.Model.Food;
import com.hv.briskybake.Model.Order;
import com.hv.briskybake.ViewHolder.FoodViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.hv.briskybake.Common.Common.currentUser;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference list;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Favorites
    Database localDB;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchadapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        //Firebase
        database=FirebaseDatabase.getInstance();
        list=database.getReference("Food");

        //Local DB
        localDB=new Database(this);

        materialSearchBar=findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        //materialSearchBar.setSpeechMode(false);
        loadSuggest();
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user will type the text, we will change the suggest list


                List<String> suggest=new ArrayList<String>();
                for(String search:suggestList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search ba is closed
                //Restore original suggest adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish, show result of search adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recyclerView=findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        listAll();
    }

    private void listAll()  {
        FirebaseRecyclerOptions<Food> options=new FirebaseRecyclerOptions.Builder<Food>().
                setQuery(list,Food.class).
                build();
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.textItemName.setText(model.getName());
                holder.textItemPrice.setText(String.format("₹ %s",model.getPrice()));
                Picasso.get().load(model.getImage()).into(holder.imageViewItem, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                holder.cart_quick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean isExists=new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(),Common.currentUser.getPhone(),model.getUnit().get(0));
                        if(!isExists) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage(),
                                    model.getUnit().get(0),
                                    model.getMenuValue()
                            ));

                        }
                        else {
                            new Database(getBaseContext()).incCart(Common.currentUser.getPhone(),adapter.getRef(position).getKey(),model.getUnit().get(0));
                        }
                        Toast.makeText(SearchActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                    }
                });

                //Add Favorites
                if (localDB.isFavorites(adapter.getRef(position).getKey(), Common.currentUser.getPhone()))
                    holder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_24);

                //Click to change state of favorites
                holder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favourite favourite=new Favourite();
                        favourite.setFoodId(adapter.getRef(position).getKey());
                        favourite.setFoodName(model.getName());
                        favourite.setFoodPrice(model.getPrice());
                        favourite.setFoodDescription(model.getDescription());
                        favourite.setFoodDiscount(model.getDiscount());
                        favourite.setFoodImage(model.getImage());
                        favourite.setFoodMenuId(model.getMenuId());
                        favourite.setUserPhone(currentUser.getPhone());
                        if (!localDB.isFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavorites(favourite);
                            holder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_24);
                            Toast.makeText(SearchActivity.this, ""+model.getName()+" was added to Favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            holder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            Toast.makeText(SearchActivity.this, ""+model.getName()+" was removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (model.getDiscount()==null|| model.getDiscount().equals("0")) {
                    holder.dis.setVisibility(View.GONE);
                    holder.off.setVisibility(View.GONE);

                }
                else {
                    holder.dis.setText(String.format("₹ %s", model.getDiscount()));
                    holder.dis.setVisibility(View.VISIBLE);
                    holder.off.setVisibility(View.VISIBLE);
                }

                final Food local=model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail=new Intent(SearchActivity.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey()); //Send food id to new activity
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bakeryitem,parent,false);
                return new FoodViewHolder(view);
            }
        };

        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void startSearch(CharSequence text) {
        FirebaseRecyclerOptions<Food> options=new FirebaseRecyclerOptions.Builder<Food>().
                setQuery(list.orderByChild("name").equalTo(text.toString().trim()),Food.class).
                build();
        searchadapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.textItemName.setText(model.getName());
                holder.textItemPrice.setText(String.format("₹ %s",model.getPrice()));
                Picasso.get().load(model.getImage()).into(holder.imageViewItem, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                holder.cart_quick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean isExists=new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(),Common.currentUser.getPhone(),model.getUnit().get(0));
                        if(!isExists) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage(),
                                    model.getUnit().get(0),
                                    model.getMenuValue()
                            ));

                        }
                        else {
                            new Database(getBaseContext()).incCart(Common.currentUser.getPhone(),adapter.getRef(position).getKey(),model.getUnit().get(0));
                        }
                        Toast.makeText(SearchActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                    }
                });

                //Add Favorites
                if (localDB.isFavorites(adapter.getRef(position).getKey(), Common.currentUser.getPhone()))
                    holder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_24);

                //Click to change state of favorites
                holder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favourite favourite=new Favourite();
                        favourite.setFoodId(adapter.getRef(position).getKey());
                        favourite.setFoodName(model.getName());
                        favourite.setFoodPrice(model.getPrice());
                        favourite.setFoodDescription(model.getDescription());
                        favourite.setFoodDiscount(model.getDiscount());
                        favourite.setFoodImage(model.getImage());
                        favourite.setFoodMenuId(model.getMenuId());
                        favourite.setUserPhone(currentUser.getPhone());
                        if (!localDB.isFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavorites(favourite);
                            holder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_24);
                            Toast.makeText(SearchActivity.this, ""+model.getName()+" was added to Favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            holder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            Toast.makeText(SearchActivity.this, ""+model.getName()+" was removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (model.getDiscount()==null|| model.getDiscount().equals("0")) {
                    holder.dis.setVisibility(View.GONE);
                    holder.off.setVisibility(View.GONE);

                }
                else {
                    holder.dis.setText(String.format("₹ %s", model.getDiscount()));
                    holder.dis.setVisibility(View.VISIBLE);
                    holder.off.setVisibility(View.VISIBLE);
                }

                final Food local=model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail=new Intent(SearchActivity.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchadapter.getRef(position).getKey()); //Send food id to new activity
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bakeryitem,parent,false);
                return new FoodViewHolder(view);
            }
        };

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        searchadapter.startListening();
        recyclerView.setAdapter(searchadapter);
    }

    @Override
    protected void onStop() {
        if (adapter!=null)
            adapter.stopListening();
        if (searchadapter!=null)
            searchadapter.stopListening();
        super.onStop();
    }

    private void loadSuggest() {
        list.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot postSnapshot:snapshot.getChildren())
                        {
                            Food item=postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}