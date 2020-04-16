package e.aman.firebaseoodlesdemo.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import e.aman.firebaseoodlesdemo.utils.Actions;
import e.aman.firebaseoodlesdemo.utils.Constants;
import e.aman.firebaseoodlesdemo.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private boolean isAdmin = false;
    private ActivityLoginBinding binding;

    FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        addExternalStoragePermission();


        mAuth = FirebaseAuth.getInstance();

        if(isAdmin)
        {
            binding.adminText.setText("I'm not an admin");
        }
        else
        {
            binding.adminText.setText("I'm an admin");
        }

        setListeners();

    }

    private void setListeners()
    {

        binding.forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actions.forgotPassword(LoginActivity.this);
            }
        });

        binding.signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Actions.sendToSignupActivity(LoginActivity.this);
            }
        });

        binding.adminText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAdmin)
                {
                    binding.textLayout.setVisibility(View.VISIBLE);
                    isAdmin = false;
                    binding.adminText.setText("I'm admin");
                }
                else
                {
                    binding.textLayout.setVisibility(View.GONE);
                    isAdmin = true;
                    binding.adminText.setText("I'm user");
                }

            }
        });


        binding.signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actions.sendToSignupActivity(LoginActivity.this);
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                login();
            }
        });
    }




    private void login()
    {
        String username = binding.loginUsernameText.getText().toString().trim();
        String password = binding.loginPasswordText.getText().toString().trim();
        boolean isValid = checkValidity(username , password);
        if(isValid == true)
        {
          if(isAdmin)
          {
              if(username.equals(Constants.ADMIN_EMAIL) && password.equals(Constants.ADMIN_PASSWORD))
              {
                  binding.progressBar.setVisibility(View.GONE);
                  Actions.sendToAdminMainActivity(LoginActivity.this);
                  binding.loginUsernameText.setText("");
                  binding.loginPasswordText.setText("");
                  binding.loginUsernameText.requestFocus();

              }
              else
              {
                  binding.progressBar.setVisibility(View.GONE);
                  binding.loginUsernameText.setText("");
                  binding.loginPasswordText.setText("");
                  binding.loginUsernameText.requestFocus();
                  Toast.makeText(getApplicationContext() , "Incorrect credentials!" , Toast.LENGTH_SHORT).show();
              }
          }
          else
          {
             mAuth.signInWithEmailAndPassword(username , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task)
                 {
                     if (task.isSuccessful())
                     {
                         binding.progressBar.setVisibility(View.GONE);
                         binding.loginUsernameText.setText("");
                         binding.loginPasswordText.setText("");
                         binding.loginUsernameText.requestFocus();
                         Actions.sendToMainActivity(LoginActivity.this);
                     }
                     else
                     {
                         binding.progressBar.setVisibility(View.GONE);
                         Toast.makeText(getApplicationContext() , "Login Failed!" , Toast.LENGTH_SHORT).show();
                         binding.loginUsernameText.setText("");
                         binding.loginPasswordText.setText("");
                         binding.loginUsernameText.requestFocus();
                     }
                 }
             });
          }
        }
    }



    private boolean checkValidity(String username , String password)
    {
        if(username.isEmpty())
        {
            Toast.makeText(getApplicationContext() , "Username can't be empty!" , Toast.LENGTH_SHORT).show();
            return false;
        }
        else  if(password.isEmpty())
        {
            Toast.makeText(getApplicationContext() , "Password can't be empty!" , Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void hideNavigationBar()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void addExternalStoragePermission()
    {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {



                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

    }

}
