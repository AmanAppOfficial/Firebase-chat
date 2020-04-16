package e.aman.firebaseoodlesdemo.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import e.aman.firebaseoodlesdemo.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private ActivitySignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                signUp();
            }
        });



    }

    private void signUp()
    {
        String username = binding.signupUsernameText.getText().toString().trim();
        String password = binding.signupPasswordText.getText().toString().trim();
        String reEnterPassword = binding.reenterPasswordText.getText().toString().trim();
        boolean isValid = checkValidity(username , password , reEnterPassword);
        if(isValid)
        {
            if(password.equals(reEnterPassword))
            signUpAccount(username , password);
            else
            {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext() , "Password different!" , Toast.LENGTH_SHORT).show();
                binding.signupUsernameText.setText("");
                binding.signupPasswordText.setText("");
                binding.reenterPasswordText.setText("");
                binding.signupUsernameText.requestFocus();
            }

        }

    }

    private void signUpAccount(String username , String password)
    {

        mAuth.createUserWithEmailAndPassword(username , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext() , "Account created!" , Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext() , "Sign up failed!" , Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private boolean checkValidity(String username , String password , String rePassword)
    {
        if(username.isEmpty())
        {
            Toast.makeText(getApplicationContext() , "Username can't be empty!" , Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return false;
        }
        else  if(rePassword.isEmpty())
        {
            Toast.makeText(getApplicationContext() , "Password can't be empty!" , Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return false;
        }

        else  if(password.isEmpty())
        {
            Toast.makeText(getApplicationContext() , "Password can't be empty!" , Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    void hideNavigationBar()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
