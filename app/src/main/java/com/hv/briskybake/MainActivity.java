package com.hv.briskybake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText tbname,tbemail,tbphone,tbpassword;
    Button btnRegister,bsignin;

    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth=FirebaseAuth.getInstance();

        tbname=findViewById(R.id.TextNamesignup);
        tbemail=findViewById(R.id.TextEmailsignup);
        tbphone=findViewById(R.id.TextPhonesignup);
        tbpassword=findViewById(R.id.TextPasswordsignup);
        btnRegister=findViewById(R.id.btnsignup);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=tbemail.getText().toString().trim();
                String pwd=tbpassword.getText().toString().trim();
                String name=tbname.getText().toString().trim();
                String phone=tbphone.getText().toString().trim();

                if(email.isEmpty() && pwd.isEmpty() && name.isEmpty() && phone.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please enter the details", Toast.LENGTH_SHORT).show();
                }
                else if(name.isEmpty())
                {
                    tbname.setError("Please enter name");
                    tbname.requestFocus();
                }
                else if(email.isEmpty())
                {
                    tbemail.setError("Please enter email");
                    tbemail.requestFocus();
                }
                else if(phone.isEmpty())
                {
                    tbphone.setError("Please enter phone number");
                    tbphone.requestFocus();
                }
                else if(phone.length()!=10)
                {
                    tbphone.setError("Phone number should be of 10 digits");
                    tbphone.requestFocus();
                }
                else if(pwd.isEmpty())
                {
                    tbpassword.setError("Please enter password");
                    tbpassword.requestFocus();
                }
                else if(pwd.length()<6)
                {
                    tbpassword.setError("Password should be atleast 6 characters");
                    tbpassword.requestFocus();
                }
                else if(!(email.isEmpty() && pwd.isEmpty() && name.isEmpty() && phone.isEmpty()))
                {
                    Intent intent=new Intent(MainActivity.this,VerifyPhoneNo.class);
                    intent.putExtra("phone",phone);
                    startActivity(intent);

                  //  mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                   //     @Override
                   //     public void onComplete(@NonNull Task<AuthResult> task) {
                    //        if (!task.isSuccessful()) {
                    //            Toast.makeText(MainActivity.this, "Sign up unsuccessful", Toast.LENGTH_SHORT).show();
                    //        }
                   //         else
                    //        {
                      //          User user=new User(name,email,phone);
                      //          FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                      //              @Override
                      //              public void onComplete(@NonNull Task<Void> task) {
                      //                  if(task.isSuccessful())
                       //                 {
                      //                      Toast.makeText(MainActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                       //                 }
                       //             }
                       //         });
                               // startActivity(new Intent(MainActivity.this, SplashScreen.class));
                              //  finish();
                        //    }
                      //  }
                   // });
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bsignin=findViewById(R.id.directtosignin);

        bsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Login.class));
            }
        });
    }
}