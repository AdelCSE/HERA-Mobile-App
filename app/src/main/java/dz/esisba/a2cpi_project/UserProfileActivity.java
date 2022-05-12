package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.adapter.UserProfileAdapter;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.UserModel;

public class UserProfileActivity extends AppCompatActivity {
    private TextView usernameTxt,name, bio, followersCount, followingCount;
    private Button followBtn;
    private static  String date = DateFormat.getInstance().format(new Date());
    private static Boolean following = false;
    private CollapsingToolbarLayout toolbarLayout;
    private CircleImageView profilePic;
    private ImageView banner;

    private UserModel userModel, currentUserModel;
    private PostModel postModel;

    FirebaseAuth auth;
    FirebaseUser user,user1;
    FirebaseFirestore fstore;


    ViewPager2 viewPager;
    TabLayout tabLayout;
    UserProfileAdapter adapter;
    Toolbar toolbar;


    private String[] titles = {"All" , "Questions" , "Answers"};
    private ArrayList<String> currUserFollowers, currUserFollowing;

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

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();



        followBtn = findViewById(R.id.followBtn);
        name = findViewById(R.id.profileName);
        usernameTxt = findViewById(R.id.usernameTxt);
        bio = findViewById(R.id.profileBio);
        followersCount = findViewById(R.id.fllwNb);
        followingCount = findViewById(R.id.fllwingNb);
        toolbarLayout = findViewById(R.id.CollapsingToolBarLayout2);
        profilePic = findViewById(R.id.profilePic);
        banner = findViewById(R.id.banner);


        userModel = (UserModel) getIntent().getSerializableExtra("Tag");

        DocumentReference userRef = fstore.collection("Users").document(userModel.getUid());
        DocumentReference currentUserRef = fstore.collection("Users").document(user.getUid());

        GetCurrentUserModel();
        SetUserInfo();

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followBtn.getTag().equals("follow"))
                {
                    followBtn.setText("Unfollow");
                    followBtn.setTag("following");

                    ArrayList<String> following = new ArrayList<>();
                    ArrayList<String> followers = new ArrayList<>();
                    followers = userModel.getFollowers();
                    following = currentUserModel.getFollowing();


                    following.add(userModel.getUid()); //add following to current user
                    followers.add(user.getUid());//add follower to user

                    Map<String,Object> currUser = new HashMap();
                    Map<String,Object> user = new HashMap();
                    
                    currUser.put("following", following);
                    user.put("followers", followers);
                    
                    currentUserRef.update(currUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            userRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    int i = Integer.parseInt(followersCount.getText().toString());
                                    i++;
                                    followersCount.setText(Integer.toString(i)); //update count
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                            followBtn.setText("Follow");
                            followBtn.setTag("follow");
                        }
                    });
                }
                else if (followBtn.getTag().equals("following"))
                {
                    followBtn.setTag("follow");
                    followBtn.setText("Follow");

                    ArrayList<String> following = currentUserModel.getFollowing();
                    ArrayList<String> followers = userModel.getFollowers();
                    following.remove(userModel.getUid()); //remove following to current user
                    followers.remove(user.getUid());//remove follower to user

                    Map<String,Object> currUser = new HashMap();
                    Map<String,Object> user = new HashMap();

                    currUser.put("following", following);
                    user.put("followers", followers);

                    currentUserRef.update(currUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            userRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    int i = Integer.parseInt(followersCount.getText().toString());
                                    i--;
                                    followersCount.setText(Integer.toString(i)); //update count
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                            followBtn.setText("Unfollow");
                            followBtn.setTag("following");
                        }
                    });
                }
            }
        });

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
        ArrayList<String> following = currentUserModel.getFollowing();
        if (following.contains(userModel.getUid()))
        {
            followBtn.setTag("following");
            followBtn.setText("Unfollow");
        }
        else
        {
            followBtn.setTag("follow");
        }
    }

    private void GetCurrentUserModel()
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
                        currentUserModel =  doc.toObject(UserModel.class);
                        currUserFollowers = userModel.getFollowers();
                        currUserFollowing = userModel.getFollowing();

                        if (currUserFollowers!=null) {
                            String i = Integer.toString(currUserFollowers.size());
                            followersCount.setText(i);
                        }
                        if (currUserFollowing!=null) {
                            String j = Integer.toString(currUserFollowing.size());
                            followingCount.setText(j);
                        }

                        RunCheck(); //check if the current user (logged in) if following the user (user we're looking at) or not
                        //if he's following him then he will get the option to unfollow
                    }
                }
            }
        });
    }
}

//TODO: retrieve the name
//TODO: add followers and following count