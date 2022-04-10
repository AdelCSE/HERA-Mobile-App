package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private static String username,uid,currentUsername = "";
    private TextView usernameTxt;
    private Button followBtn;
    private static  String date = DateFormat.getInstance().format(new Date());
    private static Boolean following = false;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        username = getIntent().getStringExtra("Username");
        uid = getIntent().getStringExtra("uid");
        usernameTxt = findViewById(R.id.usernameTxt);
        followBtn = findViewById(R.id.followBtn);
        usernameTxt.setText("@"+username);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();

        GetCurrentUsername();

        //we need this to update what our current user is following
        DocumentReference currentUserRef = fstore.collection("Users").document(user.getUid()).
                collection("following").document(uid);
        currentUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        followBtn.setEnabled(false);
                        followBtn.setText("Following");
                    }
                    else
                    {
                        followBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //we need this to update the followers of the user we're watching
                                DocumentReference userRef = fstore.collection("Users").document(uid).
                                        collection("followers").document(user.getUid());

                                Map<String,Object> userInfor = new HashMap<>(); //represents key, value
                                userInfor.put("Username", currentUsername);
                                userInfor.put("followingDate", date);
                                userInfor.put("uid", user.getUid());

                                userRef.set(userInfor); //pass our map to the fb document

                                Map<String,Object> currUserInfor = new HashMap<>(); //represents key, value
                                currUserInfor.put("Username", username);
                                currUserInfor.put("followingDate", date);
                                currUserInfor.put("uid", uid);

                                currentUserRef.set(currUserInfor); //pass our map to the fb document
                                followBtn.setEnabled(false);
                                followBtn.setText("Following");
                            }
                        });
                    }
                }
            }
        });


    }

    private void GetCurrentUsername()
    {
        DocumentReference df = fstore.collection("Users").document(user.getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        currentUsername=doc.get("Username").toString();
                    }
                }
            }
        });
    }
}