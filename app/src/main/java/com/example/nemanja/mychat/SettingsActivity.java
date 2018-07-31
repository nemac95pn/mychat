package com.example.nemanja.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity
{
    private CircleImageView settingsDisplayProfileImage;
    private TextView settingsDisplayname;
    public static TextView settingsDisplayStatus;
    private Button settingsChangeProfileImageButton;
    private Button settingsChangeStatusButton;

    private final static int Gallery_Pick = 1;
    private StorageReference storageProfileImageStorageRef;

    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;

    Bitmap thumb_bitmap = null;

    private StorageReference thumbImageRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        String online_user_id = mAuth.getCurrentUser().getUid();

        getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        getUserDataReference.keepSynced(true);

        storageProfileImageStorageRef = FirebaseStorage.getInstance().getReference().child("Profile_images");

        thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");

        settingsDisplayProfileImage =  findViewById(R.id.settings_profile_image);
        settingsDisplayname =  findViewById(R.id.settings_username);
        settingsDisplayStatus =  findViewById(R.id.settings_user_status);
        settingsChangeProfileImageButton =  findViewById(R.id.settings_change_profile_image_button);
        settingsChangeStatusButton =  findViewById(R.id.settings_change_profile_status);
        loadingBar = new ProgressDialog(this);


        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                settingsDisplayname.setText(name);
                settingsDisplayStatus.setText(status);
                if (!image.equals("default_profile"))
                {
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile).into(settingsDisplayProfileImage, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_profile).into(settingsDisplayProfileImage);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        settingsChangeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        settingsChangeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String old_status = settingsDisplayStatus.getText().toString();

                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("user_status", old_status);
                startActivity(statusIntent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Updating Profile Image");
                loadingBar.setMessage("Please wait, while we are updating proflile image...");
                loadingBar.show();

                Uri resultUri = result.getUri();

                File thumb_filePathUri = new File(resultUri.getPath());

                String user_id = mAuth.getCurrentUser().getUid();

                try
                {
                    thumb_bitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(50).compressToBitmap(thumb_filePathUri);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                StorageReference filePath = storageProfileImageStorageRef.child(user_id + ".jpg");

                final StorageReference thumb_filePath = thumbImageRef.child(user_id + ".jpg");


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "Saving your profile image to Firebase Storage...", Toast.LENGTH_LONG).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);

                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task)
                                {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (task.isSuccessful())
                                    {
                                        Map update_user_data = new HashMap();
                                        update_user_data.put("user_image", downloadUrl);
                                        update_user_data.put("user_thumb_image", thumb_downloadUrl);


                                        getUserDataReference.updateChildren(update_user_data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                Toast.makeText(SettingsActivity.this, "Profile Image Updated Sucessfully.", Toast.LENGTH_SHORT).show();

                                                loadingBar.dismiss();
                                            }
                                        });

                                    }
                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this, "Error occured, while uploading you profile picture!", Toast.LENGTH_SHORT).show();

                            loadingBar.dismiss();
                        }
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
