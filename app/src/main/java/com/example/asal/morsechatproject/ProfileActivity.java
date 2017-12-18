package com.example.asal.morsechatproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private Button mButtonSendReq,mButtonDeclineReq;
    private TextView mTextViewUserName,mTextViewStatus;
    private ImageView mImageViewVisitUser;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       // mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        String visit_user_id = getIntent().getExtras().get("visit_user_id").toString();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mTextViewUserName = (TextView)findViewById(R.id.tv_username_profile);
        mTextViewStatus = (TextView)findViewById(R.id.tv_status_profile);
        mImageViewVisitUser = (ImageView)findViewById(R.id.visit_userImage_profile);
        mButtonSendReq =(Button)findViewById(R.id.btn_sendRequest_profile);
        mButtonDeclineReq = (Button)findViewById(R.id.btn_declineRequest_profile);


        mDatabaseReference.child(visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                mTextViewUserName.setText(name);
                mTextViewStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultimage1).into(mImageViewVisitUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
