package com.example.asal.morsechatproject;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asal.morsechatproject.Model.Chats;
import com.example.asal.morsechatproject.Model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private View myMainView;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;
    String online_user_id;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_chats, container, false);
        mRecyclerView = (RecyclerView) myMainView.findViewById(R.id.recyclerView_chat_fragment);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Chats,ChatsFragment.ChatsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>
                (
                        Chats.class,
                        R.layout.all_users_display_layout,
                        ChatsFragment.ChatsViewHolder.class,
                        mDatabaseReference
                )
        {
            @Override
            protected void populateViewHolder(final ChatsFragment.ChatsViewHolder viewHolder, Chats model, int position)
            {



                final String list_user_id = getRef(position).getKey();
                usersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot)
                    {

                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_tumb_image").getValue().toString();

                        String user_status = dataSnapshot.child("user_status").getValue().toString();

                        if(dataSnapshot.hasChild("online"))
                        {
                            String online_status = (String) dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(online_status);
                        }

                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage,getContext());
                        viewHolder.setUserStatus(user_status);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                if(dataSnapshot.child("online").exists())
                                {
                                    Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id",list_user_id);
                                    chatIntent.putExtra("user_name",userName);
                                    startActivity(chatIntent);
                                }
                                else
                                {
                                    usersReference.child(list_user_id).child("online").setValue(ServerValue.TIMESTAMP)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid)
                                                {

                                                    Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                                    chatIntent.putExtra("visit_user_id",list_user_id);
                                                    chatIntent.putExtra("user_name",userName);
                                                    startActivity(chatIntent);

                                                }
                                            });
                                }

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setUserName(String user_name) {
            TextView userName = (TextView) mView.findViewById(R.id.tv_allUsers_userName);
            userName.setText(user_name);
        }

        public void setThumbImage(final String thumbImage, final Context context) {
            final CircleImageView thumb_image = (CircleImageView) mView.findViewById(R.id.allUsers_profile_image);
            if (!thumbImage.equals("2131165298")) {

                Picasso.with(context).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).resize(600, 600)
                        .centerCrop().placeholder(R.drawable.defaultimage1).into(thumb_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(context).load(thumbImage).resize(600, 600)
                                .centerCrop().placeholder(R.drawable.defaultimage1).into(thumb_image);
                    }
                });

            }

        }
        public void setUserOnline(String online_status)
        {

            ImageView online_statusImage = (ImageView)mView.findViewById(R.id.online_status);
            if(online_status.equals("true"))
            {

                online_statusImage.setVisibility(View.VISIBLE);

            }
            else
            {

                online_statusImage.setVisibility(View.INVISIBLE);
            }
        }

        public void setUserStatus(String user_status)
        {
            TextView userStatus = (TextView)mView.findViewById(R.id.tv_allUsers_status);
            userStatus.setText(user_status);
        }
    }
}
