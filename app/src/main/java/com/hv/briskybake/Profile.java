package com.hv.briskybake;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hv.briskybake.Common.Common;

public class Profile extends AppCompatActivity {

    TextView user_name,user_phone,user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_name=findViewById(R.id.name_user);
        user_email=findViewById(R.id.email_user);
        user_phone=findViewById(R.id.phone_user);

        user_name.setText(Common.currentUser.getName());
        user_email.setText(Common.currentUser.getEmail());
        user_phone.setText(Common.currentUser.getPhone());
    }
}