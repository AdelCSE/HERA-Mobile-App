package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.adapter.QuestionBlocAdapter;
import dz.esisba.a2cpi_project.adapter.QuestionBlocAdapter;
import dz.esisba.a2cpi_project.interfaces.OnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionBlocActivity extends AppCompatActivity implements OnItemClickListner, PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerView;
    private ArrayList<PostModel> postsDataHolder;
    private ImageButton AnswerBtn;
    private QuestionBlocAdapter adapter;
    private BottomSheetDialog dialog;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private String askedByName,askedByUsername = "";
    private DocumentReference userRef;
    private String downloadUrl;
    private PostModel post;
    private int count;
    private DocumentReference postRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_bloc);

        setAnswerBtn(findViewById(R.id.answerBtn));
        setDialog(new BottomSheetDialog(this));

//comment
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        userRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        //get username of poster
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(this.toString(), "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    askedByUsername = snapshot.get("Username").toString();
                    if (snapshot.get("profilePictureUrl") != null) {
                        downloadUrl = snapshot.get("profilePictureUrl").toString();
                    }
                    if (snapshot.get("Name")!= null)
                    {
                        askedByName = snapshot.get("Name").toString();
                    }
                }
                else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });

        post = (PostModel) getIntent().getSerializableExtra("Tag");
        postRef = fstore.collection("Posts").document(post.getPostid());
        count = post.getAnswersCount();
        postsDataHolder = new ArrayList<>();

        FetchAnswers();

    }

    //Display answers
    private void FetchAnswers()
    {
        postsDataHolder = new ArrayList<>();
        postsDataHolder.add(post);

        PostModel text = new PostModel(null,null,null,null,null,null,null,null,-1,post.getAnswersCount());
        postsDataHolder.add(text);

        postRef.collection("Answers")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel answer = document.toObject(PostModel.class);
                        answer.setAnswersCount(-1);
                        postsDataHolder.add(answer);
                    }
                    buildRecyclerView();
                } else {
                    Toast.makeText(QuestionBlocActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Add an Answer
    @Override
    public void onAnswerClick(int position) {
        showAnswerDialog();
        getDialog().show();
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    //Share APP
    @Override
    public void onShareClick(int position) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String Body = "Download this app";
        intent.putExtra(Intent.EXTRA_TEXT,Body);
        intent.putExtra(Intent.EXTRA_TEXT,"URL");
        startActivity(Intent.createChooser(intent,"Share using"));
    }

    @Override
    public void onMenuClick(int position,View v) {
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.post_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }


    public void buildRecyclerView(){

        setRecyclerView(findViewById(R.id.recviewa));
        getRecyclerView().setLayoutManager(new LinearLayoutManager(this));

        adapter = new QuestionBlocAdapter(postsDataHolder,this);
        recyclerView.setAdapter(adapter);
    }

    String date = DateFormat.getInstance().format(new Date());

    public void showAnswerDialog(){
        View view = getLayoutInflater().inflate(R.layout.activty_add_answer,null,false);

        ImageButton closeAnswerBtn = view.findViewById(R.id.closeAnswerBtn);
        EditText addAnswer =  view.findViewById(R.id.answerEditTxt);
        TextView postAnswerBtn =  view.findViewById(R.id.postAnswerBtn);
        CircleImageView profileimg = view.findViewById(R.id.bottomsheetimg);

        Glide.with(QuestionBlocActivity.this).load(downloadUrl).into(profileimg);

        closeAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });


        postAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!addAnswer.getText().toString().isEmpty())
                {
                    PerformValidation(addAnswer.getText().toString());
                }else {
                    getDialog().dismiss();
                    startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
                    finish();
                }
            }
        });

        getDialog().setContentView(view);
    }

    private void PerformValidation(String answer)
    {
        String answerId = post.getPostid() + Integer.toString(post.getAnswersCount());
        Toast.makeText(QuestionBlocActivity.this, "Posting...", Toast.LENGTH_SHORT).show();
        HashMap<String, Object> data = new HashMap<>();
        data.put("postid", answerId);
        data.put("publisherPic", downloadUrl);
        data.put("body", answer);
        data.put("publisher", user.getUid().toString());
        data.put("answerBy", askedByName);
        data.put("Username", askedByUsername);
        data.put("Date", date);
        data.put("likesCount", 0);

        DocumentReference df = postRef.collection("Answers").document(answerId);
        df.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Task<QuerySnapshot> followingReference = postRef.collection("Answers")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        count = task.getResult().size();
                                        HashMap<String, Object> hm = new HashMap<>();
                                        hm.put("answersCount", count);
                                        postRef.update(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(QuestionBlocActivity.this, "Answer posted successfully", Toast.LENGTH_SHORT).show();
                                                getDialog().dismiss();
                                                startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
                                                finish();
                                            }
                                        });

                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(QuestionBlocActivity.this, "Could not post answer " + task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public ArrayList<PostModel> getPostsDataHolder() {
        return postsDataHolder;
    }

    public void setPostsDataHolder(ArrayList<PostModel> postsDataHolder) {
        this.postsDataHolder = postsDataHolder;
    }

    public ImageButton getAnswerBtn() {
        return AnswerBtn;
    }

    public void setAnswerBtn(ImageButton answerBtn) {
        AnswerBtn = answerBtn;
    }

    public QuestionBlocAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(QuestionBlocAdapter adapter) {
        this.adapter = adapter;
    }

    public BottomSheetDialog getDialog() {
        return dialog;
    }

    public void setDialog(BottomSheetDialog dialog) {
        this.dialog = dialog;
    }


}