package dz.esisba.a2cpi_project;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private CircleImageView settingsImg;
    private TextView settingsName,settingsUsername;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private LinearLayout logOut;

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
        progressBar = findViewById(R.id.settingsProgressBar);
        scrollView = findViewById(R.id.settingsScrollView);
        logOut = findViewById(R.id.logOutApp);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                clearToken(user.getUid());
                startActivity(intent);
                finish();
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
                            Glide.with(SettingsActivity.this).load(downloadUrl).into(settingsImg);
                        }

                        if (doc.get("Name")!= null)
                            settingsName.setText(doc.get("Name").toString());

                        settingsUsername.setText("@"+doc.get("Username").toString());
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
}