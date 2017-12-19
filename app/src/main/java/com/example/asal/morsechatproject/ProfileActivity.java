package com.example.asal.morsechatproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private DatabaseReference friendsRequestRefrence;
    private FirebaseAuth mAuth;

    private String CURRENT_STATE;
    String sender_user_id;
    String receiver_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendsRequestRefrence = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");

        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();


        mTextViewUserName = (TextView)findViewById(R.id.tv_username_profile);
        mTextViewStatus = (TextView)findViewById(R.id.tv_status_profile);
        mImageViewVisitUser = (ImageView)findViewById(R.id.visit_userImage_profile);
        mButtonSendReq =(Button)findViewById(R.id.btn_sendRequest_profile);
        mButtonDeclineReq = (Button)findViewById(R.id.btn_declineRequest_profile);

        CURRENT_STATE = "not_friends";

        mDatabaseReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                mTextViewUserName.setText(name);
                mTextViewStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultimage1).into(mImageViewVisitUser);

                friendsRequestRefrence.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild(receiver_user_id))
                        {
                            String request_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();
                            if(request_type.equals("sent"))
                            {
                                CURRENT_STATE = "request_sent";
                                mButtonSendReq.setText("Cancel Friend Request");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mButtonSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mButtonSendReq.setEnabled(false);
                if(CURRENT_STATE.equals("not_friends"))
                {
                    mButtonSendRequestToAPerson();
                }
                else if(CURRENT_STATE.equals("request_sent"))
                {
                    CancelFriendRequest();
                }
            }
        });
    }



    private void CancelFriendRequest()
    {

        friendsRequestRefrence.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            friendsRequestRefrence.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> receiveTask)
                                        {

                                            if(receiveTask.isSuccessful())
                                            {
                                                mButtonSendReq.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                mButtonSendReq.setText("Send Friend Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void mButtonSendRequestToAPerson()
    {

        friendsRequestRefrence.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    friendsRequestRefrence.child(receiver_user_id).child(sender_user_id)
                            .child("request_type").setValue("received")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> receiveTask)
                        {
                            if(receiveTask.isSuccessful())
                            {
                                mButtonSendReq.setEnabled(true);
                                CURRENT_STATE = "request_sent";
                                mButtonSendReq.setText("Cancel Friend Request");
                            }
                        }
                    });
                }

            }
        });
    }
}
