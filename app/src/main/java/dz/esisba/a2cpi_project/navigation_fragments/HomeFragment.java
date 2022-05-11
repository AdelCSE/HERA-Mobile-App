package dz.esisba.a2cpi_project.navigation_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dz.esisba.a2cpi_project.LoginActivity;
import dz.esisba.a2cpi_project.QuestionBlocActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.SearchActivity;
import dz.esisba.a2cpi_project.adapter.PostAdapter;
import dz.esisba.a2cpi_project.interfaces.PostsOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;
import likeNotification.APIService;
import likeNotification.Data;
import likeNotification.MyResponse;
import likeNotification.NotificationSender;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements PostsOnItemClickListner {

    private View parentHolder;
    private RecyclerView recyclerView;
    private ArrayList<PostModel> PostsDataHolder;
    private PostAdapter adapter;
    private ImageButton imageButton , searchBtn;


    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference likesRef;
    private CollectionReference postRef;
    private String downloadUrl;

    private boolean liked = false;
    private static  String date = DateFormat.getInstance().format(new Date());
    private int likes;
    APIService apiService;
    final static String TAG ="_____________________";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_home,container,false);

        searchBtn = parentHolder.findViewById(R.id.search_btn);
        imageButton = parentHolder.findViewById(R.id.logoutBtn);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        postRef = fstore.collection("Posts");


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearToken(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

        postRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);
                        PostsDataHolder.add(post);
                    }
                    buildRecyclerView();
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
        adapter = new PostAdapter(PostsDataHolder,this);
        recyclerView.setAdapter(adapter);

    }

    public void StartQuestionBlocActivity(int position){
        PostModel Post1 = new PostModel(PostsDataHolder.get(position).getAskedBy(), PostsDataHolder.get(position).getPublisher()
                , PostsDataHolder.get(position).getUsername() , PostsDataHolder.get(position).getQuestion() ,
                PostsDataHolder.get(position).getBody(),PostsDataHolder.get(position).getPostid(),
                PostsDataHolder.get(position).getDate(),PostsDataHolder.get(position).getPublisherPic(),
                PostsDataHolder.get(position).getLikesCount(),PostsDataHolder.get(position).getAnswersCount(), PostsDataHolder.get(position).getTags());
        Intent intent = new Intent(getActivity(), QuestionBlocActivity.class);
        intent.putExtra("Tag", (Serializable) Post1);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        StartQuestionBlocActivity(position);
    }

    @Override
    public void onAnswerClick(int position) {
        StartQuestionBlocActivity(position);
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
        lottieAnimationView.setEnabled(false);
        PostModel post = new PostModel(PostsDataHolder.get(position).getAskedBy(), PostsDataHolder.get(position).getPublisher()
                , PostsDataHolder.get(position).getUsername() , PostsDataHolder.get(position).getQuestion() ,
                PostsDataHolder.get(position).getBody(),PostsDataHolder.get(position).getPostid(),
                PostsDataHolder.get(position).getDate(),PostsDataHolder.get(position).getPublisherPic(),
                PostsDataHolder.get(position).getLikesCount(),PostsDataHolder.get(position).getAnswersCount(), PostsDataHolder.get(position).getTags());

        postRef.document(post.getPostid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        //i need the token of the publisher of the post in the recyclerView
                        //sendNotification(FirebaseMessaging.getInstance().getToken().getResult(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"Liked your post");
                        likes = doc.getLong("likesCount").intValue();
                        Log.d(String.valueOf(getActivity()), "initial value" +Integer.toString(likes));
                        //likes are stored for user for faster query
                        likesRef = fstore.collection("Users").document(user.getUid()).
                                collection("Likes").document(post.getPostid());


                        Map<String, Object> hm = new HashMap<>();
                        if (lottieAnimationView.getTag().equals("Like"))
                        {
                            lottieAnimationView.setSpeed(1);
                            lottieAnimationView.playAnimation(); //play like animation
                            Map<String,Object> userInfor = new HashMap<>(); //represents key, value
                            userInfor.put("likedDate", date);
                            userInfor.put("uid", user.getUid());
                            userInfor.put("postid", post.getPostid());
                            userInfor.put("tags", post.getTags()); //we might need tags later for recommendation system
                            likesRef.set(userInfor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    lottieAnimationView.setTag("Liked");
                                    Log.d("Like", "before incrementing" +Integer.toString(likes));
                                    likes++;
                                    Log.d("Like", "after incrementing" +Integer.toString(likes));
                                    hm.put("likesCount", likes);
                                    //update likes count for the post
                                    postRef.document(post.getPostid()).update(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            post.setLikesCount(likes);
                                            likesTxt.setText(Integer.toString(likes)); //update likes count for post
                                            // likes = post.getLikesCount();
                                            lottieAnimationView.setEnabled(true);
                                        }
                                    });
                                }
                            });
                        }
                        else if (lottieAnimationView.getTag().equals("Liked")) {
                            lottieAnimationView.setSpeed(-1f);
                            lottieAnimationView.playAnimation(); //play animation in reverse
                            likesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() { //delete like from user
                                @Override
                                public void onSuccess(Void aVoid) {
                                    lottieAnimationView.setTag("Like");
                                    likes--;
                                    hm.put("likesCount", likes);
                                    postRef.document(post.getPostid()).update(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            post.setLikesCount(likes);
                                            likesTxt.setText(Integer.toString(likes)); //update likes count for post
                                            // likes = post.getLikesCount();//get current likes on post
                                            lottieAnimationView.setEnabled(true);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    //function that delete the token of the user Because when the user is signed-out he doesn't receive Notifications
    private void clearToken(String uid){
        final Map<String,Object> emptyToken = new HashMap<>();
        emptyToken.put("Token", FieldValue.delete());
        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(uid)
                .update(emptyToken);
    }

    //Send Like Notification to the publisher
    public void sendNotification(String publisherToken,String title , String message){
        Data data = new Data(title,message);
        NotificationSender sender = new NotificationSender(data, publisherToken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if(response.code() == 200){
                    if(response.body().success != 1){
                        //Toast.makeText(HomeFragment.this, "Failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: _____");
                    }
                }
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
            }
        });
    }

}
