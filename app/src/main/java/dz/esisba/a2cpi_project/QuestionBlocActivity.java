package dz.esisba.a2cpi_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.Inflater;

import dz.esisba.a2cpi_project.adapter.AllPostsAdapter;
import dz.esisba.a2cpi_project.adapter.PostAdapter;
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionBlocActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PostModel> PostsDataHolder;
    ImageButton AnswerBtn;
    AllPostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_bloc);

        AnswerBtn = findViewById(R.id.answerBtn);

        recyclerView = findViewById(R.id.recviewa);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        PostsDataHolder = new ArrayList<>();
        PostModel post = (PostModel) getIntent().getSerializableExtra("Tag");


        PostsDataHolder.add(post);
        /*PostModel Post2 = new PostModel(R.drawable.exemple, "Yassine Benyamina" , "@yassine" , "What's your Question2" , "details here","1000",null,"11:08 AM • 29 APR 22");
        PostsDataHolder.add(Post2);
        PostModel Post3 = new PostModel(R.drawable.exemple, "Chifa Ribel Belarbi" , "@ribel" , "What's your Question3" , "details here","1000",null,"10:45 AM • 29 APR 22");
        PostsDataHolder.add(Post3);
        PostModel Post4 = new PostModel(R.drawable.exemple, "Yacine Dait dehane" , "@yac_ine" , "What's your Question4" , "details here","1000",null,"10:30 AM • 29 APR 22");
        PostsDataHolder.add(Post4);
        PostModel Post5 = new PostModel(R.drawable.exemple, "Rachid Benayad" , "@rachide" , "What's your Question5" , "details here","1000",null,"08:25 AM • 29 APR 22");
        PostsDataHolder.add(Post5);
        PostModel Post6 = new PostModel(R.drawable.exemple, "Rachid Benayad" , "@rachide" , "What's your Question5" , "details here","1000",null,"08:25 AM • 29 APR 22");
        PostsDataHolder.add(Post6);*/

        /*if (PostsDataHolder.size()>=1){
            recyclerView.setAdapter(new AllPostsAdapter(PostsDataHolder));

            adapter = new AllPostsAdapter(PostsDataHolder);

            adapter.setOnItemClickListner(new AllPostsAdapter.OnItemClickListner() {
                @Override
                public void onAnswerClick(int position) {
                    Toast.makeText(QuestionBlocActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    showAnswerDialog();
                }
            });
        }
    }

    public void showAnswerDialog(){
        LayoutInflater inflater;
        inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.activty_add_answer, null);

        /*final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activty_add_answer);

        ImageButton closeAnswer = findViewById(R.id.closeAnswerBtn);
        EditText writeAnswer = findViewById(R.id.answerEditTxt);
        TextView postAnswer = findViewById(R.id.postAnswerBtn);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);*/
    }
}