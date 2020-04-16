package e.aman.firebaseoodlesdemo.chats;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.adapter.ChatAdapter;
import e.aman.firebaseoodlesdemo.models.Chats;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String currentGroupName;

    private FirebaseAuth mAuth;
    private String currentUserId, currentUserName;
    private DatabaseReference reference , groupsRef ,  groupsMessageKeyRef;

    private ImageButton sendMessageButton;
    private EditText messageText;
    private RecyclerView groupChatMessages;
    private String currentDate, currentTime;

    private List<Chats> chatList;
    private ChatAdapter chatAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().getString(Constants.CURRENT_GROUP_NAME);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
        groupsRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_GROUP_REF);



        initFields();
        getUserInfo();
        setupListener();

    }

    private void setupListener()
    {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                if (!TextUtils.isEmpty(message))
                {
                    saveMessageToDatabase(message);
                    messageText.setText("");

                }
            }
        });
    }

    private void saveMessageToDatabase(String message)
    {

        String messageKey = groupsRef.child(currentGroupName).push().getKey();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        groupsMessageKeyRef = groupsRef.child(currentGroupName).child(messageKey);

        HashMap messageMap = new HashMap();
        messageMap.put(Constants.DATABASE_NAME , currentUserName);
        messageMap.put(Constants.DATABASE_MESSAGE , message);
        messageMap.put(Constants.DATE , currentDate);
        messageMap.put(Constants.TIME , currentTime);

        groupsMessageKeyRef.updateChildren(messageMap);


    }

    private void getUserInfo()
    {
        reference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child(Constants.DATABASE_NAME).getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initFields()
    {

        mToolbar = (Toolbar)findViewById(R.id.group_chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMessageButton = findViewById(R.id.group_send_message_button);
        messageText =  findViewById(R.id.group_input_message);
        groupChatMessages = findViewById(R.id.group_chat_list);
        chatList =  new ArrayList<>();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        groupChatMessages.setLayoutManager(linearLayoutManager);




    }

    @Override
    protected void onStart() {
        super.onStart();

        groupsRef.child(currentGroupName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if (dataSnapshot.exists())
                {
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            displayMessages(dataSnapshot);

                        }
                    });

                }
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

    private void displayMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();


            Chats chats = new Chats();
            chats.setMessage(chatMessage);
            chats.setTime(chatTime);
            chats.setName(chatName);

            chatList.add(chats);

        }


        chatAdapter  = new ChatAdapter(chatList , GroupChatActivity.this , currentUserName);
        groupChatMessages.setAdapter(chatAdapter);
        groupChatMessages.scrollToPosition(chatAdapter.getItemCount()-1);
        chatAdapter.notifyDataSetChanged();


    }
}
