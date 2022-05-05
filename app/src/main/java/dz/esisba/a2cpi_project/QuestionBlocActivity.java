package dz.esisba.a2cpi_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.adapter.AllPostsAdapter;
import dz.esisba.a2cpi_project.interfaces.OnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionBlocActivity extends AppCompatActivity implements OnItemClickListner {

    RecyclerView recyclerView;
    ArrayList<PostModel> PostsDataHolder;
    ImageButton AnswerBtn;
    AllPostsAdapter adapter;
    BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_bloc);

        AnswerBtn = findViewById(R.id.answerBtn);
        dialog= new BottomSheetDialog(this);

        buildRecyclerView();

    }

    @Override
    public void onAnswerClick(int position) {
        showAnswerDialog();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    public void buildRecyclerView(){

        recyclerView = findViewById(R.id.recviewa);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PostsDataHolder = new ArrayList<>();

        PostModel post = (PostModel) getIntent().getSerializableExtra("Tag");
        PostsDataHolder.add(post);

        adapter = new AllPostsAdapter(PostsDataHolder,this);
        recyclerView.setAdapter(adapter);
    }


    public void showAnswerDialog(){
        View view = getLayoutInflater().inflate(R.layout.activty_add_answer,null,false);

        ImageButton closeAnswerBtn = (ImageButton) view.findViewById(R.id.closeAnswerBtn);
        EditText addAnswer = (EditText) view.findViewById(R.id.answerEditTxt);
        TextView postAnswerBtn = (TextView) view.findViewById(R.id.postAnswerBtn);
        ImageView profileimg = view.findViewById(R.id.bottomsheetimg);

        closeAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        postAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(QuestionBlocActivity.this, "Posting...", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setContentView(view);
    }
}