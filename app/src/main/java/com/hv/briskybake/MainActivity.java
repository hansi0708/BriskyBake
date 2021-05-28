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

    EditText tbName, tbEmail, tbPhone, tbPassword;
    Button btnRegister, bSignIn;

    String verificationCodeBySystem;

    FirebaseAuth mFirebaseAuth;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        tbName = findViewById(R.id.TextNamesignup);
        tbEmail = findViewById(R.id.TextEmailsignup);
        tbPhone = findViewById(R.id.TextPhonesignup);
        tbPassword = findViewById(R.id.TextPasswordsignup);
        btnRegister = findViewById(R.id.btnsignup);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectToInternet(getBaseContext())) {

                    String email = tbEmail.getText().toString().trim();
                    String pwd = tbPassword.getText().toString().trim();
                    String name = tbName.getText().toString().trim();
                    String phone = tbPhone.getText().toString().trim();

                    if (email.isEmpty() && pwd.isEmpty() && name.isEmpty() && phone.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter the details", Toast.LENGTH_SHORT).show();
                    } else if (name.isEmpty()) {
                        tbName.setError("Please enter name");
                        tbName.requestFocus();
                    } else if (email.isEmpty()) {
                        tbEmail.setError("Please enter email");
                        tbEmail.requestFocus();
                    } else if (phone.isEmpty()) {
                        tbPhone.setError("Please enter phone number");
                        tbPhone.requestFocus();
                    } else if (phone.length() != 10) {
                        tbPhone.setError("Phone number should be of 10 digits");
                        tbPhone.requestFocus();
                    } else if (pwd.isEmpty()) {
                        tbPassword.setError("Please enter password");
                        tbPassword.requestFocus();
                    } else if (pwd.length() < 6) {
                        tbPassword.setError("Password should be atleast 6 characters");
                        tbPassword.requestFocus();
                    } else if (!(email.isEmpty() && pwd.isEmpty() && name.isEmpty() && phone.isEmpty())) {

                        showAlertDialog(phone);

                    } else {
                        Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bSignIn = findViewById(R.id.directtosignin);

        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

    }

    private void reload() {
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
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
        edtOTP.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC);
        alertDialog.setView(edtOTP);


        sendVerificationCodeToUser(phoneNo);

        alertDialog.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = edtOTP.getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    edtOTP.setError("Wrong OTP");
                    edtOTP.requestFocus();
                    return;
                }
           //     createAccount();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, code);
                String c=credential.getSmsCode();
                Toast.makeText(MainActivity.this, ""+c, Toast.LENGTH_SHORT).show();
                if (c.equals(code))
                {
                    createAccount(code);

                //    signInWithPhoneAuthCredential(credential);

                }

             //   signInWithPhoneAuthCredential(credential);

            }
        });

        alertDialog.setNegativeButton("RESEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Resending Code", Toast.LENGTH_SHORT).show();
                sendVerificationCodeToUser(phoneNo);
            }
        });

        alertDialog.show();

    }

    private void sendVerificationCodeToUser(String phoneNo) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mFirebaseAuth)
                        .setPhoneNumber("+91" + phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Toast.makeText(MainActivity.this, "Code sent "+s, Toast.LENGTH_SHORT).show();
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
          //  Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            Toast.makeText(MainActivity.this, "Verification completed", Toast.LENGTH_SHORT).show();

            //createAccount(code);

        //    signInWithPhoneAuthCredential(phoneAuthCredential);

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    };

    private void createAccount(String code) {

        String email = tbEmail.getText().toString().trim();
        String pwd = tbPassword.getText().toString().trim();
        String name = tbName.getText().toString().trim();
        String phone = tbPhone.getText().toString().trim();

        mFirebaseAuth.createUserWithEmailAndPassword(email,pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Created account", Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information
                            mFirebaseAuth.getCurrentUser();

                            User user = new User(name, email, phone);
                            FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, code);
                                        signInWithPhoneAuthCredential(credential);
                                    }
                                }
                            });
                            Log.d(TAG, "createUserWithEmail:success");
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

        Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).linkWithCredential(phoneAuthCredential)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Successfully linked emailLink credential!");
                                                Toast.makeText(MainActivity.this, "Linked account", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(MainActivity.this, Home.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                // You can access the new user via result.getUser()
                                                // Additional user info profile *not* available via:
                                                // result.getAdditionalUserInfo().getProfile() == null
                                                // You can check if the user is new or existing:
                                                // result.getAdditionalUserInfo().isNewUser()
                                            } else {
                                                Log.e(TAG, "Error linking emailLink credential", task.getException());
                                            }
                                        }
                                    });


    }


}