package com.hv.briskybake.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Model.Token;

public class MyFirebaseIdServices extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
        if (Common.currentUser!=null)
            updateTokenToFirebase(s);
    }

    private void updateTokenToFirebase(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");

        Token data=new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }
}
