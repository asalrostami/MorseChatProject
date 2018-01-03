package com.example.asal.morsechatproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.asal.morsechatproject.Model.LastSeenTime;
import com.example.asal.morsechatproject.Model.Message;
import com.example.asal.morsechatproject.Model.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
    private String messageReceiverId;
    private String messageUserName;
    private Toolbar mToolbar;

    private TextView mTextViewUserNameTitle,mTextViewUserLastSeen;
    private CircleImageView mCircleImageView;

    private ImageButton mImageButtonSelectImage,mImageButtonSendMessage;
    private EditText mEditTextMessage;
    private RecyclerView userMessagesList;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String messageSenderId;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private MessageAdapter mMessageAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();


        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messageUserName = getIntent().getExtras().get("user_name").toString();

        mToolbar = (Toolbar)findViewById(R.id.chat_bar_layout);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        //add back button on the toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar,null);

        actionBar.setCustomView(action_bar_view);

        mTextViewUserNameTitle = (TextView)findViewById(R.id.tv_userName_chatBar);
        mTextViewUserLastSeen = (TextView)findViewById(R.id.tv_last_seen_chatBar);
        mCircleImageView = (CircleImageView)findViewById(R.id.image_chatBar);

        mEditTextMessage = (EditText)findViewById(R.id.et_write_message_chat) ;
        mImageButtonSelectImage = (ImageButton)findViewById(R.id.btn_select_image_chat) ;
        mImageButtonSendMessage = (ImageButton)findViewById(R.id.btn_send_message_chat);

        mMessageAdapter = new MessageAdapter(messageList);

        userMessagesList = (RecyclerView)findViewById(R.id.recyclerView_messagesList_chat) ;


        mLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(mLayoutManager);

        userMessagesList.setAdapter(mMessageAdapter);

        FetchMessages();


        rootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                final String online = dataSnapshot.child("online").getValue().toString();
                final String thumbImage = dataSnapshot.child("user_tumb_image").getValue().toString();

                //Picasso.with(ChatActivity.this).load(thumbImage).centerCrop().placeholder(R.drawable.defaultimage1).into(mCircleImageView);
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

                mTextViewUserNameTitle.setText(messageUserName);
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


        mImageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               SendMessage();

            }
        });



    }

    private void FetchMessages()
    {
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {

                        Message message = dataSnapshot.getValue(Message.class);
                        messageList.add(message);
                        mMessageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage()
    {
        String messageText = mEditTextMessage.getText().toString();

        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(ChatActivity.this,"Please write your message.",Toast.LENGTH_SHORT).show();
        }
        else
        {

            String message_sender_ref = "Messages/"+ messageSenderId + "/" + messageReceiverId;
            String message_receiver_ref = "Messages/"+ messageReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId)
                                                                          .child(messageReceiverId).push();

            String message_push_id = user_message_key.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("seen",false);
            messageTextBody.put("type","text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from",messageSenderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref + "/" + message_push_id ,messageTextBody);
            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id ,messageTextBody);

            rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    if(databaseError != null)
                    {
                        Log.d("Chat_Log",databaseError.getMessage().toString());
                    }

                    mEditTextMessage.setText("");
                }
            });
        }


    }
}
