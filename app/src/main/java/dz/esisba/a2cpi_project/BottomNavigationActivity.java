package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import dz.esisba.a2cpi_project.navigation_fragments.AddPostFragment;
import dz.esisba.a2cpi_project.navigation_fragments.HomeFragment;
import dz.esisba.a2cpi_project.navigation_fragments.NotificationsFragment;
import dz.esisba.a2cpi_project.navigation_fragments.ProfileFragment;
import dz.esisba.a2cpi_project.navigation_fragments.SmartRoomFragment;

public class BottomNavigationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference askedByRef;
    private String downloadUrl;

    private ImageView profileImg;
    private Drawable icon;

    BottomNavigationView bottomNav;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        //setEnabled(false) for the 2nd index because we're using a floating button instead
        bottomNav = findViewById(R.id.bottom_navigation_layout);
        bottomNav.setBackground(null);
        bottomNav.getMenu().getItem(2).setEnabled(false);

        bottomNav.setOnItemSelectedListener(navListener);

        //Here we're setting the home fragment as default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

        //start AddPostActivity class
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPostActivity.class));
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        DocumentReference df = fstore.collection("Users").document(user.getUid());
        //grab the existing info
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        if (doc.get("profilePictureUrl")!= null) {
                            downloadUrl = doc.get("profilePictureUrl").toString();
                            Glide.with(getApplicationContext()).asBitmap().circleCrop().load(downloadUrl)
                                    .into(new CustomTarget<Bitmap>() {

                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            bottomNav.setItemIconTintList(null);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                bottomNav.getMenu().getItem(4).setIconTintList(null);
                                            }
                                            Drawable profileImage = new BitmapDrawable(getResources(), resource);
                                            bottomNav.getMenu().getItem(4).setIcon(profileImage);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });
                        }
                    }
                }
            }
        });
        bottomNav.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                bottomNav.getMenu().getItem(2).setIcon(R.drawable.ic_arrow);
                return false;
            }
        });

    }

    //Switch between fragments
    private BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_sr:
                            selectedFragment = new SmartRoomFragment();
                            break;
                        case R.id.nav_notif:
                            selectedFragment = new NotificationsFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}