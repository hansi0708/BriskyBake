package com.hv.briskybake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetPassword extends AppCompatActivity {

    Button forgetButton;
    EditText txtEmail;
    String femail;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        auth=FirebaseAuth.getInstance();

        txtEmail=findViewById(R.id.TextEmailFP);
        forgetButton=findViewById(R.id.btnforgetpassword);

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatData();
            }
        });
    }

    private void validatData() {

        femail=txtEmail.getText().toString();
        if(femail.isEmpty())
        {
            txtEmail.setError("Required");
        }else{
            forgetPass();
        }
    }

    private void forgetPass() {
        auth.sendPasswordResetEmail(femail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgetPassword.this, "Check your mail", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgetPassword.this,Login.class));
                    finish();
                }else {
                    Toast.makeText(ForgetPassword.this, "Error"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}