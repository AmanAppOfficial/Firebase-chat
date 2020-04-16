package e.aman.firebaseoodlesdemo.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.databinding.ActivityPersonProfileBinding;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class PersonProfileActivity extends AppCompatActivity {

    private ActivityPersonProfileBinding binding;

    private DatabaseReference reference , friendRequestRef;
    private String currentUserId ;
    private String friendId;
    private DatabaseReference friendsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        final String name = getIntent().getExtras().getString(Constants.INTENT_NAME);
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_FRIEND_REQUEST_REF);
        friendsRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_FRIEND_NODE);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setListener();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Users user = snapshot.getValue(Users.class);
                    if(user.getName().equals(name))
                    {
                        if (snapshot.getKey().equals(currentUserId))
                        {
                            binding.requestButton.setVisibility(View.GONE);
                        }

                        checkPendingFriendship();              //check for pending friend request
                        checkFriendship();

                        friendId =  snapshot.getKey();
                        binding.personProfileName.setText(user.getName());
                        binding.personProfileAge.setText(user.getAge());
                        Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.profile_icon).into(binding.personProfileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkFriendship()
    {
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUserId).child(friendId).exists())
                {
                    binding.requestButton.setText(Constants.UNFRIEND);
                }
                else if (dataSnapshot.child(friendId).child(currentUserId).exists())
                {
                    binding.requestButton.setText(Constants.UNFRIEND);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkPendingFriendship()
    {
        friendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUserId).child(friendId).exists())
                {
                    binding.requestButton.setText(Constants.ACCEPT_REQUEST);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setListener()
    {
        binding.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.requestButton.getText().toString().equals(Constants.SEND_REQUEST))
                sendFriendRequest();
                else if(binding.requestButton.getText().toString().equals(Constants.CANCEL_REQUEST))
                    cancelFriendRequest();
                else if (binding.requestButton.getText().toString().equals(Constants.ACCEPT_REQUEST))
                    acceptRequest();
                else if (binding.requestButton.getText().toString().equals(Constants.UNFRIEND))
                    unfriend();

            }
        });
    }

    private void unfriend()
    {
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUserId).child(friendId).exists())
                {
                    dataSnapshot.child(currentUserId).child(friendId).getRef().removeValue();
                    binding.requestButton.setText(Constants.SEND_REQUEST);
                }
                else if (dataSnapshot.child(friendId).child(currentUserId).exists())
                {
                    dataSnapshot.child(friendId).child(currentUserId).getRef().removeValue();
                    binding.requestButton.setText(Constants.SEND_REQUEST);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void acceptRequest()
    {
         friendRequestRef.child(currentUserId).child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        dataSnapshot.getRef().removeValue();
                        addToFriend(currentUserId  , friendId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }



    private void cancelFriendRequest()
    {
        friendRequestRef.child(friendId).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                     dataSnapshot.getRef().removeValue();
                    binding.requestButton.setText(Constants.SEND_REQUEST);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void addToFriend(String currentUserId, String senderId)
    {
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_FRIEND_NODE);
        HashMap friendsMap = new HashMap();
        friendsMap.put(Constants.TIME , getTime());
        friendsMap.put(Constants.DATE , getDate());

        friendsRef.child(currentUserId).child(senderId).updateChildren(friendsMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                Toast.makeText(getApplicationContext() , "Added to friend list!" , Toast.LENGTH_SHORT).show();
                binding.requestButton.setVisibility(View.GONE);
            }
        });

    }




    private void sendFriendRequest()
    {
        HashMap friendMap = new HashMap();
        friendMap.put(Constants.TIME , getTime());
        friendMap.put(Constants.DATE , getDate());
        friendRequestRef.child(friendId).child(currentUserId).updateChildren(friendMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    binding.requestButton.setText(Constants.CANCEL_REQUEST);
                    Toast.makeText(getApplicationContext() , "Friend request sent!" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext() , "Error : " + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getTime()
    {
        SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
        String current_time_str = time_formatter.format(System.currentTimeMillis());
        return current_time_str;
    }

    private String getDate()
    {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        return currentDate;
    }

    private void updateUI()
    {
        friendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(friendId).child(currentUserId).exists())
                {
                    binding.requestButton.setText(Constants.CANCEL_REQUEST);
                }
                else
                {
                    binding.requestButton.setText(Constants.SEND_REQUEST);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUI();

    }


}
