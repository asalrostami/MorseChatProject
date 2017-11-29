package com.example.asal.morsechatproject.Model;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by asal on 2017-11-29.
 */

public class Brain {
    public void showToast(String message, Context context)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

}
