package com.hv.briskybake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hv.briskybake.Common.Common;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    EditText temail,tpassword;
    Button btnSignIn;
    Button bsignup;
    Button bfp;

    CheckBox ckbRemember;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth=FirebaseAuth.getInstance();

        bsignup=findViewById(R.id.directtosignup);
        bfp=findViewById(R.id.directtofrgpass);

        temail=findViewById(R.id.TextEmailsignin);
        tpassword=findViewById(R.id.TextPasswordsignin);
        btnSignIn=findViewById(R.id.btnsignin);

        ckbRemember=findViewById(R.id.ckbRemember);

        Paper.init(this);

        mAuthStateListener=new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser mFirebaseUser=firebaseAuth.getCurrentUser();

                if(mFirebaseUser!=null)
                {
                    Toast.makeText(Login.this, "You are Logged in", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Login.this,Home.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Login.this, "Please log in", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectToInternet(getBaseContext())) {

                    //Save user & password
                    if(ckbRemember.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,temail.getText().toString());
                        Paper.book().write(Common.PWD_KEY,tpassword.getText().toString());
                    }

                    String email = temail.getText().toString().trim();
                    String pwd = tpassword.getText().toString().trim();

                    if (email.isEmpty() && pwd.isEmpty()) {
                        Toast.makeText(Login.this, "Please enter the details", Toast.LENGTH_SHORT).show();
                    } else if (email.isEmpty()) {
                        temail.setError("Please enter email");
                        temail.requestFocus();
                    } else if (pwd.isEmpty()) {
                        tpassword.setError("Please enter password");
                        tpassword.requestFocus();
                    } else if (pwd.length() < 6) {
                        tpassword.setError("Password should be atleast 6 characters");
                        tpassword.requestFocus();
                    } else if (!(email.isEmpty() && pwd.isEmpty())) {
                        mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Login error. Please login again", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent i = new Intent(Login.this, Home.class);
                                    Common.currentUser = mFirebaseAuth.getCurrentUser();
                                    startActivity(i);
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Login.this, "Error occured", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Login.this, "Please checck your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        bfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,ForgetPassword.class);
                startActivity(intent);
            }
        });

        bsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
}