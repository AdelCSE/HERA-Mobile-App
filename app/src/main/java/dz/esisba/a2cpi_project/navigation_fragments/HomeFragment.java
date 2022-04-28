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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.SearchActivity;
import dz.esisba.a2cpi_project.adapter.PostAdapter;
import dz.esisba.a2cpi_project.models.PostModel;

public class HomeFragment extends Fragment{
    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<PostModel> PostsDataHolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_home,container,false);

        recyclerView = parentHolder.findViewById(R.id.recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PostsDataHolder = new ArrayList<>();

        PostModel Post1 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "addy1001" , "What's your Question1" , "details here","1000","500");
        PostsDataHolder.add(Post1);
        PostModel Post2 = new PostModel(R.drawable.exemple, "Yassine Benyamina" , "yassine" , "What's your Question2" , "details here","1000","500");
        PostsDataHolder.add(Post2);
        PostModel Post3 = new PostModel(R.drawable.exemple, "Chifa Ribel Belarbi" , "ribel" , "What's your Question3" , "details here","1000","500");
        PostsDataHolder.add(Post3);
        PostModel Post4 = new PostModel(R.drawable.exemple, "Yacine Dait Dehane" , "yacine" , "What's your Question4" , "details here","1000","500");
        PostsDataHolder.add(Post4);
        PostModel Post5 = new PostModel(R.drawable.exemple, "Rachid Benayad" , "rachide" , "What's your Question5" , "details here","1000","500");
        PostsDataHolder.add(Post5);

        recyclerView.setAdapter(new PostAdapter(PostsDataHolder));


        return parentHolder;
    }

}
