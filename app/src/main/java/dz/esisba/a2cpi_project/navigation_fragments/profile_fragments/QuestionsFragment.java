package dz.esisba.a2cpi_project.navigation_fragments.profile_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

import dz.esisba.a2cpi_project.QuestionBlocActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.PostAdapter;
import dz.esisba.a2cpi_project.interfaces.PostsOnItemClickListner;
import dz.esisba.a2cpi_project.interfaces.GetUserInterface;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.UserModel;

public class QuestionsFragment extends Fragment implements PostsOnItemClickListner {

    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<PostModel> QuestionsDataHolder;
    PostAdapter adapter;

    private UserModel userModel;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference likesRef;
    private CollectionReference postRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_questions, container, false);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        postRef = fstore.collection("Posts");

        recyclerView = parentHolder.findViewById(R.id.recviewQuestions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        QuestionsDataHolder = new ArrayList<>();


        //recyclerView.setAdapter(new PostAdapter(QuestionsDataHolder));//

        GetUserInterface id = (GetUserInterface) getActivity();
        userModel = id.getUserModel();
       // if (userModel!=null) Toast.makeText(getActivity(), userModel.getUid(), Toast.LENGTH_SHORT).show();

        FetchPosts();

        return parentHolder;
    }

    private void FetchPosts() {
        QuestionsDataHolder = new ArrayList<>();

        postRef.whereEqualTo("publisher", userModel.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);
                        QuestionsDataHolder.add(post);
                    }
                    buildRecyclerView();
                } else {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void buildRecyclerView() {
        recyclerView = parentHolder.findViewById(R.id.recviewQuestions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(QuestionsDataHolder,this);
        recyclerView.setAdapter(adapter);

    }

    public void StartQuestionBlocActivity(int position) {
        PostModel Post1 = new PostModel(QuestionsDataHolder.get(position).getAskedBy(), QuestionsDataHolder.get(position).getPublisher()
                , QuestionsDataHolder.get(position).getUsername(), QuestionsDataHolder.get(position).getQuestion(),
                QuestionsDataHolder.get(position).getBody(), QuestionsDataHolder.get(position).getPostid(),
                QuestionsDataHolder.get(position).getDate(), QuestionsDataHolder.get(position).getPublisherPic(),
                QuestionsDataHolder.get(position).getLikesCount(), QuestionsDataHolder.get(position).getAnswersCount(), QuestionsDataHolder.get(position).getTags());
        Intent intent = new Intent(getActivity(), QuestionBlocActivity.class);
        intent.putExtra("Tag", (Serializable) Post1);
        getActivity().startActivity(intent);

    }
    @Override
    public void onAnswerClick(int position) {
        StartQuestionBlocActivity(position);
    }

    @Override
    public void onPictureClick(int position) {

    }

    @Override
    public void onNameClick(int position) {

    }

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
    public void onLikeClick(int position, LottieAnimationView lottieAnimationView, TextView likesTxt, boolean isAnswer) {

    }

    @Override
    public void onItemClick(int position) {
        StartQuestionBlocActivity(position);
    }
}