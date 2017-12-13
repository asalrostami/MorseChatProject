package com.example.asal.morsechatproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "ERROR";
    private static final int gallery_pick = 1;

    private CircleImageView mCircleImageViewProfileImage;
    private TextView mTextViewUserName,mTextViewUserStatus;
    private Button mButtonChangeProfileImage,mButtonChangeStatus;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    private StorageReference mThumbImageStorageReference;

    Bitmap thumb_bitmap = null;

    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        String online_user_id = mAuth.getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        mThumbImageStorageReference = FirebaseStorage.getInstance().getReference().child("Thumb_Images");

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
        mButtonChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String old_status = mTextViewUserStatus.getText().toString();
                Intent statusIntent = new Intent(SettingsActivity.this,StatusActivity.class);
                statusIntent.putExtra("user_status",old_status);
                startActivity(statusIntent);

            }
        });

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
                    if(!image.equals("default_profile_image"))
                    {
                        Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.defaultimage1).resize(600, 600)
                                .centerCrop().into(mCircleImageViewProfileImage);
                    }

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
                mProgressDialog.setTitle("Updating Profile Image");
                mProgressDialog.setMessage("Please Wait ....");
                mProgressDialog.show();

                //uri of image which user chose
                Uri resultUri = result.getUri();

                //save image path
                File thumb_filePathUri = new File(resultUri.getPath());

                String user_id = mAuth.getCurrentUser().getUid();

                //compress the image by library -> implementation 'id.zelory:compressor:2.1.0'

                try
                {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200).setMaxHeight(200)
                            .setQuality(50).compressToBitmap(thumb_filePathUri);

                }catch (IOException e)
                {

                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();



                StorageReference filePath = mStorageReference.child(user_id + ".jpg");
                final StorageReference thumb_filePath = mThumbImageStorageReference.child(user_id + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this,"Saving your profile image",Toast.LENGTH_SHORT).show();


                            //save imageUrl into database of firebase ("user_image")
                           final String downloadUrl = task.getResult().getDownloadUrl().toString();


                           //save thumb image into the database
                           UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                           uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task)
                               {

                                   String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                   if(thumb_task.isSuccessful())
                                   {
                                       Map update_user_data = new HashMap();
                                       update_user_data.put("user_image",downloadUrl);
                                       update_user_data.put("user_tumb_image",thumb_downloadUrl);

                                       //update both images in database with using Map
                                       mDatabaseReference.updateChildren(update_user_data)
                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task)
                                                   {

                                                       Toast.makeText(SettingsActivity.this,"Profile Image Uploaded Successfully ...",Toast.LENGTH_SHORT).show();
                                                       mProgressDialog.dismiss();


                                                   }
                                               });
                                   }
                               }
                           });


                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this,"ERROR occured,while uploading your profile pic",Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                            mProgressDialog.dismiss();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}
