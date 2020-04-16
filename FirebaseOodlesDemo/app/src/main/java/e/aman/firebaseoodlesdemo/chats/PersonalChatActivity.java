package e.aman.firebaseoodlesdemo.chats;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.adapter.PersonalMessagesAdapter;
import e.aman.firebaseoodlesdemo.models.Messages;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class PersonalChatActivity extends AppCompatActivity {

    private String friendName , currentUserId  , friendId;
    private TextView userName , lastSeen ;
    private CircleImageView imageView;

    private DatabaseReference userRef;

    private ImageButton sendMessageButton;
    private EditText messageText;
    private RecyclerView chatRecyclerview;
    private DatabaseReference chatRef;

    private List<Messages> messagesList;
    private PersonalMessagesAdapter messagesAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);

        friendName = getIntent().getExtras().getString(Constants.FRIEND_NAME);
        friendId = getIntent().getExtras().getString(Constants.FRIEND_KEY);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userName  = findViewById(R.id.custom_profile_name);
        imageView = findViewById(R.id.custom_profile_image);
        lastSeen = findViewById(R.id.custom_user_lastseen);
        sendMessageButton = findViewById(R.id.personal_send_message_button);
        messageText = findViewById(R.id.personal_input_message);
        chatRecyclerview = findViewById(R.id.chat_recyclerview);
        chatRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_CHAT_NODE);
        messagesList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonalChatActivity.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        chatRecyclerview.setLayoutManager(linearLayoutManager);



        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                if (!TextUtils.isEmpty(message))
                {
                    sendMessage(message);
                }
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                userRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
                userRef.child(friendId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                            String profileI = dataSnapshot.child(Constants.DATABASE_PROFILE_IMAGE).getValue().toString();
                            String lastS = dataSnapshot.child(Constants.LAST_SEEN).getValue().toString();

                            lastSeen.setText(lastS);
                            Picasso.get().load(profileI).into(imageView);
                            userName.setText(friendName);

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



    }

    private void loadMessages()
    {
        chatRef.child(currentUserId).child(friendId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

              showMessages(dataSnapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                showMessages(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showMessages(DataSnapshot dataSnapshot)
    {

        Messages messages = dataSnapshot.getValue(Messages.class);
        messagesList.add(messages);
        messagesAdapter = new PersonalMessagesAdapter(messagesList , PersonalChatActivity.this , currentUserId);
        chatRecyclerview.setAdapter(messagesAdapter);
        chatRecyclerview.scrollToPosition(messagesAdapter.getItemCount()-1);
        messagesAdapter.notifyDataSetChanged();

    }

    private void sendMessage(String message)
    {
        DatabaseReference messageSenderRef = chatRef.child(currentUserId).child(friendId);
        DatabaseReference messageReceiverRef = chatRef.child(friendId).child(currentUserId);

        DatabaseReference messageKeyRef = chatRef.child(currentUserId).child(friendId).push();

        String messagePushId = messageKeyRef.getKey();

        HashMap messageMap = new HashMap();
        messageMap.put(Constants.DATABASE_MESSAGE , message);
        messageMap.put(Constants.DATABASE_TYPE , Constants.TEXT);
        messageMap.put(Constants.DATABASE_FROM , currentUserId);

        messageSenderRef.child(messagePushId).updateChildren(messageMap);
        messageReceiverRef.child(messagePushId).updateChildren(messageMap);

        messageText.setText("");
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadMessages();
    }
}
