package com.hv.briskybake;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Interface.ItemClickListener;
import com.hv.briskybake.Model.Category;
import com.hv.briskybake.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;

    TextView textfullname;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        //Init Firebase
        database=FirebaseDatabase.getInstance();
        category= database.getReference("Category");

        Paper.init(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent=new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //Set Name for User
        View headerView=navigationView.getHeaderView(0);
        textfullname=headerView.findViewById(R.id.textFullName);
        FirebaseUser user= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        textfullname.setText(user.getDisplayName());

        //Load Menu
        recycler_menu=findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        if(Common.isConnectToInternet(getBaseContext()))
            loadMenu();
        else{
            Toast.makeText(Home.this, "Please checck your connection", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void loadMenu() {
        FirebaseRecyclerOptions<Category> options=new FirebaseRecyclerOptions.Builder<Category>().setQuery(category,Category.class).build();
        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                holder.textMenuName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
              //  final Category clickItem=model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get CategoryId and send to New Activity
                        Intent list=new Intent(Home.this,List.class);
                        //Because CategoryId is Key, so we take key of the item
                        list.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(list);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(view);
            }
        };

        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),1);
        recycler_menu.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer=findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
           drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.nav_refresh) {
            loadMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.nav_menu){

        }else if(id==R.id.nav_orders){
            Intent orderIntent=new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);
        }else if(id==R.id.nav_cart){
            Intent cartIntent=new Intent(Home.this,Cart.class);
            startActivity(cartIntent);
        }else if(id==R.id.nav_logout){
            //Delete Remember user & pwd
            Paper.book().destroy();
            //Logout
            Intent logoutIntent=new Intent(Home.this,Login.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        DrawerLayout drawer=findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}