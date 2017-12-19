package com.example.asal.morsechatproject.Model;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by asal on 2017-12-19.
 */
//used in manifests -> <application ..... android:name=".Model.MorseChat_Offline">.../>
public class MorseChat_Offline extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        //load the streams from database offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //load picture offline with Picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);

        Picasso.setSingletonInstance(built);
    }
}
