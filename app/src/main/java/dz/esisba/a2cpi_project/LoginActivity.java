package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GOOGLEAUTH";
    GoogleSignInClient googleSignInClient;
    Dialog dialog;

    EditText emailEditTxt, pwEditTxt;
    Button lloginBtn;
    TextView singupBtn, forgotPwBtn;

    FirebaseAuth auth;

    ProgressBar progressBar;

    AlertDialog.Builder reset_alert;

    LayoutInflater inflater;


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


        lloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEditTxt.getText().toString().trim();
                String password = pwEditTxt.getText().toString().trim();

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
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
        //google authentification
        //https://firebase.google.com/docs/auth/android/google-signin?hl=en&authuser=4
        //createRequest();

    }

    //creating the request to google server
    /*private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      //  dialog.setContentView(R.layout.dialog);
        dialog.setCanceledOnTouchOutside(false);


        //getting the button click
        ImageView googleBtn = findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(); //calling the sign in method
            }
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            dialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                dialog.dismiss();
            }
        }


        *//* TODO: create google authenticator *//*
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                          //  updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                         //   updateUI(null);
                        }
                    }
                });
    }*/

}