package dz.esisba.a2cpi_project.navigation_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.ProfileAdapter;
import dz.esisba.a2cpi_project.adapter.SmartRoomPagerAdapter;

public class ProfileFragment extends Fragment {

    View Holder;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    ProfileAdapter adapter;

    private String[] titles = {"All" , "Questions" , "Answers" , "Requests"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Holder = inflater.inflate(R.layout.fragment_profile,container,false);

        viewPager = Holder.findViewById(R.id.viewPagerp);
        tabLayout = Holder.findViewById(R.id.tabLayoutp);

        adapter = new ProfileAdapter(getChildFragmentManager(),getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(titles[position]))).attach();


        return Holder;
    }
}
