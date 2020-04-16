package e.aman.firebaseoodlesdemo.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.adapter.TabsAccessorAdapter;
import e.aman.firebaseoodlesdemo.utils.Actions;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String userId;
    DatabaseReference reference , groupsRef;


    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myViewPager = (ViewPager)findViewById(R.id.main_tabs_pager);                                   //view pager with adapter
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager() ,  1);
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = (TabLayout)findViewById(R.id.main_tabs);           //Tab Layout with view pager
        myTabLayout.setupWithViewPager(myViewPager);
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();


        reference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
        groupsRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_GROUP_REF);


        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

    }


    @Override
    protected void onStart() {
        super.onStart();

        checkEntryInDatabse();

    }


    private void checkEntryInDatabse() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(userId).exists()) {
                   Actions.sendToRegisterActivity(MainActivity.this);
                   finish();
                }
                else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);

        int id = item.getItemId();
                switch(id)
                {
                    case R.id.search:
                        Actions.sendToSearchUserActivity(MainActivity.this);
                        break;

                    case R.id.friend_request:
                        Actions.sendUserToFriendRequestActivity(MainActivity.this);
                        break;

                    case R.id.logout:
                        mAuth.signOut();
                        Actions.sendToLoginActivity(MainActivity.this);
                        finish();
                        break;

                    case R.id.create_group:
                        requestNewGroup();
                        break;

                    case R.id.settings:
                        Actions.sendToSettingsActivity(MainActivity.this);
                        break;

                    default:
                        return true;
                }


                return true;

    }

    private void requestNewGroup()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this , R.style.AlertDialog);
        alertDialog.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Friends Zone");
        alertDialog.setView(groupNameField);

        alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String groupName = groupNameField.getText().toString().trim();
                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(getApplicationContext() , "Please Write Group Name!" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    createNewGroup(groupName);
                }
            }
        });


        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void createNewGroup(String groupName)
    {
        groupsRef.child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(getApplicationContext() , "Group created successfully" , Toast.LENGTH_SHORT).show();
            }
        });

    }


    private String getTime()
    {
        SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
        String current_time_str = time_formatter.format(System.currentTimeMillis());
        return current_time_str;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
