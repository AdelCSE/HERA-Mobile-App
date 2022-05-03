package dz.esisba.a2cpi_project.navigation_fragments.profile_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.AllPostsAdapter;
import dz.esisba.a2cpi_project.adapter.AnswersAdapter;
import dz.esisba.a2cpi_project.models.PostModel;


public class AnswersFragment extends Fragment {

    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<PostModel> AnswersDataHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_answers, container, false);

        recyclerView = parentHolder.findViewById(R.id.recviewAnswers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AnswersDataHolder = new ArrayList<>();

        PostModel Post1 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question1" , "details here","1000",null,"11:11 AM • 29 APR 22");
        AnswersDataHolder.add(Post1);
        PostModel Post2 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question2" , "details here","1000",null,"11:08 AM • 29 APR 22");
        AnswersDataHolder.add(Post2);
        PostModel Post3 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question3" , "details here","1000",null,"10:45 AM • 29 APR 22");
        AnswersDataHolder.add(Post3);
        PostModel Post4 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question4" , "details here","1000",null,"10:30 AM • 29 APR 22");
        AnswersDataHolder.add(Post4);
        PostModel Post5 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question5" , "details here","1000",null,"08:25 AM • 29 APR 22");
        AnswersDataHolder.add(Post5);

        recyclerView.setAdapter(new AnswersAdapter(AnswersDataHolder));

        return parentHolder;
    }
}