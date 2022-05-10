package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.adapter.UserProfileAdapter;
import dz.esisba.a2cpi_project.models.UserModel;

public class UserProfileActivity extends AppCompatActivity {
    private static String uid,currentUsername = "";
    private TextView usernameTxt,name, bio, followersCount, followingCount;
    private Button followBtn;
    private static  String date = DateFormat.getInstance().format(new Date());
    private static Boolean following = false;
    private CollapsingToolbarLayout toolbarLayout;
    private CircleImageView profilePic;
    private ImageView banner;

    private UserModel userModel;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;

    ViewPager2 viewPager;
    TabLayout tabLayout;
    UserProfileAdapter adapter;
    Toolbar toolbar;


    private String[] titles = {"All" , "Questions" , "Answers"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        viewPager = findViewById(R.id.viewPagerUp);
        tabLayout = findViewById(R.id.tabLayoutUp);
        toolbar = findViewById(R.id.ToolBarUp);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        adapter = new UserProfileAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(titles[position]))).attach();


        userModel = (UserModel) getIntent().getSerializableExtra("Tag");
        uid = userModel.getUid();

        followBtn = findViewById(R.id.followBtn);
        name = findViewById(R.id.profileName);
        usernameTxt = findViewById(R.id.usernameTxt);
        bio = findViewById(R.id.profileBio);
        followersCount = findViewById(R.id.fllwNb);
        followingCount = findViewById(R.id.fllwingNb);
        toolbarLayout = findViewById(R.id.CollapsingToolBarLayout2);
        profilePic = findViewById(R.id.profilePic);
        banner = findViewById(R.id.banner);

        SetUserInfo();


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();

        GetCurrentUsername();

        DocumentReference df = fstore.collection("Users").document(uid);

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

    private void SetUserInfo()
    {
        toolbarLayout.setTitle(userModel.getUsername());
        usernameTxt.setText("@"+userModel.getUsername());
        name.setText(userModel.getName());
        bio.setText(userModel.getBio());
        Glide.with(UserProfileActivity.this).load(userModel.getProfilePictureUrl()).into(profilePic);
        Glide.with(UserProfileActivity.this).load(userModel.getBannerUrl()).into(banner);
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
                                        int i = Integer.parseInt(followersCount.getText().toString());
                                        i--;
                                        followersCount.setText(Integer.toString(i));
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
                                int i = Integer.parseInt(followersCount.getText().toString());
                                i++;
                                followersCount.setText(Integer.toString(i));
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
                                currUserInfor.put("Username", userModel.getUsername());
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
//TODO: add followers and following count