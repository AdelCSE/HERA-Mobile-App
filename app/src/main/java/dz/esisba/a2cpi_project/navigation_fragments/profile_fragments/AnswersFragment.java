package dz.esisba.a2cpi_project.navigation_fragments.profile_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.AnswersAdapter;
import dz.esisba.a2cpi_project.interfaces.AnswersOnItemClickListner;
import dz.esisba.a2cpi_project.interfaces.SetUserModelInterface;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.UserModel;


public class AnswersFragment extends Fragment implements AnswersOnItemClickListner {

    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<PostModel> AnswersDataHolder;
    AnswersAdapter adapter;

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


        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        postRef = fstore.collection("Posts");

        AnswersDataHolder = new ArrayList<>();
        context = parentHolder.getContext();

        /*PostModel Post1 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question1" , "details here","1000",null,"11:11 AM • 29 APR 22");
        AnswersDataHolder.add(Post1);
        PostModel Post2 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question2" , "details here","1000",null,"11:08 AM • 29 APR 22");
        AnswersDataHolder.add(Post2);
        PostModel Post3 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question3" , "details here","1000",null,"10:45 AM • 29 APR 22");
        AnswersDataHolder.add(Post3);
        PostModel Post4 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question4" , "details here","1000",null,"10:30 AM • 29 APR 22");
        AnswersDataHolder.add(Post4);
        PostModel Post5 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question5" , "details here","1000",null,"08:25 AM • 29 APR 22");
        AnswersDataHolder.add(Post5);*/

        SetUserModelInterface id = (SetUserModelInterface) getActivity();
        userModel = id.setUserModel();
        if (userModel==null) Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
       // FetchAnswers();

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
        for (String s: answers) {
            String id = s.split("#")[0];
            if(!postIDs.contains(id)) postIDs.add(id);
        }
        for (String id: postIDs) {
            postRef.document(id).collection("Answers").whereEqualTo("publisher", userModel.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PostModel answer = document.toObject(PostModel.class);
                            answer.setAnswersCount(-1);
                            AnswersDataHolder.add(answer);
                        }
                        buildRecyclerView();
                    } else {
                        Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

    }


    @Override
    public void onLikeClick(int position, LottieAnimationView lottieAnimationView, TextView likesTxt, boolean isAnswer) {

    }

    @Override
    public void onItemClick(int position) {

    }
}