package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dz.esisba.a2cpi_project.navigation_fragments.AddPostFragment;
import dz.esisba.a2cpi_project.navigation_fragments.HomeFragment;
import dz.esisba.a2cpi_project.navigation_fragments.NotificationsFragment;
import dz.esisba.a2cpi_project.navigation_fragments.ProfileFragment;
import dz.esisba.a2cpi_project.navigation_fragments.SmartRoomFragment;

public class BottomNavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_layout);

        //setEnabled(false) for the 2nd index because we're using a floating button instead
        bottomNav.setBackground(null);
        bottomNav.getMenu().getItem(2).setEnabled(false);

        bottomNav.setOnItemSelectedListener(navListener);

        //Here we're setting the home fragment as default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
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