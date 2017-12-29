package com.example.asal.morsechatproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.asal.morsechatproject.Model.LastSeenTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
    private String messageUserId;
    private String messageUserName;
    private Toolbar mToolbar;

    private TextView mTextViewUserNameTitle,mTextViewUserLastSeen;
    private CircleImageView mCircleImageView;

    private DatabaseReference rootRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();


        messageUserId = getIntent().getExtras().get("visit_user_id").toString();
        messageUserName = getIntent().getExtras().get("user_name").toString();

        mToolbar = (Toolbar)findViewById(R.id.chat_bar_layout);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        //add back button on the toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar,null);

        actionBar.setCustomView(action_bar_view);

        mTextViewUserNameTitle = (TextView)findViewById(R.id.tv_userName_chatBar);
        mTextViewUserLastSeen = (TextView)findViewById(R.id.tv_last_seen_chatBar);
        mCircleImageView = (CircleImageView)findViewById(R.id.image_chatBar);

        mTextViewUserNameTitle.setText(messageUserName);

        rootRef.child("Users").child(messageUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                final String online = dataSnapshot.child("online").getValue().toString();
                final String thumbImage = dataSnapshot.child("user_tumb_image").getValue().toString();

                if(!thumbImage.equals("2131165298"))
                {
                    Picasso.with(ChatActivity.this).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).resize(600, 600)
                            .centerCrop().placeholder(R.drawable.defaultimage1).into(mCircleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(ChatActivity.this).load(thumbImage).resize(600, 600)
                                    .centerCrop().placeholder(R.drawable.defaultimage1).into(mCircleImageView);
                        }
                    });
                }

                if(online.equals("true"))
                {

                    mTextViewUserLastSeen.setText("online");
                }
                else
                {

                    LastSeenTime lastSeenTime = new LastSeenTime();
                    Long last_seen = Long.parseLong(online);
                    String lastSeenDisplayTime = lastSeenTime.getTimeAgo(last_seen,getApplicationContext()).toString();

                    mTextViewUserLastSeen.setText(lastSeenDisplayTime);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


    }
}
