package dz.esisba.a2cpi_project;

import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private CircleImageView settingsImg;
    private ImageButton backBtn;
    private TextView settingsName,settingsUsername,settingsEmail;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private LinearLayout logOut,changePassword,sendFeedBack,reportProblem;
    private AppCompatButton editprofileBtn;
    private SwitchCompat securitySwitch;
    private SwitchCompat notificationsSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        settingsImg = findViewById(R.id.settingsImg);
        settingsName = findViewById(R.id.settingsName);
        settingsUsername = findViewById(R.id.settingsUsername);
        settingsEmail = findViewById(R.id.settingsEmail);
        progressBar = findViewById(R.id.settingsProgressBar);
        scrollView = findViewById(R.id.settingsScrollView);
        backBtn = findViewById(R.id.settingsBackBtn);
        logOut = findViewById(R.id.logOutApp);
        changePassword = findViewById(R.id.ChangePasswordBtn);
        sendFeedBack = findViewById(R.id.sendFeedBackBtn);
        reportProblem = findViewById(R.id.reportProblemBtn);
        editprofileBtn=findViewById(R.id.settingsEditProfileBtn);
        securitySwitch = findViewById(R.id.securitySwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        editprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this,EditProfileActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                                FirebaseAuth.getInstance().signOut();
                                clearToken(user.getUid());
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this,ChangePasswordActivity.class));
            }
        });

        reportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this , HelpActivity.class);
                intent.putExtra("helpTag","problem");
                startActivity(intent);
            }
        });
        sendFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this , HelpActivity.class);
                intent.putExtra("helpTag","feedback");
                startActivity(intent);
            }
        });

        securitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                fstore.collection("Users").document(user.getUid()).update("Security",b);
            }
        });

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    retrieveRestoreToken();
                } else {
                    clearToken(user.getUid());
                }
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        SetUserInfos();

    }

    private void SetUserInfos(){
        DocumentReference df = fstore.collection("Users").document(user.getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        if (doc.get("profilePictureUrl")!= null) {
                            String downloadUrl = doc.get("profilePictureUrl").toString();
                            Glide.with(getApplicationContext()).load(downloadUrl).into(settingsImg);
                        }

                        if (doc.get("Name")!= null)
                            settingsName.setText(doc.get("Name").toString());

                        settingsUsername.setText("@"+doc.get("Username").toString());
                        settingsEmail.setText(doc.get("Email").toString());
                        securitySwitch.setChecked(Boolean.TRUE.equals(doc.getBoolean("Security")));
                    }
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(VISIBLE);

                }
            }
        });
    }



    //function that delete the token of the user Because when the user is signed-out he doesn't receive Notifications
    private void clearToken(String uid){
        final Map<String,Object> emptyToken = new HashMap<>();
        emptyToken.put("Token", FieldValue.delete());
        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(uid)
                .update(emptyToken);
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
                            FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userToken).addOnSuccessListener(unused -> Toast.makeText(SettingsActivity.this,"Succccessss",Toast.LENGTH_LONG));
                }
            }
        });
    }
}

//TODO // fe logout Onclick  dir if(securityCheck ==true) Email yweli unVerified
