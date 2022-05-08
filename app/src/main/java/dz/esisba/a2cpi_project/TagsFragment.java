package dz.esisba.a2cpi_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import dz.esisba.a2cpi_project.interfaces.OnItemClickListner;
import dz.esisba.a2cpi_project.interfaces.PostsOnItemClickListner;
import dz.esisba.a2cpi_project.interfaces.QuestionsOnItemClickListner;

public class TagsFragment extends Fragment implements QuestionsOnItemClickListner {

    View parentHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_tags, container, false);


        return parentHolder;
    }

    @Override
    public void onAnswerClick(int position) {

    }
}