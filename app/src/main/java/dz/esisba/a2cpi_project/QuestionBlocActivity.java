package dz.esisba.a2cpi_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.adapter.AllPostsAdapter;
import dz.esisba.a2cpi_project.adapter.PostAdapter;
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionBlocActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PostModel> PostsDataHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_bloc);

        recyclerView = findViewById(R.id.recviewa);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        PostsDataHolder = new ArrayList<>();

        PostModel Post1 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question1" , "details here","1000","500","11:11 AM • 29 APR 22");
        PostsDataHolder.add(Post1);
        PostModel Post2 = new PostModel(R.drawable.exemple, "Yassine Benyamina" , "@yassine" , "What's your Question2" , "details here","1000",null,"11:08 AM • 29 APR 22");
        PostsDataHolder.add(Post2);
        PostModel Post3 = new PostModel(R.drawable.exemple, "Chifa Ribel Belarbi" , "@ribel" , "What's your Question3" , "details here","1000",null,"10:45 AM • 29 APR 22");
        PostsDataHolder.add(Post3);
        PostModel Post4 = new PostModel(R.drawable.exemple, "Yacine Dait dehane" , "@yac_ine" , "What's your Question4" , "details here","1000",null,"10:30 AM • 29 APR 22");
        PostsDataHolder.add(Post4);
        PostModel Post5 = new PostModel(R.drawable.exemple, "Rachid Benayad" , "@rachide" , "What's your Question5" , "details here","1000",null,"08:25 AM • 29 APR 22");
        PostsDataHolder.add(Post5);
        PostModel Post6 = new PostModel(R.drawable.exemple, "Rachid Benayad" , "@rachide" , "What's your Question5" , "details here","1000",null,"08:25 AM • 29 APR 22");
        PostsDataHolder.add(Post6);

        recyclerView.setAdapter(new AllPostsAdapter(PostsDataHolder));

    }
}