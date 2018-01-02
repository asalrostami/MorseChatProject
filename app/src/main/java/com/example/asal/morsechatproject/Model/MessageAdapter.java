package com.example.asal.morsechatproject.Model;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asal.morsechatproject.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asal on 2018-01-02.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{

    private List<Message> userMessageList;

    public MessageAdapter(List<Message> userMessageList)
    {
        this.userMessageList = userMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_users,parent,false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position)
    {
        Message message = userMessageList.get(position);
        holder.messageText.setText(message.getMessage());

    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {

        public TextView messageText;
        public CircleImageView userProfileImage;


        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView)view.findViewById(R.id.tv_messageText_message);
            userProfileImage = (CircleImageView)view.findViewById(R.id.ciclerImage_image_message);
        }
    }
}
