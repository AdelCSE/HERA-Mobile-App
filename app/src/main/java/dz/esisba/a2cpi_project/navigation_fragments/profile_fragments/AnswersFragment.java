package dz.esisba.a2cpi_project.navigation_fragments.profile_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.AnswersAdapter;
import dz.esisba.a2cpi_project.interfaces.AnswersOnItemClickListner;
import dz.esisba.a2cpi_project.interfaces.GetUserInterface;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.UserModel;


public class AnswersFragment extends Fragment implements AnswersOnItemClickListner {

    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<PostModel> AnswersDataHolder;
    AnswersAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout myEmptyAnswers,emptyAnswers;
    static int i=0;
    Context context;

    private UserModel userModel;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private CollectionReference postRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_answers, container, false);
        progressBar = parentHolder.findViewById(R.id.answersProgressBar);
        myEmptyAnswers = parentHolder.findViewById(R.id.myEmptyAnswers);
        emptyAnswers = parentHolder.findViewById(R.id.emptyAnswers);


        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        postRef = fstore.collection("Posts");

        AnswersDataHolder = new ArrayList<>();
        context = parentHolder.getContext();


        GetUserInterface id = (GetUserInterface) getActivity();
        userModel = id.getUserModel();
 //       if (userModel!=null) Toast.makeText(context, userModel.getUid(), Toast.LENGTH_SHORT).show();

        emptyAnswers.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        FetchAnswers();

        return parentHolder;
    }

    public void buildRecyclerView() {
        recyclerView = parentHolder.findViewById(R.id.recviewAnswers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnswersAdapter(AnswersDataHolder);
        recyclerView.setAdapter(adapter);

    }

    public void FetchAnswers(){
        AnswersDataHolder = new ArrayList<>();
        ArrayList<String> answers = userModel.getAnswers();
        ArrayList<String> postIDs = new ArrayList<>();
        if (answers.size()!=0) {
            for (String s: answers) {
                String id = s.split("#")[0];
                if(!postIDs.contains(id)) postIDs.add(id);
            }
            for (int i=0;i<postIDs.size();i++) {
                String id = postIDs.get(i);

                int finalI = i;
                postRef.document(id).collection("Answers")
                        .whereEqualTo("publisher", userModel.getUid())
                        .orderBy("Date", Query.Direction.DESCENDING)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PostModel answer = document.toObject(PostModel.class);
                                answer.setAnswersCount(-1);
                                AnswersDataHolder.add(answer);
                            }
                            if(finalI ==postIDs.size()-1){
                                PostModel lastItem = new PostModel(null,null,null,null,null,null,null,null,-1,-1,null);
                                AnswersDataHolder.add(lastItem);
                            }
                            buildRecyclerView();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else{
            progressBar.setVisibility(View.GONE);
            if(userModel.getUid().equals(user.getUid())){
                myEmptyAnswers.setVisibility(View.VISIBLE);
            }else{
                emptyAnswers.setVisibility(View.VISIBLE);
            }
        }
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

    }
}