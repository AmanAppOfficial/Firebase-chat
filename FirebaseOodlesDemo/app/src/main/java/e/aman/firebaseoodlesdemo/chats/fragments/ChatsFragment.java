package e.aman.firebaseoodlesdemo.chats.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.adapter.PersonalChatAdapter;
import e.aman.firebaseoodlesdemo.models.Messages;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View chatView;
    private RecyclerView chatRecyclerView;
    private DatabaseReference userRef , chatRef;

    private String currentUserId , friendKey;

    private List<Users> usersList;

    private PersonalChatAdapter adapter;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      chatView = inflater.inflate(R.layout.fragment_chats, container, false);
        chatRecyclerView = chatView.findViewById(R.id.chatListRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
        chatRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_CHAT_NODE);


      return chatView;
    }

    private void loadChats()
    {
        usersList = new ArrayList<>();
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(currentUserId).exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.child(currentUserId).getChildren())
                    {
                        friendKey = snapshot.getKey();

                        userRef.child(friendKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                if (dataSnapshot1.exists())
                                {
                                    Users users = new Users();
                                    users.setName(dataSnapshot1.child(Constants.DATABASE_NAME).getValue().toString());
                                    users.setProfileimage(dataSnapshot1.child(Constants.DATABASE_PROFILE_IMAGE).getValue().toString());
                                    usersList.add(users);

                                }

                                adapter = new PersonalChatAdapter(currentUserId , usersList, getContext());
                                chatRecyclerView.setAdapter(adapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadChats();
            }
        });
    }
}
