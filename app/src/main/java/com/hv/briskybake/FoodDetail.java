package com.hv.briskybake;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Database.Database;
import com.hv.briskybake.Model.Food;
import com.hv.briskybake.Model.Order;
import com.hv.briskybake.Model.Rating;
import com.hv.briskybake.ViewHolder.ShowCommentViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_price, food_description;
    ImageView foodimage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCart;
    FloatingActionButton btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;

    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    Food currentFood;
    Spinner units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");
        ratingTbl = database.getReference("Rating");

        //Init view
        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isExists=new Database(getBaseContext()).checkFoodExists(foodId,Common.currentUser.getPhone());
                if(!isExists) {
                    new Database(getBaseContext()).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            foodId,
                            currentFood.getName(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage()
                    ));
                }
                else {
                    new Database(getBaseContext()).incCart(Common.currentUser.getPhone(),foodId);
                }
                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                btnCart.setCount(new Database(FoodDetail.this).getCountCarts(Common.currentUser.getPhone()));
            }
        });


        food_description = findViewById(R.id.food_description);
        food_price = findViewById(R.id.food_price);

        foodimage = findViewById(R.id.img_food);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        recyclerView=findViewById(R.id.recycler_comment);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get foodId from intent
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {
            if (Common.isConnectToInternet(getBaseContext())) {
                getDetailFood(foodId);
                getRatingFood(foodId);
                listComment(foodId);
            } else {
                Toast.makeText(FoodDetail.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void listComment(String foodId) {
        Query query=ratingTbl.orderByChild("foodId").equalTo(foodId);

        FirebaseRecyclerOptions<Rating> options=new FirebaseRecyclerOptions.Builder<Rating>()
                .setQuery(query,Rating.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ShowCommentViewHolder holder, int position, @NonNull @NotNull Rating model) {
                holder.rating_Bar.setRating(Float.parseFloat(model.getRateValue()));
                holder.txtComment.setText(model.getComment());
                holder.txtName.setText(model.getName());
            }

            @NonNull
            @NotNull
            @Override
            public ShowCommentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_comment_layout,parent,false);
                return new ShowCommentViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very bad", "Not good", "Quite okay", "Very good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Food")
                .setDescription("Give your feedback")
                .setTitleTextColor(R.color.buttoncolor)
                .setDescriptionTextColor(R.color.buttoncolor)
                .setHint("Please leave yor comments here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.black)
                .setCommentBackgroundColor(R.color.buttoncolor)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood = snapshot.getValue(Food.class);

                Picasso.get().load(currentFood.getImage()).into(foodimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_description.setText(currentFood.getDescription());
                food_price.setText(String.format("₹ %s",currentFood.getPrice()));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }


    @Override
    public void onPositiveButtonClicked(int i, @NotNull String comments) {

        Rating rating=new Rating(
                Common.currentUser.getPhone(),
                Common.currentUser.getName(),
                foodId,
                String.valueOf(i),
                comments);
        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this, "Thanks",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
