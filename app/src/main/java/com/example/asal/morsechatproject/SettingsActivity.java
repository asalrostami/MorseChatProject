package com.example.asal.morsechatproject;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "ERROR";
    private static final int gallery_pick = 1;

    private CircleImageView mCircleImageViewProfileImage;
    private TextView mTextViewUserName,mTextViewUserStatus;
    private Button mButtonChangeProfileImage,mButtonChangeStatus;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        String online_user_id = mAuth.getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");


        mCircleImageViewProfileImage = (CircleImageView)findViewById(R.id.settings_profile_circleImage);
        mTextViewUserName = (TextView)findViewById(R.id.tvUserName_settings);
        mTextViewUserStatus = (TextView)findViewById(R.id.tvStatus_settings);
        mButtonChangeProfileImage = (Button)findViewById(R.id.btn_changeImage_settings);
        mButtonChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery of phone
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,gallery_pick);
                //
            }
        });



        mButtonChangeStatus = (Button)findViewById(R.id.btn_changeStatus_settings);

        //retrieve information from database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()) {
                    //fetching information from database
                    // Log.e(TAG, "onComplete: Failed=" + dataSnapshot.getValue().toString());
                    String name = dataSnapshot.child("user_name").getValue().toString();
                    String status = dataSnapshot.child("user_status").getValue().toString();
                    String image = dataSnapshot.child("user_image").getValue().toString();
                    String thumb_image = dataSnapshot.child("user_tumb_image").getValue().toString();


                    mTextViewUserName.setText(name);
                    mTextViewUserStatus.setText(status);
                    Picasso.with(SettingsActivity.this).load(image).into(mCircleImageViewProfileImage);

                }
                else
                {
                    Log.e(TAG, "onComplete: Failed=");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
    //for copying the images from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == gallery_pick && resultCode == RESULT_OK && data!=null)
        {

            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        // get crop result
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();
                String user_id = mAuth.getCurrentUser().getUid();
                StorageReference filePath = mStorageReference.child(user_id + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this,"Saving your profile image",Toast.LENGTH_SHORT).show();
                            //save imageUrl into database of firebase ("user_image")
                            String downloadUrl = task.getResult().getDownloadUrl().toString();
                            mDatabaseReference.child("user_image").setValue(downloadUrl)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {

                                    Toast.makeText(SettingsActivity.this,"Profile Image Uploaded Successfully ...",Toast.LENGTH_SHORT).show();


                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this,"ERROR occured,while uploading your profile pic",Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}
