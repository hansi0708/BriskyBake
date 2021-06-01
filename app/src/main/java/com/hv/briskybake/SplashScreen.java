package com.hv.briskybake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Model.User;

import java.util.Objects;

import io.paperdb.Paper;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        View v=getWindow().getDecorView();
        int u=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN;
        v.setSystemUiVisibility(u);

        setContentView(R.layout.splash_screen);

        //Init Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        mFirebaseAuth = FirebaseAuth.getInstance();

        //Init paper
        Paper.init(this);

        String email = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        Thread thread = new Thread(){
            public void run(){
                    if (email == null && pwd == null) {
                        try {
                            sleep(3*1000);
                            Intent i=new Intent(getBaseContext(),MainActivity.class);
                            startActivity(i);
                            finish();
                        }catch(Exception e){
                                  e.printStackTrace();
                        }

                    }
                    else
                    {
                      //  assert email != null;
                      //  if (!email.isEmpty() && !pwd.isEmpty())
                        users.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SplashScreen.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        User user = snapshot.child(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid()).getValue(User.class);

                                        if (!task.isSuccessful()) {
                                            Toast.makeText(SplashScreen.this, "Login error. Please login again", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Intent i = new Intent(SplashScreen.this, Home.class);
                                            Common.currentUser =user;
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                });

                       //         login(email, pwd);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


            }

        };thread.start();
    }

   /// private void login(String email, String pwd) {


  //  }
}