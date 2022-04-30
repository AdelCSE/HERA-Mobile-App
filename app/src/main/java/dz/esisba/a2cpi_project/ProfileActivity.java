package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private StorageReference storageReference;
    private Uri resultUri;

    private TextView username, name, bio, followersCount, followingCount;
    private ImageButton editProfile;
    private CollapsingToolbarLayout toolbarLayout;
    private CircleImageView profileImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.usernameTxt);
        name = findViewById(R.id.nameText);
        bio = findViewById(R.id.bioText);
        followersCount = findViewById(R.id.fllwNb2);
        followingCount = findViewById(R.id.fllwingNb2);
        editProfile = findViewById(R.id.editProfile);
        toolbarLayout = findViewById(R.id.CollapsingToolBarLayout);
        profileImg = findViewById(R.id.profileImg);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");

        DocumentReference df = fstore.collection("Users").document(user.getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        username.setText("@"+ doc.get("Username").toString());
                        toolbarLayout.setTitle(doc.get("Username").toString());
                        String downloadUrl = doc.get("profilePictureUrl").toString();
                        Glide.with(ProfileActivity.this).load(downloadUrl).into(profileImg);
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


        Toolbar toolbar = findViewById(R.id.ToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingtoolbarlayout = findViewById(R.id.CollapsingToolBarLayout);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.exemple);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                if (palette != null){
                    collapsingtoolbarlayout.setContentScrimColor(palette.getMutedColor(androidx.appcompat.R.attr.colorPrimary));
                }
            }
        });

    }
}