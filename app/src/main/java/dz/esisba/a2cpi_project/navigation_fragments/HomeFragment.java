package dz.esisba.a2cpi_project.navigation_fragments;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import NotificationTest.FcmNotificationsSender;
import dz.esisba.a2cpi_project.NotificationsActivity;
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
    private ArrayList<String> likes;
    private PostAdapter adapter;
    private ImageButton settingsBtn , searchBtn , notificationsBtn;
    private SwipeRefreshLayout refresh;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference likesRef,userInfos;
    private CollectionReference postRef;
    private String downloadUrl;

    NotificationBadge notificationBadge;
    public static HomeFragment homeFragment;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_home,container,false);

        refresh = parentHolder.findViewById(R.id.homeRefreshLayout);
        searchBtn = parentHolder.findViewById(R.id.search_btn);
        settingsBtn = parentHolder.findViewById(R.id.settingsBtn);
        notificationsBtn = parentHolder.findViewById(R.id.notification_btn);
        progressBar = parentHolder.findViewById(R.id.homeProgressBar);
        recyclerView = parentHolder.findViewById(R.id.recview);
        notificationBadge = parentHolder.findViewById(R.id.badge);

        NotificationsActivity.homeFragment=this;
        QuestionBlocActivity.homeFragment=this;



        Main();



        return parentHolder;

    }



    private void Main()
    {
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        postRef = fstore.collection("Posts");
        userInfos = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());



        userInfos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().getLong("unseenNotifications")!= null) {
                    if(task.getResult().getLong("unseenNotifications").intValue() >99) {
                        notificationBadge.setText("99+");
                    }
                    else{
                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                    }
                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                    }else{
                       Toast.makeText(getContext(), "You Don't Have Notifications ! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

/////////////////////////////////////////////////////////////////////////


        userInfos.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                }
                else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchPosts();
                refresh.setRefreshing(false);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        //Start Notifications Activity
        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });

        //Start Search Activity
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        FetchPosts();
    }

    //Fetch Posts and display them in home
    private void FetchPosts() {
        PostsDataHolder = new ArrayList<>();

        postRef.orderBy("Date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);
                        PostsDataHolder.add(post);
                    }
                    buildRecyclerView();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(VISIBLE);
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

    //Start Question Bloc Activity
    public void StartQuestionBlocActivity(int position){
        PostModel Post1 = new PostModel(PostsDataHolder.get(position).getAskedBy(), PostsDataHolder.get(position).getPublisher()
                , PostsDataHolder.get(position).getUsername() , PostsDataHolder.get(position).getQuestion() ,
                PostsDataHolder.get(position).getBody(),PostsDataHolder.get(position).getPostid(),
                PostsDataHolder.get(position).getDate(),PostsDataHolder.get(position).getPublisherPic(),
                PostsDataHolder.get(position).getLikesCount(),PostsDataHolder.get(position).getAnswersCount(), PostsDataHolder.get(position).getTags());
        Intent intent = new Intent(getActivity(), QuestionBlocActivity.class);
        intent.putExtra("Tag", (Serializable) Post1);
        intent.putExtra("position", position);
        questionBlocActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> questionBlocActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        ArrayList<String> likes = (ArrayList<String>)data.getExtras().getSerializable("likes");
                        int position = data.getIntExtra("position", 0);
                        Refresh(likes,position);
                    }
                }
            });

    private void Refresh(ArrayList<String> likes, int position)
    {
         if (likes != null && position>=0)
         {
             PostsDataHolder.get(position).setLikes(likes);
             PostsDataHolder.get(position).setLikesCount(likes.size());
             adapter.notifyItemChanged(position);
             adapter.notifyDataSetChanged();
             buildRecyclerView();
         }
    }

    //Start User Profile Activity
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
        PostModel post = PostsDataHolder.get(position);

        if (lottieAnimationView.getTag().equals("Like")) {

            //sending like notification to the publisher
            Task<DocumentSnapshot> s = fstore.collection("Users").document(user.getUid()).get();
            fstore.collection("Users").document(post.getPublisher()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()&& s.isSuccessful()){
                        Notify(task.getResult().getString("Token"),
                                s.getResult().getString("Username"),
                                getActivity(),
                                position);
                    }
                }
            });

            lottieAnimationView.setSpeed(3);
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
                            PostsDataHolder.get(position).setLikesCount(likes.size());
                            //likes are stored for user for faster query
                            Map<String,Object> hm = new HashMap<>();
                            hm.put("likes", likes);
                            hm.put("likesCount", likes.size());
                            //update likes count for the post
                            postRef.document(post.getPostid()).update(hm).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    LikeFailure(lottieAnimationView, likesTxt, position);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    SetLikesForUser(post.getTags(), 1);
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
                            PostsDataHolder.get(position).setLikesCount(likes.size());
                            likesRef = fstore.collection("Users").document(user.getUid()).
                                    collection("Likes").document(post.getPostid());
                            Map<String,Object> hm = new HashMap<>();
                            hm.put("likes", likes);
                            hm.put("likesCount", likes.size());
                            //update likes count for the post
                            postRef.document(post.getPostid()).update(hm).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DislikeFailure(lottieAnimationView, likesTxt, position);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    SetLikesForUser(post.getTags(), -1);
                                }
                            });
                        }
                    } else DislikeFailure(lottieAnimationView, likesTxt, position);
                }
            });
        }
        //updateToken();
    }

    private void SetLikesForUser(ArrayList<String> tags, int i)
    {
        CollectionReference userRef = fstore.collection("Users").document(user.getUid())
                .collection("LikedTags");
        for (String tag: tags) {
            DocumentReference tagRef = userRef.document(tag);
            tagRef.update("occurrence", FieldValue.increment(i));
        }
    }

    private void DislikeFailure(LottieAnimationView lottieAnimationView, TextView likesTxt, int position) {
        lottieAnimationView.setSpeed(2);
        lottieAnimationView.playAnimation();//play like animation
        lottieAnimationView.setTag("Liked");
        int i = Integer.parseInt(likesTxt.getText().toString());
        i++;
        likesTxt.setText(Integer.toString(i));
    }

    private void LikeFailure(LottieAnimationView lottieAnimationView,TextView likesTxt, int position) {
        lottieAnimationView.setSpeed(-2);
        lottieAnimationView.playAnimation();//play like animation
        lottieAnimationView.setTag("Like");
        int i = Integer.parseInt(likesTxt.getText().toString());
        i--;
        likesTxt.setText(Integer.toString(i));
    }


    public void Notify(String publisherToken,String title,Activity activity,int position) {

        fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().getString("Token").equals(publisherToken) || true) {
                        FcmNotificationsSender send = new FcmNotificationsSender(
                                publisherToken,
                                title+" Liked your Question !",
                                "Click To See All Notifications",
                                activity);
                        send.SendNotifications();
                    }
                }
            }
        });

        //add notifier data to notified user (name )  ******* this is for the recyclerView **********
        PostModel postModel = new PostModel(PostsDataHolder.get(position).getPostid());

        CollectionReference DocRef = fstore.collection("Users").document(PostsDataHolder.get(position).getPublisher()).collection("Notifications");
        //add the notification data to the notification collection of the notified user
        Map<String, Object> notif = new HashMap<>();
        notif.put("Type", 1);
        notif.put("PostId", postModel.getPostid());
        notif.put("Username", title);
        notif.put("Date", Timestamp.now());
        notif.put("Image",downloadUrl);
        notif.put("UserId",auth.getCurrentUser().getUid() );
        //add the document to the notification collection
        DocRef.add(notif);

        fstore.collection("Users").document(PostsDataHolder.get(position).getPublisher()).update("unseenNotifications",FieldValue.increment(1));

        userInfos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().getLong("unseenNotifications")!= null) {
//                    if(task.getResult().getLong("unseenNotifications").intValue() >99) {
//                        notificationBadge.setText("99+");
//                    }
//                    else{
//                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
//                    }
                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                    }else{
                        Log.d("____________", "onComplete: null value");
                    }
                }
            }
        });



    }
}

//TODO Add updateToken Function [Optional]
//*************Important*******firebase notification*********
//Notification Types :
/*    0 => Follow
 *    1 => Like post
 *    2 => Like answer
 *    3 => answered question
 * */
