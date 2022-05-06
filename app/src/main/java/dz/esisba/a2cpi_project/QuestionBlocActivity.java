package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.adapter.AllPostsAdapter;
import dz.esisba.a2cpi_project.interfaces.OnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionBlocActivity extends AppCompatActivity implements OnItemClickListner {

    private RecyclerView recyclerView;
    private ArrayList<PostModel> postsDataHolder;
    private ImageButton AnswerBtn;
    private AllPostsAdapter adapter;
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
        Toast.makeText(this, post.getPostid(), Toast.LENGTH_SHORT).show();
        postRef = fstore.collection("Posts").document(post.getPostid());
        count = post.getAnswersCount();

        buildRecyclerView();


    }

    @Override
    public void onAnswerClick(int position) {
        showAnswerDialog();
        getDialog().show();
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    public void buildRecyclerView(){

        setRecyclerView(findViewById(R.id.recviewa));
        getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        setPostsDataHolder(new ArrayList<>());

        getPostsDataHolder().add(post);

        setAdapter(new AllPostsAdapter(getPostsDataHolder(),this));
        getRecyclerView().setAdapter(getAdapter());
    }

    String date = DateFormat.getInstance().format(new Date());

    public void showAnswerDialog(){
        View view = getLayoutInflater().inflate(R.layout.activty_add_answer,null,false);

        ImageButton closeAnswerBtn = (ImageButton) view.findViewById(R.id.closeAnswerBtn);
        EditText addAnswer = (EditText) view.findViewById(R.id.answerEditTxt);
        TextView postAnswerBtn = (TextView) view.findViewById(R.id.postAnswerBtn);
        CircleImageView profileimg = view.findViewById(R.id.bottomsheetimg);

        Glide.with(QuestionBlocActivity.this).load(downloadUrl).into(profileimg);

        closeAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        DocumentReference ref = FirebaseFirestore.getInstance().collection("Posts")
                .document(post.getPostid()).collection("Answers").document(); //generate unique id
        String answerId = ref.getId();

        postAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!addAnswer.getText().toString().isEmpty())
                {
                    Toast.makeText(QuestionBlocActivity.this, "Posting...", Toast.LENGTH_SHORT).show();
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("postid", answerId);
                    data.put("publisherPic", downloadUrl);
                    data.put("body", addAnswer.getText().toString());
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
                                count++;
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
                            else
                            {
                                Toast.makeText(QuestionBlocActivity.this, "Could not post answer " + task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    getDialog().dismiss();
                    startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
                    finish();
                }
            }
        });

        getDialog().setContentView(view);
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

    public AllPostsAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(AllPostsAdapter adapter) {
        this.adapter = adapter;
    }

    public BottomSheetDialog getDialog() {
        return dialog;
    }

    public void setDialog(BottomSheetDialog dialog) {
        this.dialog = dialog;
    }
}