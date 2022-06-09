package dz.esisba.a2cpi_project.navigation_fragments.profile_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.RepliesAdapter;
import dz.esisba.a2cpi_project.models.ReplyModel;


public class RepliesFragment extends Fragment {

    private View parentHolder;
    private RecyclerView recyclerView;
    private ArrayList<ReplyModel> RepliesDataHolder;
    private RepliesAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout emptyReplies;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private CollectionReference replyRef;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_replies, container, false);
        progressBar = parentHolder.findViewById(R.id.repliesProgressBar);
        emptyReplies = parentHolder.findViewById(R.id.emptyReplies);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        replyRef = fstore.collection("Users").document(user.getUid()).collection("Replies");

        progressBar.setVisibility(View.VISIBLE);
        FetchReplies();

        return parentHolder;
    }

    private void FetchReplies(){
        RepliesDataHolder = new ArrayList<>();

        replyRef.orderBy("Date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        ReplyModel reply = document.toObject(ReplyModel.class);
                        RepliesDataHolder.add(reply);
                    }
                    ReplyModel lastItem = new ReplyModel(null,null,null,null,null,null,null,null);
                    RepliesDataHolder.add(lastItem);

                    if(RepliesDataHolder.size() > 1){
                        buildRecyclerView();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else{
                        progressBar.setVisibility(View.GONE);
                        emptyReplies.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void buildRecyclerView(){
        recyclerView = parentHolder.findViewById(R.id.recviewReplies);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RepliesAdapter(RepliesDataHolder);
        recyclerView.setAdapter(new RepliesAdapter(RepliesDataHolder));
    }
}