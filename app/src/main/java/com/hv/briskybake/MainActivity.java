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
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Model.User;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText tbname, tbemail, tbphone, tbpassword;
    Button btnRegister, bsignin;

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
        alertDialog.setView(edtOTP);
        edtOTP.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC);

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
            super.onCodeSent(s, forceResendingToken);
            verifictionCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // signInWithPhoneAuthCredential(phoneAuthCredential);

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

        String email = tbemail.getText().toString().trim();

        mFirebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            sendLink(email);

                            linkAccount();


                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void linkAccount() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String emailLink = intent.getData().toString();
        String email = tbemail.getText().toString().trim();

        linkWithSignInLink(email,emailLink);
    }

    public void linkWithSignInLink(String email, String emailLink) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // [START auth_link_with_link]
        // Construct the email link credential from the current URL.
        AuthCredential credential =
                EmailAuthProvider.getCredentialWithLink(email, emailLink);

       // String email = tbemail.getText().toString().trim();
       // String pwd = tbpassword.getText().toString().trim();
        String name = tbname.getText().toString().trim();
        String phone = tbphone.getText().toString().trim();

        // Link the credential to the current user.
        auth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Successfully linked emailLink credential!");
                            AuthResult result = task.getResult();
                            User user = new User(name, email, phone);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

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
        // [END auth_link_with_link]
    }


    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifictionCodeBySystem, codeByUser);
        signInWithPhoneAuthCredential(credential);
    }

    public void sendLink(String email) {
        ActionCodeSettings actionCodeSettings =
            ActionCodeSettings.newBuilder()
                    // URL you want to redirect back to. The domain (www.example.com) for this
                    // URL must be whitelisted in the Firebase Console.
                    .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                    // This must be true
                    .setHandleCodeInApp(true)
                    .setIOSBundleId("com.example.ios")
                    .setAndroidPackageName(
                            "com.example.android",
                            true, /* installIfNotAvailable */
                            "12"    /* minimumVersion */)
                    .build();
        sendSignInLink(email,actionCodeSettings);

    }

    public void sendSignInLink(String email, ActionCodeSettings actionCodeSettings) {
        // [START auth_send_sign_in_link]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
        // [END auth_send_sign_in_link]
    }

}