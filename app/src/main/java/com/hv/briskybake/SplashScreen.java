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
import com.hv.briskybake.Common.Common;

import java.util.Objects;

import io.paperdb.Paper;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        View v=getWindow().getDecorView();
        int u=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN;
        v.setSystemUiVisibility(u);

        setContentView(R.layout.splash_screen);

        mFirebaseAuth = FirebaseAuth.getInstance();

        //Init paper
        Paper.init(this);

        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        Thread thread = new Thread(){
            public void run(){
                try{
                    sleep(4*1000);
                    if (user == null && pwd == null) {
                        Intent i=new Intent(getBaseContext(),MainActivity.class);
                        startActivity(i);
                    }

                    finish();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

        };thread.start();

        if (user != null && pwd != null) {

            if (!user.isEmpty() && !pwd.isEmpty())
                login(user, pwd);
        }
    }

    private void login(String user, String pwd) {
        mFirebaseAuth.signInWithEmailAndPassword(user, pwd).addOnCompleteListener(SplashScreen.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SplashScreen.this, "Login error. Please login again", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(SplashScreen.this, Home.class);
                    Common.currentUser = mFirebaseAuth.getCurrentUser();
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}