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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.adapter.FriendRequestAdapter;
import e.aman.firebaseoodlesdemo.adapter.FriendsAdapter;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.users.MainActivity;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class FriendsFragment extends Fragment {

    private View friendsFragmentView;
    private RecyclerView friendsList;
    private FriendsAdapter friendsAdapter;
    private List listOfFriends;
    private String currentUserId ;

    private DatabaseReference friendsRef , databaseReference;

    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        friendsFragmentView = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsList = friendsFragmentView.findViewById(R.id.friendsList);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listOfFriends = new ArrayList();
        friendsList.setLayoutManager(linearLayoutManager);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadFriends();
            }
        });
        return friendsFragmentView;
    }

    private void loadFriends()
    {
        friendsRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_FRIEND_NODE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
        friendsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
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
                                      Log.e("friend name" , user.getName());
                                      listOfFriends.add(user);

                                  }

                                  friendsAdapter = new FriendsAdapter(getContext() , currentUserId , listOfFriends);
                                  friendsList.setAdapter(friendsAdapter);

                              }


                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });
                  }
              }

                  friendsRef.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                      {
                          for (DataSnapshot snapshot : dataSnapshot.getChildren())
                          {
                             for (DataSnapshot s:  snapshot.getChildren())
                             {
                                 if (s.getKey().equals(currentUserId))
                                 {
                                    final String friendKey = snapshot.getKey();
                                     databaseReference.addValueEventListener(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull  DataSnapshot dataSnapshot1) {
                                             for (DataSnapshot s : dataSnapshot1.getChildren())
                                             {
                                                 if (s.getKey().equals(friendKey))
                                                 {
                                                     Users user = s.getValue(Users.class);
                                                     Log.e("friend name" , user.getName());
                                                     listOfFriends.add(user);

                                                 }

                                                 friendsAdapter = new FriendsAdapter(getContext() , currentUserId , listOfFriends);
                                                 friendsList.setAdapter(friendsAdapter);

                                             }


                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError databaseError) {

                                         }
                                     });
                                 }
                             }
                          }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
