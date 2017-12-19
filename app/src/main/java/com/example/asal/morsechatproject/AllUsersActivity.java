package com.example.asal.morsechatproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.asal.morsechatproject.Model.AllUsers;
import com.example.asal.morsechatproject.Model.MyDividerItemDecoration;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        //allowed to work offline
        mDatabaseReference.keepSynced(true);

        mToolbar = (Toolbar)findViewById(R.id.allUsers_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView)findViewById(R.id.allUsers_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //add divider in recyclerView by default
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //add divider in recyclerView by custom divider
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));





    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>
                (
                        AllUsers.class,
                        R.layout.all_users_display_layout,
                        AllUsersViewHolder.class,
                        mDatabaseReference
                )
        {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position)
            {

                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_tumb_image(getApplicationContext(),model.getUser_tumb_image());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        //retrieve the user's unique_id from database
                        String visit_user_id = getRef(position).getKey();
                        Intent profileIntent = new Intent(AllUsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        // set the adapter
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setUser_name(String user_name) {
            TextView userName = (TextView)mView.findViewById(R.id.tv_allUsers_userName);
            userName.setText(user_name);
        }
        public void setUser_status(String user_status) {
            TextView status = (TextView)mView.findViewById(R.id.tv_allUsers_status);
            status.setText(user_status);
        }
        public void setUser_tumb_image(final Context context,final String user_thumb_image) {
           final CircleImageView thumb_image = (CircleImageView)mView.findViewById(R.id.allUsers_profile_image);
            if(!user_thumb_image.equals("2131165298"))
            {

                Picasso.with(context).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).resize(600, 600)
                  .centerCrop().placeholder(R.drawable.defaultimage1).into(thumb_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError()
                    {

                        Picasso.with(context).load(user_thumb_image).resize(600, 600)
                          .centerCrop().placeholder(R.drawable.defaultimage1).into(thumb_image);
                    }
                });

            }

        }
    }
}
