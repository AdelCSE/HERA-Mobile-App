package dz.esisba.a2cpi_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import dz.esisba.a2cpi_project.adapter.SmartRoomPagerAdapter;

public class SmartRoom extends AppCompatActivity {

    ViewPager2 viewPager;
    TabLayout tabLayout;
    SmartRoomPagerAdapter adapter;

    private String[] titles = {"Notifications" , "Statistics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_room);


        tabLayout = findViewById(R.id.tabLayout);

        adapter = new SmartRoomPagerAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(titles[position]))).attach();

    }
}