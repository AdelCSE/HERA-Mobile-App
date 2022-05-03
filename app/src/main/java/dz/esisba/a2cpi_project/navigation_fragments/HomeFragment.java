package dz.esisba.a2cpi_project.navigation_fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

import dz.esisba.a2cpi_project.AddPostActivity;
import dz.esisba.a2cpi_project.LoginActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.SearchActivity;
import dz.esisba.a2cpi_project.adapter.PostAdapter;
import dz.esisba.a2cpi_project.models.PostModel;

public class HomeFragment extends Fragment{
    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<PostModel> PostsDataHolder;

    private ImageButton imageButton;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference askedByRef;
    private String downloadUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_home,container,false);

        recyclerView = parentHolder.findViewById(R.id.recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imageButton = parentHolder.findViewById(R.id.logoutBtn);


        /*PostModel Post1 = new PostModel(R.drawable.exemple, "Adel Mokadem" , "@addy1001" , "What's your Question1" , "details here","1000","500","11:11 AM • 29 APR 22");
        PostsDataHolder.add(Post1);
        PostModel Post2 = new PostModel(R.drawable.exemple, "Yassine Benyamina" , "@yassine" , "What's your Question2" , "details here","1000","500","11:08 AM • 29 APR 22");
        PostsDataHolder.add(Post2);
        PostModel Post3 = new PostModel(R.drawable.exemple, "Chifa Ribel Belarbi" , "@ribel" , "What's your Question3" , "details here","1000","500","10:45 AM • 29 APR 22");
        PostsDataHolder.add(Post3);
        PostModel Post4 = new PostModel(R.drawable.exemple, "Yacine Dait Dehane" , "@yac_ine" , "What's your Question4" , "details here","1000","500","10:30 AM • 29 APR 22");
        PostsDataHolder.add(Post4);
        PostModel Post5 = new PostModel(R.drawable.exemple, "Rachid Benayad" , "@rachide" , "What's your Question5" , "details here","1000","500","08:25 AM • 29 APR 22");
        PostsDataHolder.add(Post5);*/



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

        askedByRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        //get username of poster
        askedByRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(this.toString(), "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if (snapshot.get("profilePictureUrl") != null) {
                        downloadUrl = snapshot.get("profilePictureUrl").toString();
                    }
                    FetchPosts();
                }
                else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });





        return parentHolder;
    }

    private void FetchPosts() {

        PostsDataHolder = new ArrayList<>();

        fstore.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);
                        post.setPublisherPic(downloadUrl);
                        PostsDataHolder.add(post);
                        recyclerView.setAdapter(new PostAdapter(PostsDataHolder));
                    }
                } else {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
