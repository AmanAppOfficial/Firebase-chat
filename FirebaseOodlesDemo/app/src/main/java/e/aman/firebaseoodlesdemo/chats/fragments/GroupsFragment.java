package e.aman.firebaseoodlesdemo.chats.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.chats.GroupChatActivity;
import e.aman.firebaseoodlesdemo.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView groupsList;
    private ArrayAdapter<String> groupsAdapter;
    private ArrayList<String> listOfGroups = new ArrayList<>();

    private DatabaseReference groupsRef;

    public GroupsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
       groupsRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_GROUP_REF);
       initFields();
       displayGroups();
       setListeners();
       return groupFragmentView;
    }

    private void setListeners()
    {
        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String currentGroupName = parent.getItemAtPosition(position).toString();
                Intent GroupChatIntent = new Intent(getContext() , GroupChatActivity.class);
                GroupChatIntent.putExtra(Constants.CURRENT_GROUP_NAME , currentGroupName);
                startActivity(GroupChatIntent);
            }
        });
    }

    private void displayGroups()
    {
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    list.add(snapshot.getKey());
                }
                listOfGroups.clear();
                listOfGroups.addAll(list);
                groupsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initFields()
    {
        groupsList = (ListView)groupFragmentView.findViewById(R.id.group_list);
        groupsAdapter = new ArrayAdapter<String>(getContext() , android.R.layout.simple_list_item_1 , listOfGroups);
        groupsList.setAdapter(groupsAdapter);
    }
}
