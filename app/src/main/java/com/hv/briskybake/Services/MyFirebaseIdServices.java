package com.hv.briskybake.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Model.Token;

public class MyFirebaseIdServices extends FirebaseMessagingService {

    public void onNewToken(@NonNull Task<String> s) {
        super.onNewToken(String.valueOf(s));
        Task<String> tokenRefreshed= s;
        if (Common.currentUser!=null)
            updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(Task<String> tokenRefreshed) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token=new Token(tokenRefreshed,false);
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
