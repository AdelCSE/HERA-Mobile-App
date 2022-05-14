package dz.esisba.a2cpi_project.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import dz.esisba.a2cpi_project.navigation_fragments.profile_fragments.AllFragment;
import dz.esisba.a2cpi_project.navigation_fragments.profile_fragments.AnswersFragment;
import dz.esisba.a2cpi_project.navigation_fragments.profile_fragments.QuestionsFragment;
import dz.esisba.a2cpi_project.navigation_fragments.profile_fragments.RequestsFragment;

public class ProfileAdapter extends FragmentStateAdapter {

    private String[] titles = {"All" , "Questions" , "Answers" , "Requests"};

    public ProfileAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new AllFragment();
            case 1:
                return new QuestionsFragment();
            case 2:
                return new AnswersFragment();
            case 3:
                return new RequestsFragment();
        }
        return new AllFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
