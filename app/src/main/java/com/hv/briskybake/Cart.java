package com.hv.briskybake;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.hv.briskybake.Model.Order;
import com.hv.briskybake.Model.Request;
import com.hv.briskybake.ViewHolder.CartAdapter;
import com.hv.briskybake.ViewHolder.CartViewHolder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    public TextView txtTotalPrice;
    int total=0;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    Button btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;


    RelativeLayout rootLayout;
    //root_cart,empty_cart_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        rootLayout = findViewById(R.id.rootLayout);
        //  root_cart=findViewById(R.id.root_cart);
        //  empty_cart_layout=findViewById(R.id.empty_cart_layout);

        //     LayoutInflater inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,null);

        //// LayoutInflater inflater = LayoutInflater
        //     .from(getApplicationContext());
        //    @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.empty_cart, null);

        //   final View view = getLayoutInflater().inflate(R.layout.empty_cart,null);
        // myLayout.addView(hiddenInfo);


        //       root_cart.addView(view);

        //   {
        //     root_cart.removeView(view);
        //}

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        Checkout.preload(getApplicationContext());
        //      Checkout.setKeyID("rzp_test_HCFAKIoVPEq0lb");

        //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        txtTotalPrice = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlace);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your Cart is Empty!!", Toast.LENGTH_SHORT).show();
            }
        });

        loadListFood();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //  final View view = getLayoutInflater().inflate(R.layout.empty_cart,null);

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Step!");
        alertDialog.setMessage("Enter your Address: ");


        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        EditText tbaddress = order_address_comment.findViewById(R.id.taddress);
        EditText tbcomment = order_address_comment.findViewById(R.id.tcomment);

        alertDialog.setView(order_address_comment);

        alertDialog.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Create new Request
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        tbaddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        tbcomment.getText().toString(),
                        cart
                );

                //Submit to Firebase
                //We will using System.CurrentMilli to key
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);
                //Delete cart

                Intent intent = new Intent(Cart.this, PayUMoneyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", Common.currentUser.getName());
                bundle.putString("email", Common.currentUser.getEmail());
                bundle.putString("productInfo", "Payment for "+ getString(R.string.app_name));
                bundle.putString("phone", Common.currentUser.getPhone());
                bundle.putString("amount", total+"");
                //      bundle.putString("amount", "1");
                intent.putExtras(bundle);
                startActivityForResult(intent, 1234);
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void loadListFood() {
        //  final View view = getLayoutInflater().inflate(R.layout.empty_cart,null);

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        //calculate total price
        total = 0;
        for (Order order : cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for (Order item : cart)
            new Database(this).addToCart(item);
        loadListFood();
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAbsoluteAdapterPosition()).getProductName();
            Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAbsoluteAdapterPosition());
            int deleteIndex = viewHolder.getAbsoluteAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId());

            //calculate total price
            total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts();
            for (Order item : orders)
                total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("en", "IN");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));

            //Make Snackbar
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), name + " removed from Cart!!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    //calculate total price
                    total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts();
                    for (Order item : orders)
                        total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                    Locale locale = new Locale("en", "IN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                    txtTotalPrice.setText(fmt.format(total));

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

            loadListFood();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Cart", "request code " + requestCode + " resultcode " + resultCode);

        if (requestCode == 1234) {
            if (data != null) {
                String message = data.getStringExtra("MESSAGE");
                if (message != null) {
                    if (message.equalsIgnoreCase("success")) {
                        String s = data.getStringExtra("oid");
                        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Payment Done!")
                                .setContentText("Order ID : "+s)
                                .setConfirmText("ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        startActivity(new Intent(Cart.this,Home.class));
                                        sDialog.dismissWithAnimation();
                                        finish();
                                    }
                                })
                                .show();
                        new Database(getBaseContext()).cleanCart();

                        Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String s = data.getStringExtra("oid");
                        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Payment failed!")
                                .setContentText("Oops, Something Went Wrong..")
                                .setConfirmText("ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        startActivity(new Intent(Cart.this,Home.class));
                                        sDialog.dismissWithAnimation();
                                        finish();
                                    }
                                })
                                .show();
                    }
                    Toast.makeText(this, "Payment Declined, Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}