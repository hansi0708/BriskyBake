package com.hv.briskybake;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hv.briskybake.Common.Common;

public class Profile extends AppCompatActivity {

    TextView user_name,user_phone,user_email;

    Button change_password;

    private static final String TAG = "ChangePassword";

    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseAuth = FirebaseAuth.getInstance();

        final String email=Common.currentUser.getEmail();

        user_name=findViewById(R.id.name_user);
        user_email=findViewById(R.id.email_user);
        user_phone=findViewById(R.id.phone_user);
        change_password=findViewById(R.id.btnChange);

        user_name.setText(Common.currentUser.getName());
        user_email.setText(Common.currentUser.getEmail());
        user_phone.setText(Common.currentUser.getPhone());

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passResetEmail(email);
            }
        });
    }

    private void passResetEmail(String email) {
        if(mFirebaseAuth != null) {
            Log.w(" if Email authenticated", "Recovery Email has been  sent to your mail.");
            mFirebaseAuth.sendPasswordResetEmail(email);
        } else {
            Log.w(" error ", " bad entry ");
        }
    }
}