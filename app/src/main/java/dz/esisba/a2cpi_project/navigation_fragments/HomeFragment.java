package dz.esisba.a2cpi_project.navigation_fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.SearchActivity;

public class HomeFragment extends Fragment implements View.OnClickListener {
    View parentHolder;
    ImageButton searchbtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_home,container,false);

        searchbtn = (ImageButton) parentHolder.findViewById(R.id.search_btn);
        searchbtn.setOnClickListener(this);

        return parentHolder;
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(getActivity(), SearchActivity.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);

    }
}
