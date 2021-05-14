package com.hv.briskybake.Services;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Model.Token;

public class MyFirebaseIdServices extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
      //  String tokenRefreshed= FirebaseMessaging.getToken();
        updateTokenToFirebase();
    }

    private void updateTokenToFirebase() {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token=new Token(FirebaseMessaging.getInstance().getToken(),false);
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
