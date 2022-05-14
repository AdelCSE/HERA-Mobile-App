package dz.esisba.a2cpi_project.navigation_fragments;

import android.app.Activity;
import android.content.Context;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.Token;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import NotificationTest.FcmNotificationsSender;
import dz.esisba.a2cpi_project.BottomNavigationActivity;
import dz.esisba.a2cpi_project.LoginActivity;
import dz.esisba.a2cpi_project.QuestionBlocActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.SearchActivity;
import dz.esisba.a2cpi_project.SettingsActivity;
import dz.esisba.a2cpi_project.UserProfileActivity;
import dz.esisba.a2cpi_project.adapter.PostAdapter;
import dz.esisba.a2cpi_project.interfaces.PostsOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.UserModel;


public class HomeFragment extends Fragment implements PostsOnItemClickListner {

    private View parentHolder;
    private RecyclerView recyclerView;
    private ArrayList<PostModel> PostsDataHolder;
    private PostAdapter adapter;
    private ImageButton settingsBtn , searchBtn;
    private SwipeRefreshLayout refresh;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference likesRef;
    private CollectionReference postRef;
    private String downloadUrl;
    ShimmerFrameLayout shimmer;
    Activity MainActivity;

    private boolean liked = false;
    private static  String date = DateFormat.getInstance().format(new Date());
    private ArrayList<String> likes;

    final static String TAG ="_____________________";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_home,container,false);

        refresh = parentHolder.findViewById(R.id.homeRefreshLayout);
        searchBtn = parentHolder.findViewById(R.id.search_btn);
        settingsBtn = parentHolder.findViewById(R.id.settingsBtn);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        postRef = fstore.collection("Posts");

        /*postRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("reportsCount", 0);

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().update(hm);
                    }
                } else {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
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

    public void StartUserProfileActivity(int position){
        if (PostsDataHolder.get(position).getPublisher().equals(user.getUid())){
            //switch to profile
        }else{
            DocumentReference DocRef = fstore.collection("Users").document(PostsDataHolder.get(position).getPublisher());
            DocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        UserModel User;
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            User = doc.toObject(UserModel.class);
                            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                            intent.putExtra("Tag", (Serializable) User);
                            getActivity().startActivity(intent);
                        }
                    }
                }
            });
        }
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
    public void onPictureClick(int position) {StartUserProfileActivity(position);}

    @Override
    public void onNameClick(int position) {StartUserProfileActivity(position);}

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
        PostModel post = new PostModel(PostsDataHolder.get(position).getAskedBy(), PostsDataHolder.get(position).getPublisher()
                , PostsDataHolder.get(position).getUsername() , PostsDataHolder.get(position).getQuestion() ,
                PostsDataHolder.get(position).getBody(),PostsDataHolder.get(position).getPostid(),
                PostsDataHolder.get(position).getDate(),PostsDataHolder.get(position).getPublisherPic(),
                PostsDataHolder.get(position).getLikesCount(),PostsDataHolder.get(position).getAnswersCount(), PostsDataHolder.get(position).getTags());
        Log.d("___________________", "onLikeClick: _________");



        if (lottieAnimationView.getTag().equals("Like"))
        {
            fstore.collection("Users").document(post.getPublisher()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        String s=task.getResult().getString("Token");
                        Log.d("=================", "============"+s);
                        Notify(task.getResult().getString("Token"),
                                user.getDisplayName()+" Like Your Post",
                                "Click Here To Open Your Post");
                    }

                }
            });

            lottieAnimationView.setSpeed(2);
            lottieAnimationView.playAnimation();//play like animation
            lottieAnimationView.setTag("Liked");

            int i = Integer.parseInt(likesTxt.getText().toString());
            i++;
            likesTxt.setText(Integer.toString(i));
            PostsDataHolder.get(position).setLikesCount(i);

            postRef.document(post.getPostid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists())
                        {
                            likes = (ArrayList<String>) doc.get("likes");
                            likes.add(user.getUid());
                            PostsDataHolder.get(position).setLikes(likes);
                            //likes are stored for user for faster query
                            likesRef = fstore.collection("Users").document(user.getUid()).
                                    collection("Likes").document(post.getPostid());
                                Map<String,Object> hashMap = new HashMap<>(); //represents key, value
                                hashMap.put("uid", user.getUid());
                                hashMap.put("postid", post.getPostid());
                                hashMap.put("tags", post.getTags()); //we might need tags later for recommendation system
                                likesRef.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Map<String,Object> hm = new HashMap<>();
                                        hm.put("likes", likes);
                                        hm.put("likesCount", likes.size());
                                        //update likes count for the post
                                        postRef.document(post.getPostid()).update(hm).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                LikeFailure(lottieAnimationView, likesTxt, position);
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        LikeFailure(lottieAnimationView, likesTxt, position);
                                    }
                                });
                        }
                    }
                    else LikeFailure(lottieAnimationView, likesTxt, position);
                }
            });
        } else {
            lottieAnimationView.setSpeed(-2);
            lottieAnimationView.playAnimation();//play like animation
            lottieAnimationView.setTag("Like");


            int i = Integer.parseInt(likesTxt.getText().toString());
            i--;
            likesTxt.setText(Integer.toString(i));
            PostsDataHolder.get(position).setLikesCount(i);

            postRef.document(post.getPostid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists())
                        {
                            likes = (ArrayList<String>) doc.get("likes");
                            likes.remove(user.getUid());
                            PostsDataHolder.get(position).setLikes(likes);
                            likesRef = fstore.collection("Users").document(user.getUid()).
                                    collection("Likes").document(post.getPostid());
                            likesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Map<String,Object> hm = new HashMap<>();
                                    hm.put("likes", likes);
                                    hm.put("likesCount", likes.size());
                                    //update likes count for the post
                                    postRef.document(post.getPostid()).update(hm).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            DislikeFailure(lottieAnimationView, likesTxt, position);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DislikeFailure(lottieAnimationView, likesTxt, position);
                                }
                            });
                        }
                    } else DislikeFailure(lottieAnimationView, likesTxt, position);
                }
            });
        }
        //updateToken();
    }

    private void DislikeFailure(LottieAnimationView lottieAnimationView, TextView likesTxt, int position)
    {
        lottieAnimationView.setSpeed(2);
        lottieAnimationView.playAnimation();//play like animation
        lottieAnimationView.setTag("Liked");
        int i = Integer.parseInt(likesTxt.getText().toString());
        i++;
        likesTxt.setText(Integer.toString(i));

    }

    private void LikeFailure(LottieAnimationView lottieAnimationView,TextView likesTxt, int position)
    {
        lottieAnimationView.setSpeed(-2);
        lottieAnimationView.playAnimation();//play like animation
        lottieAnimationView.setTag("Like");
        int i = Integer.parseInt(likesTxt.getText().toString());
        i--;
        likesTxt.setText(Integer.toString(i));

    }


    public void Notify(String publisherToken,String title,String message){
        FcmNotificationsSender send = new FcmNotificationsSender(
                publisherToken,
                title,
                message,
                getActivity(),
                HomeFragment.super.getActivity());

        send.SendNotifications();
        Log.d("----------------", "Notification function: ////////////");
    }
}

//TODO Add updateToken Function