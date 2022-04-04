package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dz.esisba.a2cpi_project.navigation_fragments.AddPostFragment;
import dz.esisba.a2cpi_project.navigation_fragments.HomeFragment;
import dz.esisba.a2cpi_project.navigation_fragments.NotificationsFragment;
import dz.esisba.a2cpi_project.navigation_fragments.ProfileFragment;
import dz.esisba.a2cpi_project.navigation_fragments.SmartRoomFragment;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2);

        bottomNavigationView.setOnItemSelectedListener(navListener);

    }
    private BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.nav_home:
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                            break;
                        case R.id.nav_notif:
                            startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                            finish();
                            break;
                        case R.id.nav_profile:
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            finish();
                            break;
                    }

                    return true;
                }
            };

}