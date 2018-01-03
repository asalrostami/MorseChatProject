package com.example.asal.morsechatproject;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asal.morsechatproject.Model.MyDividerItemDecoration;
import com.example.asal.morsechatproject.Model.Requests;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment
{

    private RecyclerView mRecyclerView;
    private  View mMainView;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;
    String online_user_id;

    private DatabaseReference FriendDatabaseRef;
    private DatabaseReference FriendReqDatabaseRef;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {



        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");


        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        mRecyclerView = (RecyclerView)mMainView.findViewById(R.id.recyclerView_request_fragment);

        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //when a new item adds,a new row adds automatically
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        //add divider in recyclerView by custom divider
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, 16));


        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
       FirebaseRecyclerAdapter<Requests,RequestsFragment.RequestViewHolder> firebaseRecyclerAdapter
               = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>
               (
                       Requests.class,
                       R.layout.friends_requests_all_users_layout,
                       RequestsFragment.RequestViewHolder.class,
                       mDatabaseReference

               ) {
           @Override
           protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, int position)
           {
               final String list_user_id = getRef(position).getKey();
               DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();


               get_type_ref.addValueEventListener(new ValueEventListener()
               {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot)
                   {
                       if(dataSnapshot.exists())
                       {
                           String request_type = dataSnapshot.getValue().toString();


                           if(request_type.equals("received"))
                           {
                               userDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener()
                               {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot)
                                   {

                                       final String user_name = dataSnapshot.child("user_name").getValue().toString();
                                       final String thumbImage = dataSnapshot.child("user_tumb_image").getValue().toString();
                                       final String user_status = dataSnapshot.child("user_status").getValue().toString();

                                       viewHolder.setUserName(user_name);
                                       viewHolder.setThumbImage(thumbImage,getContext());
                                       viewHolder.setUserStatus(user_status);

                                       viewHolder.mView.setOnClickListener(new View.OnClickListener()
                                       {
                                           @Override
                                           public void onClick(View view)
                                           {

                                               CharSequence options[] = new CharSequence[]
                                                       {
                                                               "Accept Friend Request",
                                                               "Cancel Friend Request"

                                                       };
                                               AlertDialog.Builder  builder = new AlertDialog.Builder(getContext());
                                               builder.setTitle("Friend Request Options");
                                               builder.setItems(options, new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialogInterface, int position)
                                                   {

                                                       if(position == 0)
                                                       {

                                                           //get current date with custom format
                                                           Calendar calendar = Calendar.getInstance();
                                                           SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                           final String saveCurrentDate = currentDate.format(calendar.getTime());

                                                           FriendDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate)
                                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                       @Override
                                                                       public void onSuccess(Void aVoid)
                                                                       {
                                                                           FriendDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                       @Override
                                                                                       public void onSuccess(Void aVoid)
                                                                                       {

                                                                                           FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                       @Override
                                                                                                       public void onComplete(@NonNull Task<Void> task)
                                                                                                       {
                                                                                                           if(task.isSuccessful())
                                                                                                           {
                                                                                                               FriendReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                           @Override
                                                                                                                           public void onComplete(@NonNull Task<Void> receiveTask)
                                                                                                                           {

                                                                                                                               if(receiveTask.isSuccessful())
                                                                                                                               {
                                                                                                                                   Toast.makeText(getContext(),"Friend Request Accepted Successfully.",Toast.LENGTH_SHORT).show();
                                                                                                                               }
                                                                                                                           }
                                                                                                                       });
                                                                                                           }
                                                                                                       }
                                                                                                   });
                                                                                       }
                                                                                   });

                                                                       }
                                                                   });
                                                       }
                                                       else if(position == 1)
                                                       {

                                                           FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task)
                                                                       {
                                                                           if(task.isSuccessful())
                                                                           {
                                                                               FriendReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                           @Override
                                                                                           public void onComplete(@NonNull Task<Void> receiveTask)
                                                                                           {

                                                                                               if(receiveTask.isSuccessful())
                                                                                               {
                                                                                                   Toast.makeText(getContext(),"Friend Request Cancelled Successfully.",Toast.LENGTH_SHORT).show();

                                                                                               }
                                                                                           }
                                                                                       });
                                                                           }
                                                                       }
                                                                   });

                                                       }
                                                   }
                                               });

                                               builder.show();


                                           }
                                       });


                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError)
                                   {


                                   }
                               });


                           }
                           else if(request_type.equals("sent"))
                           {
                               Button req_sent_btn = viewHolder.mView.findViewById(R.id.btn_accept_requestFragment);
                               req_sent_btn.setText("Request Sent");
                               viewHolder.mView.findViewById(R.id.btn_cancel_requestFragment).setVisibility(View.INVISIBLE);

                               userDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener()
                               {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot)
                                   {

                                       final String user_name = dataSnapshot.child("user_name").getValue().toString();
                                       final String thumbImage = dataSnapshot.child("user_tumb_image").getValue().toString();
                                       final String user_status = dataSnapshot.child("user_status").getValue().toString();

                                       viewHolder.setUserName(user_name);
                                       viewHolder.setThumbImage(thumbImage,getContext());
                                       viewHolder.setUserStatus(user_status);

                                       viewHolder.mView.setOnClickListener(new View.OnClickListener()
                                       {
                                           @Override
                                           public void onClick(View view)
                                           {

                                               CharSequence options[] = new CharSequence[]
                                                       {
                                                               "Cancel Friend Request"

                                                       };
                                               AlertDialog.Builder  builder = new AlertDialog.Builder(getContext());
                                               builder.setTitle("Request sent");
                                               builder.setItems(options, new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialogInterface, int position)
                                                   {


                                                       if(position == 0)
                                                       {

                                                           FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task)
                                                                       {
                                                                           if(task.isSuccessful())
                                                                           {
                                                                               FriendReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                           @Override
                                                                                           public void onComplete(@NonNull Task<Void> receiveTask)
                                                                                           {

                                                                                               if(receiveTask.isSuccessful())
                                                                                               {
                                                                                                   Toast.makeText(getContext(),"Friend Request Cancelled Successfully.",Toast.LENGTH_SHORT).show();

                                                                                               }
                                                                                           }
                                                                                       });
                                                                           }
                                                                       }
                                                                   });

                                                       }
                                                   }
                                               });

                                               builder.show();


                                           }
                                       });


                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError)
                                   {

                                   }
                               });


                           }
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });




           }
       };
       mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public RequestViewHolder(View view)
        {
            super(view);
            mView = view;

        }



        public void setUserName(String user_name)
        {
            TextView userName = (TextView) mView.findViewById(R.id.tv_name_requestFragment);
            userName.setText(user_name);
        }

        public void setThumbImage(final String thumbImage, final Context context)
        {

            final CircleImageView thumb_image = (CircleImageView) mView.findViewById(R.id.circleImage_requestsFragment);
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

        public void setUserStatus(String user_status)
        {
            TextView userStatus = (TextView)mView.findViewById(R.id.tv_status_requestFragment);
            userStatus.setText(user_status);
        }
    }

}

