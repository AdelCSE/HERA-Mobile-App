package dz.esisba.a2cpi_project.navigation_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dz.esisba.a2cpi_project.R;

public class CharityFragment extends Fragment {

    View parentHolder;
    private ImageButton backBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_charity,container,false);

        backBtn = parentHolder.findViewById(R.id.charityBackBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomNavigationView bottomNavigationView;
                bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation_layout);
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        });


        return parentHolder;
    }


}
