package com.hv.briskybake;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.Model.User;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    EditText tbname, tbemail, tbphone, tbpassword;
    Button btnRegister, bsignin;

    String verifictionCodeBySystem;

    FirebaseAuth mFirebaseAuth;

    private String getLatitude="";
    private String getLongitude="";

    private static final String TAG = "EmailPassword";

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        mFirebaseAuth = FirebaseAuth.getInstance();

        //Init Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        Paper.init(this);

        tbname = findViewById(R.id.TextNamesignup);
        tbemail = findViewById(R.id.TextEmailsignup);
        tbphone = findViewById(R.id.TextPhonesignup);
        tbpassword = findViewById(R.id.TextPasswordsignup);
        btnRegister = findViewById(R.id.btnsignup);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectToInternet(getBaseContext())) {

                    Paper.book().write(Common.USER_KEY,tbemail.getText().toString());
                    Paper.book().write(Common.PWD_KEY,tbpassword.getText().toString());

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

    private void getlocation() {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (network_enabled)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gps_loc != null && net_loc != null) {

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;
            Log.e("MainActivity", "getlocation: "+ net_loc.getLatitude()+" | "+net_loc.getLongitude());
            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)
        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }

        getLatitude=finalLoc.getLatitude()+"";
        getLongitude=finalLoc.getLongitude()+"";
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
        String pwd = tbpassword.getText().toString().trim();
        String name = tbname.getText().toString().trim();
        String phone = tbphone.getText().toString().trim();

        getlocation();
        Log.e(TAG, "signInWithPhoneAuthCredential: getLongitude:"+getLongitude+", getLatitude:"+getLatitude );

        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                         //   FirebaseUser user = mAuth.getCurrentUser();
                            Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).linkWithCredential(phoneAuthCredential)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Successfully linked emailLink credential!");
                                                User user = new User(name, email, phone,getLatitude,getLongitude);

                                                FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(MainActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                                                            login(email,pwd);
                                                        }
                                                    }
                                                });

                                            } else {
                                                Log.e(TAG, "Error linking emailLink credential", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed."+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }
                    }
                });


    }

    private void login(String email, String pwd)
         {
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Login error. Please login again", Toast.LENGTH_SHORT).show();
                            } else {
                                User user = snapshot.child(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid()).getValue(User.class);

                                Intent i = new Intent(MainActivity.this, Home.class);
                                Common.currentUser =user;
                                startActivity(i);
                                finish();
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifictionCodeBySystem, codeByUser);
        signInWithPhoneAuthCredential(credential);
    }

}