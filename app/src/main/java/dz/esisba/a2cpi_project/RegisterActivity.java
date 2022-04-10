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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static  String date = DateFormat.getInstance().format(new Date());

    EditText  nameEditTxt, emailEditTxt, pwEditTxt, confirmPwEditTxt;
    Button registerBtn;
    TextView loginBtn;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    ProgressBar progressBar;

    boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditTxt = findViewById(R.id.nameEditTxt);
        emailEditTxt = findViewById(R.id.emailEditTxt);
        pwEditTxt = findViewById(R.id.passwordEditTxt);

        registerBtn = findViewById(R.id.registerBtn);

        loginBtn = findViewById(R.id.textLogin);

        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditTxt.getText().toString().trim();
                String password = pwEditTxt.getText().toString().trim();
                String name = nameEditTxt.getText().toString().trim();

                //name processing
                if (TextUtils.isEmpty(name))
                {
                    nameEditTxt.setError("Name is required!");
                    return;
                }
                //regular expression to validate uername
                if(!name.matches("^[a-zA-Z0-9._-]{3,}$"))
                {
                    nameEditTxt.setError("Please enter a valid user name");
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

                if(CheckExistingUser(name))
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
                                Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show(); // for debug purposes, can be deleted later


                                //store user id into firebase
                                DocumentReference df = fstore.collection("Users").document(user.getUid());
                                Map<String,Object> userInfor = new HashMap<>(); //represents key, value
                                //can be used to categorise our data and organize it
                                userInfor.put("Username", nameEditTxt.getText().toString());//user name categorie
                                userInfor.put("Email", email); //email categorie
                                userInfor.put("uid", user.getUid());
                                userInfor.put("createdAt", date);

                                //specify access level (if user is admin)
                                userInfor.put("isUser","1");

                                df.set(userInfor); //pass our map to the fb document

                                //move into home page
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
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
                 nameEditTxt.setError("Username already exists");
             }
             else valid = true;
            }
            else Toast.makeText(getApplicationContext(), "unknown error", Toast.LENGTH_SHORT).show();
        }
    });
        return valid;
    }

    //Todo: add confirm pw logic
    //TODO: confirm valid email
    //TODO: USERNAME
}