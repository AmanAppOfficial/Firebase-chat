package e.aman.firebaseoodlesdemo.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

import e.aman.firebaseoodlesdemo.adapter.UserAdapter;
import e.aman.firebaseoodlesdemo.databinding.ActivityAdminMainBinding;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class AdminMainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ActivityAdminMainBinding binding;
    private List<Users> list;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        list = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_ROOT_NODE);


        binding.allUsersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.allUsersList.setLayoutManager(linearLayoutManager);




        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadUsers();
            }
        });


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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


    public void loadUsers()
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

               adapter = new UserAdapter(list);
               binding.allUsersList.setAdapter(adapter);

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }



    void hideNavigationBar()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
