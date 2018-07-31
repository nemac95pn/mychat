package com.example.nemanja.mychat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Button SendFriendRequestButton;
    private Button DeclineFriendrequestButton;
    private TextView ProfileName;
    private TextView ProfileStatus;
    private ImageView ProfileImage;

    private DatabaseReference UsersReference;

    private String CUURENT_STATE;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;
    private DatabaseReference FriendsReference;
    private DatabaseReference NotificationsReference;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        FriendRequestReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReference.keepSynced(true);

        NotificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationsReference.keepSynced(true);


        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();

        SendFriendRequestButton = (Button) findViewById(R.id.profile_visit_send_req_btn);
        DeclineFriendrequestButton = (Button) findViewById(R.id.profile_decline_friend_req_btn);
        ProfileName = (TextView) findViewById(R.id.profile_visit_username);
        ProfileStatus = (TextView) findViewById(R.id.profile_visit_user_status);
        ProfileImage = (ImageView) findViewById(R.id.profile_visit_user_image);


        CUURENT_STATE = "not_friends";

        UsersReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();


                ProfileName.setText(name);
                ProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(ProfileImage);


                FriendRequestReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(receiver_user_id))
                        {
                            String reg_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();
                            if (reg_type.equals("sent"))
                            {
                                CUURENT_STATE = "request_sent";
                                SendFriendRequestButton.setText("Cancel Friend Request");
                                DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendrequestButton.setEnabled(false);
                            }
                            else if (reg_type.equals("received"))
                            {
                                CUURENT_STATE = "request_received";
                                SendFriendRequestButton.setText("Accept Friend Request");

                                DeclineFriendrequestButton.setVisibility(View.VISIBLE);
                                DeclineFriendrequestButton.setEnabled(true);

                                DeclineFriendrequestButton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        DeclineFriendRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            FriendsReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.hasChild(receiver_user_id))
                                    {
                                        CUURENT_STATE = "friends";
                                        SendFriendRequestButton.setText("Unfriend This Person");

                                        DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
                                        DeclineFriendrequestButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendrequestButton.setEnabled(false);


        if (!sender_user_id.equals(receiver_user_id))
        {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    SendFriendRequestButton.setEnabled(false);

                    if (CUURENT_STATE.equals("not_friends"))
                    {
                        SendFriendRequestToAPerson();
                    }

                    if (CUURENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }

                    if (CUURENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }

                    if (CUURENT_STATE.equals("friends"))
                    {
                        UnFriendaFriend();
                    }

                }
            });
        }
        else
        {
            DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }



    }



    private void DeclineFriendRequest()
    {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendFriendRequestButton.setEnabled(true);
                                CUURENT_STATE = "not_friends";
                                SendFriendRequestButton.setText("Send Friend Request");

                                DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendrequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }




    private void UnFriendaFriend()
    {
        FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendFriendRequestButton.setEnabled(true);
                                CUURENT_STATE = "not_friends";
                                SendFriendRequestButton.setText("Send Friend Request");

                                DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendrequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }




    private void AcceptFriendRequest()
    {
        Calendar calFordATE = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calFordATE.getTime());

        FriendsReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                FriendsReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                SendFriendRequestButton.setEnabled(true);
                                                CUURENT_STATE = "friends";
                                                SendFriendRequestButton.setText("Unfriend this Person");

                                                DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendrequestButton.setEnabled(false);
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




    private void CancelFriendRequest()
    {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendFriendRequestButton.setEnabled(true);
                                CUURENT_STATE = "not_friends";
                                SendFriendRequestButton.setText("Send Friend Request");

                                DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendrequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }



    private void SendFriendRequestToAPerson()
    {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                HashMap<String, String> notificationsData = new HashMap<String, String>();
                                notificationsData.put("from", sender_user_id);
                                notificationsData.put("type", "request");

                                NotificationsReference.child(receiver_user_id).push().setValue(notificationsData).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            SendFriendRequestButton.setEnabled(true);
                                            CUURENT_STATE = "request_sent";
                                            SendFriendRequestButton.setText("Cancel Friend Request");

                                            DeclineFriendrequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendrequestButton.setEnabled(false);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}
