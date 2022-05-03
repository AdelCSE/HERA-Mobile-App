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
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionsFragment extends Fragment {

    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<PostModel> QuestionsDataHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_questions, container, false);

        recyclerView = parentHolder.findViewById(R.id.recviewQuestions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        QuestionsDataHolder = new ArrayList<>();

        PostModel Post1 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question1" , "details here","1000","500","11:11 AM • 29 APR 22");
        QuestionsDataHolder.add(Post1);
        PostModel Post2 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question2" , "details here","1000","500","11:08 AM • 29 APR 22");
        QuestionsDataHolder.add(Post2);
        PostModel Post3 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question3" , "details here","1000","500","10:45 AM • 29 APR 22");
        QuestionsDataHolder.add(Post3);
        PostModel Post4 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question4" , "details here","1000","500","10:30 AM • 29 APR 22");
        QuestionsDataHolder.add(Post4);
        PostModel Post5 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question5" , "details here","1000","500","08:25 AM • 29 APR 22");
        QuestionsDataHolder.add(Post5);

        recyclerView.setAdapter(new AllPostsAdapter(QuestionsDataHolder));

        return parentHolder;
    }
}