package com.example.asal.morsechatproject.Model;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by asal on 2017-12-19.
 */
//used in manifests -> <application ..... android:name=".Model.MorseChat_Offline">.../>
public class MorseChat_Offline extends Application
{
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

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

        mAuth = FirebaseAuth.getInstance();
        //retrieve online user
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            String online_user_id = mAuth.getCurrentUser().getUid();

            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);

            mDatabaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

                    //determine to being online or offline whenever app is either minimize or open ,status is online
                    //mDatabaseReference.child("online").onDisconnect().setValue(false);
                    //mDatabaseReference.child("online").setValue(true);




                    //whenever app is either minimize or close , user's status becomes offline
                    mDatabaseReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
