package com.example.asal.morsechatproject.Model;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by asal on 2017-12-26.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    private static final String TOKEN = "REG_TOKEN";
    @Override
    public void onTokenRefresh()
    {
        String current_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TOKEN,current_token);
    }
}
