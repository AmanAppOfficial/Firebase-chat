package e.aman.firebaseoodlesdemo.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import e.aman.firebaseoodlesdemo.adapter.FriendRequestAdapter;
import e.aman.firebaseoodlesdemo.databinding.ActivityFriendRequestBinding;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class FriendRequestActivity extends AppCompatActivity {

    private ActivityFriendRequestBinding binding;

    private DatabaseReference friendRequestRef , databaseReference;
    private String currentUserId;
    private List<Users> list;
    private FriendRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        list = new ArrayList<>();
        binding.friendRequestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.friendRequestList.setLayoutManager(linearLayoutManager);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_FRIEND_REQUEST_REF).child(currentUserId);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadAllFriendRequests();
            }
        });

    }

    private void loadAllFriendRequests()
    {
        friendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {

                        final String key = snapshot.getKey();
                       databaseReference.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull  DataSnapshot dataSnapshot1) {
                              for (DataSnapshot s : dataSnapshot1.getChildren())
                               {
                                   if (s.getKey().equals(key))
                                   {
                                       Users user = s.getValue(Users.class);
                                        list.add(user);

                                   }

                                   adapter = new FriendRequestAdapter(list);
                                   binding.friendRequestList.setAdapter(adapter);

                               }


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
}
