package e.aman.firebaseoodlesdemo.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import e.aman.firebaseoodlesdemo.adapter.SearchUserAdapter;
import e.aman.firebaseoodlesdemo.databinding.ActivityUserSearchProfileBinding;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class PeopleSearchActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String currentUserId ;
    private List<Users> list;
    private SearchUserAdapter adapter;

    private ActivityUserSearchProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSearchProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        list = new ArrayList<>();

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_ROOT_NODE);


        binding.userSearchList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.userSearchList.setLayoutManager(linearLayoutManager);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadUsers();
            }
        });
        
        setListeners();

    }

    private void loadUsers()
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_ROOT_NODE);
        storageReference = FirebaseStorage.getInstance().getReference().child(Constants.STORAGE_ROOT_NODE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for(final DataSnapshot snapshot : dataSnapshot.getChildren())
                {

                    Users user = snapshot.getValue(Users.class);
                    list.add(user);

                }

                adapter = new SearchUserAdapter(list);
                binding.userSearchList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setListeners()
    {
        binding.userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
