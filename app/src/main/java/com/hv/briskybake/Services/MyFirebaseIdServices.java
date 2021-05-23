package com.hv.briskybake.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Model.Token;

public class MyFirebaseIdServices extends FirebaseMessagingService {

 //   @Override
    public void onNewToken(@NonNull Task<String> s) {
        super.onNewToken(String.valueOf(s));
        Log.d("NEW_TOKEN", String.valueOf(s));
        Task<String> token=s;
        if (Common.currentUser!=null)
            updateTokenToFirebase(token);
    }


    private void updateTokenToFirebase(Task<String> token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");

        Token data=new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }
}
