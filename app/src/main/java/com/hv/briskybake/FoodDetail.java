package com.hv.briskybake;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Database.Database;
import com.hv.briskybake.Model.Food;
import com.hv.briskybake.Model.Order;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_name,food_price,food_description;
    ImageView foodimage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCart;
    ElegantNumberButton numberButton;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;
    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database=FirebaseDatabase.getInstance();
        foods=database.getReference("Food");

        //Init view
        numberButton=findViewById(R.id.number_button);
        btnCart=findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                ));
                Toast.makeText(FoodDetail.this,"Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setCount(new Database(this).getCountCarts());

        food_description=findViewById(R.id.food_description);
        food_name=findViewById(R.id.foodname);
        food_price=findViewById(R.id.food_price);

        foodimage=findViewById(R.id.img_food);

        collapsingToolbarLayout=findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        //Get foodId from intent
        if(getIntent()!=null)
            foodId=getIntent().getStringExtra("FoodId");
        if(!foodId.isEmpty())
        {
            if(Common.isConnectToInternet(getBaseContext()))
                getDetailFood(foodId);
            else{
                Toast.makeText(FoodDetail.this, "Please checck your connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood=snapshot.getValue(Food.class);

                Picasso.get().load(currentFood.getImage()).into(foodimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}