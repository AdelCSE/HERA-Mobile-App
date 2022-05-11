package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.Token;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditTxt, pwEditTxt;
    Button lloginBtn;
    TextView singupBtn, forgotPwBtn;

    FirebaseAuth auth;

    ProgressBar progressBar;

    AlertDialog.Builder reset_alert;

    LayoutInflater inflater;
    private String Token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditTxt = findViewById(R.id.emailEditTxt);
        pwEditTxt = findViewById(R.id.passwordEditTxt);

        lloginBtn = findViewById(R.id.loginBtn);
        singupBtn = findViewById(R.id.textSignUp);
        forgotPwBtn = findViewById(R.id.textForgotPw);

        progressBar = findViewById(R.id.progressBar2);

        auth = FirebaseAuth.getInstance();

        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        //if there's a user logged in go directly home
        if (auth.getCurrentUser()!= null) { //if already logged in go directly to home
            startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
            finish();
        }

        lloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEditTxt.getText().toString().trim();
                String password = pwEditTxt.getText().toString().trim();

                retrieveRestoreToken();

                //email processing
                if (TextUtils.isEmpty(email)) {
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
                }

                progressBar.setVisibility(view.VISIBLE); //this piece of shit line wasted 2h of life :)

                //authenticate user

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "LOGGED IN!", Toast.LENGTH_SHORT).show(); // for debug purposes, can be deleted later
                            retrieveRestoreToken();
                            startActivity(new Intent(LoginActivity.this, BottomNavigationActivity.class));
                            finish();
                        } else {
                            //if loggin in wasn't successful get the error (debugging purpose can be removed later)
                            Toast.makeText(LoginActivity.this, "Some error has occured!" + task.getException().
                                    getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });


        singupBtn.setOnClickListener(new View.OnClickListener() { //if user doesnt have an account take them to the register page
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        forgotPwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = inflater.inflate(R.layout.reset_pop, null);

                reset_alert.setTitle("Have you forgotten your password?").setMessage("Enter your email to reset it.")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // validate the email addres
                                EditText email = v.findViewById(R.id.reset);
                                if (email.getText().toString().isEmpty()) //if user hasnt entered an email
                                {
                                    email.setError("Required Field");
                                    return;
                                }
                                // and then we will send reset link
                                auth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) { //if sending was successfull
                                        Toast.makeText(LoginActivity.this, "Reset email sent", Toast.LENGTH_SHORT);
                                    }
                                }).addOnFailureListener(new OnFailureListener() { //if sending failed display error (net problem mostly)
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                    }
                                });

                            }
                        }).setNegativeButton("Cancel", null)
                        .setView(v)
                        .create().show();
            }
        });

    }
    private void retrieveRestoreToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            Map<String, Object> userToken=new HashMap<>();
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    userToken.put("Token", token);
                    FirebaseFirestore.getInstance().collection("Users").document(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(LoginActivity.this,"Succccessss",Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        });
    }

}