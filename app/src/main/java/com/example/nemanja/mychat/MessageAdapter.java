package com.example.nemanja.mychat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nemanja on 3/20/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersDatabaseReference;

    public MessageAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout_of_users, parent, false);


        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(V);
    }




    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position)
    {
        String message_sender_id = mAuth.getCurrentUser().getUid();

        final Messages messages = userMessagesList.get(position);
        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        UsersDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
              //  String userName = dataSnapshot.child("user_name").getValue().toString();
                String userImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.with(holder.userProfileImage.getContext()).load(userImage)
                        .placeholder(R.drawable.default_profile).into(holder.userProfileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        if (fromMessageType.equals("text"))
        {
            holder.messagePicture.setImageBitmap(null);
           // holder.messagePicture.setVisibility(View.INVISIBLE);
            holder.messagePicture.setPadding(0,0,0,0);

            // For sender and receiver users //
            if (fromUserId.equals(message_sender_id))
            {
                holder.sentMessageText.setVisibility(View.VISIBLE);
                holder.sentMessageText.setText(messages.getMessage());
                holder.messageText.setVisibility(View.INVISIBLE);
                holder.userProfileImage.setVisibility(View.INVISIBLE);
                holder.messagePicture.setVisibility(View.INVISIBLE);
              //  holder.sentMessageText.setPadding(25,25,25,25);

            }
            else
            {
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(messages.getMessage());
                holder.sentMessageText.setVisibility(View.INVISIBLE);
                holder.userProfileImage.setVisibility(View.VISIBLE);
                holder.messagePicture.setVisibility(View.INVISIBLE);
               // holder.messageText.setPadding(25,25,25,25);

            }
        }
        else
        {
            holder.messagePicture.setVisibility(View.VISIBLE);
            holder.sentMessageText.setVisibility(View.INVISIBLE);
            holder.messageText.setVisibility(View.INVISIBLE);
         //   holder.messageText.setPadding(0,0,0,0);
          //  holder.sentMessageText.setPadding(0,0,0,0);
           // holder.messagePicture.setPadding(0,0,0,0);

            if (fromUserId.equals(message_sender_id))
            {
                holder.userProfileImage.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.userProfileImage.setVisibility(View.VISIBLE);
            }


            Picasso.with(holder.userProfileImage.getContext()).load(messages.getMessage())
                    .placeholder(R.drawable.default_profile).into(holder.messagePicture);
        }


    }





    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }





    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageText;
        public TextView sentMessageText;
        public CircleImageView userProfileImage;
        public ImageView messagePicture;

        public MessageViewHolder(View view)
        {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text);
            sentMessageText = (TextView) view.findViewById(R.id.sent_message_text);
            messagePicture = (ImageView) view.findViewById(R.id.message_image_view);
            userProfileImage = (CircleImageView) view.findViewById(R.id.messages_profile_image);
        }
    }
}
