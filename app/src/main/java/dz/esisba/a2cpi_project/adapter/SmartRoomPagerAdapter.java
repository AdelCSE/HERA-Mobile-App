package dz.esisba.a2cpi_project.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import dz.esisba.a2cpi_project.navigation_fragments.NotificationsFragment;
import dz.esisba.a2cpi_project.navigation_fragments.SmartRoomFragment;
import dz.esisba.a2cpi_project.navigation_fragments.smart_room_fragments.SmartRoomNotificationsFragment;
import dz.esisba.a2cpi_project.navigation_fragments.smart_room_fragments.SmartRoomStatisticsFragment;

public class SmartRoomPagerAdapter extends FragmentStateAdapter{

    private String[] titles = {"Notifications" , "Statistics"};

    public SmartRoomPagerAdapter(@NonNull FragmentManager fragment,Lifecycle lifecycle) {
        super(fragment,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new SmartRoomNotificationsFragment();
            case 1:
                return new SmartRoomStatisticsFragment();
        }
        return new NotificationsFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

}
