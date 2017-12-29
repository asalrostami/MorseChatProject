package com.example.asal.morsechatproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity
{
    private String messageUserId;
    private String messageUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageUserId = getIntent().getExtras().get("visit_user_id").toString();
        messageUserName = getIntent().getExtras().get("user_name").toString();
    }
}
