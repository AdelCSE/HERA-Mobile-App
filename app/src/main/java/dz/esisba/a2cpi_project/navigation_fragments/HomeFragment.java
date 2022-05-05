package dz.esisba.a2cpi_project.navigation_fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

import dz.esisba.a2cpi_project.AddPostActivity;
import dz.esisba.a2cpi_project.EditProfileActivity;
import dz.esisba.a2cpi_project.LoginActivity;
import dz.esisba.a2cpi_project.QuestionBlocActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.SearchActivity;
import dz.esisba.a2cpi_project.adapter.PostAdapter;
import dz.esisba.a2cpi_project.models.PostModel;

public class HomeFragment extends Fragment{

    private View parentHolder;
    private RecyclerView recyclerView;
    private ArrayList<PostModel> PostsDataHolder;
    private PostAdapter adapter;
    private ImageButton imageButton , searchBtn;


    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference askedByRef;
    private String downloadUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_home,container,false);

        searchBtn = parentHolder.findViewById(R.id.search_btn);
        imageButton = parentHolder.findViewById(R.id.logoutBtn);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        //Start Search Activity
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        FetchPosts();

        return parentHolder;
    }

    //Fetch Posts and display them in home
    private void FetchPosts() {
        PostsDataHolder = new ArrayList<>();

        fstore.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);
                        PostsDataHolder.add(post);
                        buildRecyclerView();
                    }
                } else {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Build the recyclerView
    public void buildRecyclerView() {
        recyclerView = parentHolder.findViewById(R.id.recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(PostsDataHolder);
        recyclerView.setAdapter(adapter);

        //Click on Post
        adapter.setOnItemClickListner(new PostAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                PostModel Post1 = new PostModel(PostsDataHolder.get(position).getAskedBy(), PostsDataHolder.get(position).getPublisher()
                        , PostsDataHolder.get(position).getUsername() , PostsDataHolder.get(position).getQuestion() ,
                        PostsDataHolder.get(position).getBody(),PostsDataHolder.get(position).getPostid(),
                        PostsDataHolder.get(position).getDate(),PostsDataHolder.get(position).getPublisherPic(),
                        PostsDataHolder.get(position).getLikesCount(),PostsDataHolder.get(position).getAnswersCount());
                Intent intent = new Intent(getActivity(), QuestionBlocActivity.class);
                intent.putExtra("Tag", (Serializable) Post1);
                getActivity().startActivity(intent);
            }

        });
    }

}
