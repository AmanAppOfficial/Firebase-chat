package e.aman.firebaseoodlesdemo.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import e.aman.firebaseoodlesdemo.utils.Actions;
import e.aman.firebaseoodlesdemo.utils.Constants;
import e.aman.firebaseoodlesdemo.databinding.ActivityUserRegisterBinding;

public class UserRegisterActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String currentUserId;
    StorageReference firebaseStorage;

    private ActivityUserRegisterBinding binding;
    private Uri imageUri;
    private String imagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        binding = ActivityUserRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        firebaseStorage = FirebaseStorage.getInstance().getReference().child(Constants.STORAGE_ROOT_NODE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);

        setListeners();

    }

    private void setListeners()
    {
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser()
    {
        String name = binding.registerNameText.getText().toString().trim();
        String age = binding.registerAgeText.getText().toString().trim();
        String phone = binding.registerNumberText.getText().toString().trim();


        if(!name.isEmpty() && !age.isEmpty() && !phone.isEmpty() && imageUri != null)
            saveDetailsToDatabase(name, age, phone , imageUri);
        else
        {
            Toast.makeText(getApplicationContext() , "Please fill all details with profile picture!" , Toast.LENGTH_LONG).show();
        }


    }

    private void saveDetailsToDatabase(String name, String age, String phone, Uri imageUri)
    {
        HashMap usermap = new HashMap();
        usermap.put(Constants.DATABASE_NAME , name);
        usermap.put(Constants.DATABASE_AGE, age);
        usermap.put(Constants.DATABASE_PHONE_NUMBER , phone);
        databaseReference.child(currentUserId).updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    saveImageToStorage();
                }
                else
                {
                    Toast.makeText(getApplicationContext() , "Error : " + task.getException().getMessage() , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveImageToStorage()
    {
        final StorageReference filePath = firebaseStorage.child(currentUserId + ".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child(currentUserId).child("profileimage").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    Actions.sendToMainActivity(UserRegisterActivity.this);
                                    Toast.makeText(getApplicationContext(),"Details uploaded successfully!",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    });

                }
                else
                {
                    Toast.makeText(getApplicationContext() , "Error : " + task.getException().getMessage() , Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void pickImageFromGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, Constants.PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE)
        {
            imageUri = data.getData();
            imagepath = "" ;
            if(imagepath!=null)
                imagepath = getRealPathFromUri(imageUri);

            Picasso.get().load(imageUri.toString()).into(binding.profileImage);


        }
    }

    private String getRealPathFromUri(Uri uri)
    {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext() , uri , projection , null , null , null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result ;
    }
    void hideNavigationBar()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
