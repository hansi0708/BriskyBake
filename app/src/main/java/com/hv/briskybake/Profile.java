package com.hv.briskybake;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hv.briskybake.Common.Common;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class Profile extends AppCompatActivity {

    TextView user_name,user_phone,user_email;

    Button change_password,profileEdit;
    Button btnUpload,btnSelect;

    private static final String TAG = "ChangePassword";

    FirebaseAuth mFirebaseAuth;

    Uri saveUri;

    ImageView pic_user;

    FirebaseDatabase database;
    DatabaseReference user;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseAuth = FirebaseAuth.getInstance();

        final String email=Common.currentUser.getEmail();

        //Init Firebase
        database=FirebaseDatabase.getInstance();
        user= database.getReference("Users");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        user_name=findViewById(R.id.name_user);
        user_email=findViewById(R.id.email_user);
        user_phone=findViewById(R.id.phone_user);
        change_password=findViewById(R.id.btnChange);
        profileEdit=findViewById(R.id.edit_profile);
        pic_user=findViewById(R.id.user_pic);

        loadUser();




        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passResetEmail(email);
            }
        });

        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void loadUser() {
        user_name.setText(Common.currentUser.getName());
        user_email.setText(Common.currentUser.getEmail());
        user_phone.setText(Common.currentUser.getPhone());

        if (Common.currentUser.getImage()==null)
        {
            pic_user.setImageResource(R.drawable.ic_person);
        }
        else
            Picasso.get().load(Common.currentUser.getImage()).into(pic_user);

        //pic_user.setImageDrawable();
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Profile.this);
        alertDialog.setTitle("Update Profile Picture ");
        alertDialog.setMessage("Choose an image :");

        LayoutInflater inflater=this.getLayoutInflater();
        View profile_update_layout=inflater.inflate(R.layout.profile_picture_update,null);

        btnSelect = profile_update_layout.findViewById(R.id.btnSelect);
        btnUpload = profile_update_layout.findViewById(R.id.btnUpload);

        //Event for Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();   //let user select Image from gallery and save Url of the image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    uploadImage();
            }
        });

        alertDialog.setView(profile_update_layout);

        //set Button
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                user.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(Common.currentUser);

                Toast.makeText(Profile.this, "Uploaded!!", Toast.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               dialog.dismiss();

            }
        });
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected");
        }
    }

    private void uploadImage() {
        if (saveUri != null)
        {
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("image/*"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(Profile.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //set value for newCategory if image upload and we can get Download link
                           // newCategory = new Category(edtName.getText().toString(),uri.toString());
                            Common.currentUser.setImage(uri.toString());

                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Profile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            int progress = (int) (100.0 *snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+"%");

                        }
                    });

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
    }

    private void passResetEmail(String email) {
        if(mFirebaseAuth != null) {
            Log.w(" if Email authenticated", "Recovery Email has been  sent to your mail.");
            mFirebaseAuth.sendPasswordResetEmail(email);
        } else {
            Log.w(" error ", " bad entry ");
        }
    }

    @Override
    protected void onResume() {
        loadUser();
        super.onResume();
    }
}