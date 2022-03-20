package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText  nameEditTxt, emailEditTxt, pwEditTxt, confirmPwEditTxt;
    Button registerBtn;
    TextView loginBtn;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditTxt = findViewById(R.id.nameEditTxt);
        emailEditTxt = findViewById(R.id.emailEditTxt);
        pwEditTxt = findViewById(R.id.passwordEditTxt);
        confirmPwEditTxt = findViewById(R.id.confirmPasswordEditTxt);

        registerBtn = findViewById(R.id.registerBtn);

        loginBtn = findViewById(R.id.textLogin);

        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser()!= null) { //if already logged in go directly to home
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditTxt.getText().toString().trim();
                String password = pwEditTxt.getText().toString().trim();
                String confirmPw = confirmPwEditTxt.getText().toString();

                //email processing
                if (TextUtils.isEmpty(email))
                {
                    emailEditTxt.setError("Email is required!");
                    return;
                }

                { //pw processing block
                    if (TextUtils.isEmpty(password)) {
                        pwEditTxt.setError("Password is required!");
                        return;
                    } else if (password.length() < 6) {
                        pwEditTxt.setError("Password must be more than 6 characters!");
                        return;
                    }
                    else if(TextUtils.isEmpty(confirmPw))
                    {
                        confirmPwEditTxt.setError("Please confirm your password");
                        return;
                    }
                    else if (!confirmPw.equals(password)) //if the user hasn't confirmed pw correctly
                    {
                        confirmPwEditTxt.setError("Password does not match!");
                        return;
                    }
                }

                progressBar.setVisibility(view.VISIBLE);

                //registering user into firebase

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show(); // for debug purposes, can be deleted later
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                        else
                        {
                            //if registration wasn't successful get the error (debugging purpose can be removed later)
                            Toast.makeText(RegisterActivity.this, "Some error has occured!" + task.getException().getMessage(), Toast.LENGTH_SHORT)
                            .show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }

    //Todo: add confirm pw logic
}