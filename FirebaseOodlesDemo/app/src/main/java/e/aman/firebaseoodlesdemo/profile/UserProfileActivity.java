package e.aman.firebaseoodlesdemo.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import e.aman.firebaseoodlesdemo.databinding.ActivityUserProfileBinding;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        final String name = getIntent().getExtras().getString(Constants.INTENT_NAME);

        FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(name.equals(snapshot.child(Constants.DATABASE_NAME).getValue().toString()))
                    {
                        Picasso.get().load(snapshot.child(Constants.DATABASE_PROFILE_IMAGE).getValue().toString()).into(binding.userProfileImage);
                        binding.userProfileAge.setText(snapshot.child(Constants.DATABASE_AGE).getValue().toString());
                        binding.userProfileName.setText(snapshot.child(Constants.DATABASE_NAME).getValue().toString());
                        binding.userProfilePhone.setText(snapshot.child(Constants.DATABASE_PHONE_NUMBER).getValue().toString());

                    }
                }
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
