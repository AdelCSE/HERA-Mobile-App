package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private static String username,uid,currentUsername = "";
    private TextView usernameTxt,name, bio, followersCount, followingCount;
    private Button followBtn;
    private static  String date = DateFormat.getInstance().format(new Date());
    private static Boolean following = false;
    private CollapsingToolbarLayout toolbarLayout;

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
        name = findViewById(R.id.profileName);
        bio = findViewById(R.id.profileBio);
        followersCount = findViewById(R.id.fllwNb2);
        followingCount = findViewById(R.id.fllwingNb2);
        toolbarLayout = findViewById(R.id.CollapsingToolBarLayout2);

        usernameTxt.setText("@"+username);
        toolbarLayout.setTitle(username);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();

        GetCurrentUsername();

        DocumentReference df = fstore.collection("Users").document(uid);
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        if (doc.get("Name")!= null && doc.get("Bio")!=null) {
                            name.setText(doc.get("Name").toString());
                            bio.setText(doc.get("Bio").toString());
                        }
                    }
                }
            }
        });

        Task<QuerySnapshot> followingReference = df.collection("following").
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size()>0) {
                        int size = task.getResult().size();
                        followingCount.setText(Integer.toString(size));
                    }
                }
            }
        });


        Task<QuerySnapshot> followerReference = df.collection("followers").
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size()>0) {
                        int size = task.getResult().size();
                        followersCount.setText(Integer.toString(size));
                    }
                }
            }
        });

        RunCheck(); //check if the current user (logged in) if following the user (user we're looking at) or not
        //if he's following him then he will get the option to follow
        //else he'll get the option to follow him
        //we will need a recursive method to keep updating checks


    }

    private void RunCheck()
    {
        //we need this to update what our current user is following
        DocumentReference currentUserRef = fstore.collection("Users").document(user.getUid()).
                collection("following").document(uid);
        currentUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override //check if the document exists, i.e current user follow the user
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        following = true;
                        followBtn.setText("Unfollow");
                        followBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //we need this to update the followers of the user we're watching
                                DocumentReference userRef = fstore.collection("Users").document(uid).
                                        collection("followers").document(user.getUid());
                                userRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {  //delete a current user from followers lust
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        following=false;
                                        followBtn.setText("Follow");
                                    }
                                });
                                currentUserRef.delete().addOnFailureListener(new OnFailureListener() { //delete user from list that current user follows
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        following = true;
                                        followBtn.setText("Unfollow");
                                    }
                                });
                            }
                        });
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
                                //add a follower to user

                                Map<String,Object> currUserInfor = new HashMap<>(); //represents key, value
                                currUserInfor.put("Username", username);
                                currUserInfor.put("followingDate", date);
                                currUserInfor.put("uid", uid);

                                currentUserRef.set(currUserInfor); //pass our map to the fb document
                                //add what our current user is following
                            }
                        });
                    }
                    RunCheck(); //recursive method to keep checking
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

//TODO: retrieve the name