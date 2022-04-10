package dz.esisba.a2cpi_project.navigation_fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.SmartRoomPagerAdapter;
import dz.esisba.a2cpi_project.navigation_fragments.smart_room_fragments.SmartRoomNotificationsFragment;
import dz.esisba.a2cpi_project.navigation_fragments.smart_room_fragments.SmartRoomStatisticsFragment;

public class SmartRoomFragment extends Fragment {

    View Holder;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    SmartRoomPagerAdapter adapter;

    private String[] titles = {"Notifications" , "Statistics"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Holder = inflater.inflate(R.layout.fragment_smart_room,container,false);

        viewPager = Holder.findViewById(R.id.viewPager);
        tabLayout = Holder.findViewById(R.id.tabLayout);

        adapter = new SmartRoomPagerAdapter(getChildFragmentManager(),getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(titles[position]))).attach();

        return Holder;
    }

}
