package com.hv.briskybake;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Model.User;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText tbname,tbemail,tbphone,tbpassword;
    Button btnRegister,bsignin;

    String verifictionCodeBySystem;

    FirebaseAuth mFirebaseAuth;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        tbname = findViewById(R.id.TextNamesignup);
        tbemail = findViewById(R.id.TextEmailsignup);
        tbphone = findViewById(R.id.TextPhonesignup);
        tbpassword = findViewById(R.id.TextPasswordsignup);
        btnRegister = findViewById(R.id.btnsignup);



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectToInternet(getBaseContext())) {

                    String email = tbemail.getText().toString().trim();
                    String pwd = tbpassword.getText().toString().trim();
                    String name = tbname.getText().toString().trim();
                    String phone = tbphone.getText().toString().trim();

                    if (email.isEmpty() && pwd.isEmpty() && name.isEmpty() && phone.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter the details", Toast.LENGTH_SHORT).show();
                    } else if (name.isEmpty()) {
                        tbname.setError("Please enter name");
                        tbname.requestFocus();
                    } else if (email.isEmpty()) {
                        tbemail.setError("Please enter email");
                        tbemail.requestFocus();
                    } else if (phone.isEmpty()) {
                        tbphone.setError("Please enter phone number");
                        tbphone.requestFocus();
                    } else if (phone.length() != 10) {
                        tbphone.setError("Phone number should be of 10 digits");
                        tbphone.requestFocus();
                    } else if (pwd.isEmpty()) {
                        tbpassword.setError("Please enter password");
                        tbpassword.requestFocus();
                    } else if (pwd.length() < 6) {
                        tbpassword.setError("Password should be atleast 6 characters");
                        tbpassword.requestFocus();
                    } else if (!(email.isEmpty() && pwd.isEmpty() && name.isEmpty() && phone.isEmpty())) {

                        showAlertDialog(phone);


                    } else {
                        Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        bsignin = findViewById(R.id.directtosignin);

        bsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });


    }

    private void reload() { }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void showAlertDialog(String phoneNo) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Phone no. Verification");
        alertDialog.setMessage("Enter your OTP: ");

        final EditText edtOTP = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtOTP.setLayoutParams(lp);
        alertDialog.setView(edtOTP);
        edtOTP.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC);

        sendVerificationCodeToUser(phoneNo);

        alertDialog.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code=edtOTP.getText().toString();

                if(code.isEmpty()||code.length()<6)
                {
                    edtOTP.setError("Wrong OTP");
                    edtOTP.requestFocus();
                    return;}
                verifyCode(code);
            }
        });

        alertDialog.setNegativeButton("RESEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendVerificationCodeToUser(phoneNo);
            }
        });
        alertDialog.show();

    }

    private void sendVerificationCodeToUser(String phoneNo) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mFirebaseAuth)
                        .setPhoneNumber("+91"+phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verifictionCodeBySystem =s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code!=null)
            {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser){
      //  PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verifictionCodeBySystem,codeByUser);
        AuthCredential credential=PhoneAuthProvider.getCredential(tbphone.getText().toString(),tbpassword.getText().toString());
        signInTheUserByCredentials(credential);
    }



    private void signInTheUserByCredentials(AuthCredential credential) {

        String email = tbemail.getText().toString().trim();
        String pwd = tbpassword.getText().toString().trim();

        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser Fuser = mFirebaseAuth.getCurrentUser();
                            String email = tbemail.getText().toString().trim();
                            // String pwd = tbpassword.getText().toString().trim();
                            String name = tbname.getText().toString().trim();
                            String phone = tbphone.getText().toString().trim();

                            User user=new User(name,email,phone);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(MainActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            Intent intent=new Intent(MainActivity.this,Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                //            updateUI(Fuser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
               //             updateUI(null);
                        }
                    }
                });


        Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}