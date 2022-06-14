package dz.esisba.a2cpi_project;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditTxt, pwEditTxt;
    Button lloginBtn;
    TextView singupBtn, forgotPwBtn;

    FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private FirebaseUser user;


    ProgressBar progressBar;

    AlertDialog.Builder reset_alert;

    LayoutInflater inflater;

    private boolean securitySwitch1;
    public String code;


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
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();


        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        //if there's a user logged in go directly home
        if (auth.getCurrentUser()!= null ) {
            if (auth.getCurrentUser().isEmailVerified()) {//if already logged in go directly to home
                startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
                finish();
            }
        }

        lloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEditTxt.getText().toString().trim();
                String password = pwEditTxt.getText().toString().trim();

                //retrieveRestoreToken();

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
                            user = auth.getCurrentUser();
                            //verify if user is signed in
                            if (auth.getCurrentUser().isEmailVerified() ) { //Here in we need to add SecurityCheck Condition (false)
                            //Security check =boolean value in users collection
                            //true=verify email each time user try to login , else = login directly
                                fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            boolean securitySwitch = Boolean.TRUE.equals(task.getResult().getBoolean("Security"));
                                            if(!securitySwitch){
                                                //login directly
                                                retrieveRestoreToken();
                                                startActivity(new Intent(LoginActivity.this, BottomNavigationActivity.class));
                                                finish();
                                            }else{
                                                //send verification email contains random 6 digits code and verify it in Security Activity...
                                                code = generateCode();
                                                    sendEmail(code);
                                            }
                                        }
                                    }
                                });
                            }
                            else 
                            {
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginActivity.this,VerificationActivity.class);
                                startActivity(intent);
                            }
                            //to use if you want to create users without verification
                            /*Toast.makeText(LoginActivity.this, "LOGGED IN!", Toast.LENGTH_SHORT).show(); // for debug purposes, can be deleted later
                            retrieveRestoreToken();
                            startActivity(new Intent(LoginActivity.this, BottomNavigationActivity.class));
                            finish();*/
                            
                        } else {
                            //if loggin in wasn't successful get the error (debugging purpose can be removed later)
                            Toast.makeText(LoginActivity.this, "Some error has occurred " + task.getException().
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
            final Map<String, Object> userToken=new HashMap<>();
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    userToken.put("Token", token);
                    FirebaseFirestore.getInstance().collection("Users").document(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userToken).addOnSuccessListener(unused -> Toast.makeText(LoginActivity.this,"Succccessss",Toast.LENGTH_LONG));
                }
            }
        });
    }

    private void sendEmail(String verificationCode){
        final String ourEmail = "HeraAppTeam@gmail.com";
        final String ourPassword = "gnkquvxmgonntkxw";
        final String emailVerification= "Your Confirmation Code : "+verificationCode;
        String userEmail = emailEditTxt.getText().toString().trim();

        Properties props = new Properties();
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        props.put("mail.smtp.user", ourEmail);

        Session session = Session.getInstance(props,
                new Authenticator(){
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(ourEmail,ourPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ourEmail));
            message.setRecipients(Message.RecipientType.TO , InternetAddress.parse(userEmail));
            message.setSubject("HERA VERIFICATION CODE");
            message.setText(emailVerification);
            new SendMail().execute(message);
        }catch(MessagingException e){
            e.printStackTrace();
        }
    }

    private class SendMail extends AsyncTask<Message,String,String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.INVISIBLE);
            pd = ProgressDialog.show(LoginActivity.this , "Please Wait","Sending Email...", true,false);
        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return "Error in SendingEmail->doInBackground";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if(s.equals("Success")){
                AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
                ad.setCancelable(false);
                ad.setTitle(Html.fromHtml("<font color ='#509324'>Email Sent Successfully</font>"));
                ad.setMessage("A Verification code is sent to your email\nGo Check it out now");
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //open verification activity
                        //and passing the verification code to it
                        Intent intent = new Intent(LoginActivity.this,SecurityVerification.class);
                        intent.putExtra("verificationCode",code);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                ad.show();
            }else{
                Toast.makeText(LoginActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
            }
        }


    }

    private String generateCode() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}