package dz.esisba.a2cpi_project.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import dz.esisba.a2cpi_project.navigation_fragments.CharityFragment;
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
        return new CharityFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

}
