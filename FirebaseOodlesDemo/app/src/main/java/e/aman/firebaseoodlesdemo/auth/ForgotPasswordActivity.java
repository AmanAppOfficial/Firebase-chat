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
import com.google.firebase.auth.FirebaseAuth;

import e.aman.firebaseoodlesdemo.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        binding.forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameForgotPassword = binding.forgotPasswordUsernameText.getText().toString().trim();
                if(!usernameForgotPassword.isEmpty())
                {
                    resetPassword(usernameForgotPassword);
                }
            }
        });

    }

    private void resetPassword(String username)
    {
        mAuth.sendPasswordResetEmail(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext() , "Reset password email sent to your mail id!" , Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext() , "Reset password failed!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void hideNavigationBar()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
