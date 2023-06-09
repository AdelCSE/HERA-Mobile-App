package dz.esisba.a2cpi_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dz.esisba.a2cpi_project.models.PostModel;

public class RegisterActivity extends AppCompatActivity {

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Date date = new Date();

    TextInputEditText usernameEditTxt, emailEditTxt, pwEditTxt, nameEditTxt;
    AppCompatButton registerBtn;
    TextView loginBtn;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    ProgressBar progressBar;

    boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_register);

        RegisterUser();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }

    private void RegisterUser(){

        usernameEditTxt = findViewById(R.id.UsernameEditTxt);
        nameEditTxt = findViewById(R.id.NameEditTxt);
        emailEditTxt = findViewById(R.id.EmailEditTxt);
        pwEditTxt = findViewById(R.id.PasswordEditTxt);

        registerBtn = findViewById(R.id.SignUpButton);

        loginBtn = findViewById(R.id.SignInText);

        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditTxt.getText().toString().trim();
                String password = pwEditTxt.getText().toString().trim();
                String username = usernameEditTxt.getText().toString().trim();
                String name = nameEditTxt.getText().toString().trim();

                //name processing
                if (TextUtils.isEmpty(username))
                {
                    usernameEditTxt.setError("Username is required!");
                    return;
                }
                if (TextUtils.isEmpty(name))
                {
                    nameEditTxt.setError("Name is required!");
                    return;
                }
                //regular expression to validate uername
                if(!username.matches("^[a-zA-Z0-9._-]{3,}$"))
                {
                    usernameEditTxt.setError("Please enter a valid username");
                }

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
                }

                if(CheckExistingUser(username))
                {
                    progressBar.setVisibility(view.VISIBLE);
                    //registering user into firebase
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                //get the current user
                                FirebaseUser user = auth.getCurrentUser();
                                //display feedback

                                retrieveRestoreToken();
                                //store user id into firebase
                                DocumentReference df = fstore.collection("Users").document(user.getUid());
                                Map<String,Object> userInfor = new HashMap<>(); //represents key, value
                                //can be used to categorise our data and organize it
                                userInfor.put("Username", usernameEditTxt.getText().toString());
                                userInfor.put("Name", nameEditTxt.getText().toString());//user name categorie
                                userInfor.put("Email", email); //email categorie
                                userInfor.put("uid", user.getUid());
                                userInfor.put("createdAt", date);

                                ArrayList<String> following = new ArrayList<>();
                                ArrayList<String> followers = new ArrayList<>();
                                ArrayList<String>  posts = new ArrayList<>();
                                ArrayList<String>  answers = new ArrayList<>();

                                userInfor.put("following", following);
                                userInfor.put("followers", followers);
                                userInfor.put("posts", posts);
                                userInfor.put("answers", answers);
                                userInfor.put("reputation", 0);
                                userInfor.put("profilePictureUrl", "https://firebasestorage.googleapis.com/v0/b/hera-483e9.appspot.com/o/defualty.png?alt=media&token=826ced5c-30ac-47f5-a3ad-e49b7676689d");

                                //specify access level (if user is admin)
                                userInfor.put("isAdmin",false);

                                df.set(userInfor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        user.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "Please verifiy your email and sign in", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), VerificationActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }); //pass our map to the fb document
                            }
                            else
                            {
                                //if registration wasn't successful get the error (debugging purpose can be removed later)
                                Toast.makeText(RegisterActivity.this, "Some error has occured " + task.getException().getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean CheckExistingUser(String name)
    {
        //get all users
        fstore.collection("Users").whereEqualTo("Username", name)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                //Toast.makeText(getApplicationContext(), "method clicked", Toast.LENGTH_SHORT).show();
             if (task.getResult().size()>0) {
                 valid = false;
                 progressBar.setVisibility(View.GONE);
                 usernameEditTxt.setError("Username already exists");
             }
             else valid = true;
            }
            else Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
        return valid;
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
                            FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userToken);
                }
            }
        });
    }

    //Todo: add confirm pw logic
    //TODO: confirm valid email
    //TODO: USERNAME
}