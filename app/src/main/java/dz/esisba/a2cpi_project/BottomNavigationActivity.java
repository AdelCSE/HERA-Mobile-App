package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import dz.esisba.a2cpi_project.interfaces.GetUserInterface;
import dz.esisba.a2cpi_project.models.UserModel;
import dz.esisba.a2cpi_project.navigation_fragments.AddPostFragment;
import dz.esisba.a2cpi_project.navigation_fragments.HomeFragment;
import dz.esisba.a2cpi_project.navigation_fragments.CharityFragment;
import dz.esisba.a2cpi_project.navigation_fragments.ProfileFragment;
import dz.esisba.a2cpi_project.navigation_fragments.SmartRoomFragment;

public class BottomNavigationActivity extends AppCompatActivity implements GetUserInterface {

    private FirebaseAuth auth;
    private FirebaseUser user;
    UserModel currUser;
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
            }
        });

        //get current user model
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        CheckNetwork();


        user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (user == null) {
                    startActivity(new Intent(BottomNavigationActivity.this, LoginActivity.class));
                    finish();
                } else if (!user.isEmailVerified()) {
                    startActivity(new Intent(BottomNavigationActivity.this, VerificationActivity.class));
                    finish();
                }
            }
        });

        DocumentReference df = fstore.collection("Users").document(user.getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        currUser = doc.toObject(UserModel.class);
                    }
                }
            }
        });
        //TODO: FIX https://stackoverflow.com/questions/57861254/how-to-change-a-specific-icon-image-from-bottom-navigation-view
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        if (doc.get("profilePictureUrl") != null) {
                            downloadUrl = doc.get("profilePictureUrl").toString();
                            RequestOptions myOptions = new RequestOptions()
                                    .centerCrop()
                                    .override(50, 50);
                            Glide.with(getApplicationContext()).asBitmap().apply(myOptions).circleCrop().load(downloadUrl)
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
    }

    private void CheckNetwork() {
        if (!isNetworkAvailable()) {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(parentLayout, "⚠️ Please check you internet connection and try again.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("REFRESH", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckNetwork();
                }
            }).show();
        }
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
                            selectedFragment = new CharityFragment();
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

    @Override
    public UserModel getUserModel() {
        return currUser;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}