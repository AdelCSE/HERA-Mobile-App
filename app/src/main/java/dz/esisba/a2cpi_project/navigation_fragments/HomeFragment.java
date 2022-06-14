package dz.esisba.a2cpi_project.navigation_fragments;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
    private ArrayList<String> following = new ArrayList<>();
    private PostAdapter adapter;
    private ImageButton settingsBtn, searchBtn, notificationsBtn;
    private SwipeRefreshLayout refresh;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference  userInfos, likesRef;
    private CollectionReference postRef;
    private String downloadUrl;

    NotificationBadge notificationBadge;
    public static HomeFragment homeFragment;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling;
    private boolean isLastItemPaged;
    private LinearLayoutManager linearLayoutManager;
    private HashMap<String, Long> tagsMap = new HashMap<String, Long>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_home, container, false);

        refresh = parentHolder.findViewById(R.id.homeRefreshLayout);
        searchBtn = parentHolder.findViewById(R.id.search_btn);
        settingsBtn = parentHolder.findViewById(R.id.settingsBtn);
        notificationsBtn = parentHolder.findViewById(R.id.notification_btn);
        progressBar = parentHolder.findViewById(R.id.homeProgressBar);
        recyclerView = parentHolder.findViewById(R.id.recview);
        notificationBadge = parentHolder.findViewById(R.id.badge);

        linearLayoutManager = new LinearLayoutManager(getContext());

        NotificationsActivity.homeFragment = this;
        QuestionBlocActivity.homeFragment = this;


        Main();


        //DO NOT REMOVE THIS WE NEED IT LATER!!!!!!!
        //SETTING FEED FOR ALL USERS **IMPORTANT**

        /*Query user = fstore.collection("Users");
        user.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                {
                    fstore.collection("Posts").orderBy("likesCount", Query.Direction.DESCENDING).limit(50).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                PostModel post = document.toObject(PostModel.class);
                                doc.getReference().collection("Feed").document(post.getPostid()).set(post);
                                doc.getReference().collection("Feed").document(post.getPostid()).update("priority", 2);
                            }
                        }
                    });
                    doc.getReference().update("reputation", 0);
                }
            }
        });*/

        return parentHolder;

    }


    private void Main() {
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        postRef = fstore.collection("Posts");
        userInfos = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());


        //adding most liked posts
        fstore.collection("Posts").orderBy("likesCount", Query.Direction.DESCENDING).limit(10)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    PostModel post = document.toObject(PostModel.class);
                    userInfos.collection("Feed").document(post.getPostid()).set(post);
                    userInfos.collection("Feed").document(post.getPostid()).update("priority", 2);
                }
            }
        });

        userInfos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().get("following") != null) {
                        following = (ArrayList<String>) task.getResult().get("following");
                        if (task.getResult().get("LikedTags") != null) {
                            tagsMap = (HashMap<String, Long>) task.getResult().get("LikedTags");
                        }
                        SetFeed();
                    }



                    if (task.getResult().getLong("unseenNotifications") != null) {
                        if (task.getResult().getLong("unseenNotifications").intValue() > 99) {
                            notificationBadge.setText("99+");
                        } else {
                            notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                        }
                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                    } else {
//                        Toast.makeText(getContext(), "You Don't Have Notifications ! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        FetchPosts();

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
                } else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
                SetFeed();
                FetchPosts();
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
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SetFeed() {

        //adding recommended
        List<Map.Entry<String, Long>> list = new LinkedList<>(tagsMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> false ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        //sorting the map
        tagsMap = list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
        ArrayList<String> tags = new ArrayList<>(tagsMap.keySet());

        for (int i = 0; i < 2; i++) {
            try {
                fstore.collection("Posts").whereArrayContains("tags", tags.get(i)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            PostModel post = document.toObject(PostModel.class);
                            DocumentReference feedRef = userInfos.collection("Feed").document(post.getPostid());
                            feedRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (!documentSnapshot.exists()) {
                                        feedRef.set(post);
                                        feedRef.update("priority", 1);
                                    }
                                    else if(documentSnapshot.getLong("priority").intValue()>1)
                                    {
                                        feedRef.update("priority", 1);
                                    }
                                }
                            });
                        }
                    }
                });
            }catch (IndexOutOfBoundsException e){
                Log.e("Some error has occured:", e.getMessage());
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -30); //current day -30 days


        //combining queries
        ArrayList<Task> tasks = new ArrayList<>();
        for (String user : following) {
            Task t = postRef.whereEqualTo("publisher", user)
                    .whereGreaterThanOrEqualTo("Date", c.getTime())
                    .get();
            tasks.add(t);
        }
        //getting the result from combined queries - works fine
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for (QuerySnapshot nextQueryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot document : nextQueryDocumentSnapshots) {
                        PostModel post = document.toObject(PostModel.class);
                        DocumentReference feedRef = userInfos.collection("Feed").document(post.getPostid());
                        feedRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (!documentSnapshot.exists()) {
                                    feedRef.set(post);
                                    feedRef.update("priority", 0);
                                }
                                else if(documentSnapshot.getLong("priority").intValue()>0)
                                {
                                    feedRef.update("priority", 0);
                                }
                            }
                        });
                    }
                }
            }
        });


        //delete extras
        Query query = userInfos.collection("Feed").whereLessThan("Date", c.getTime());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    doc.getReference().delete();
                }
            }
        });
        /*ArrayList<PostModel> safePosts = new ArrayList<>();
        for (String id: following) {
            Query delete = fstore.collection("Users").document(user.getUid()).collection("Feed")
                    .whereEqualTo("publisher", id);
            delete.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        PostModel post = doc.toObject(PostModel.class);
                        safePosts.add(post);
                    }
                }
            });
        }*/
    }

    //Fetch Posts and display them in home
    private void FetchPosts() {
        PostsDataHolder = new ArrayList<>();
        isLastItemPaged = false;


        Query query = fstore.collection("Users").document(user.getUid()).collection("Feed")
                .orderBy("priority", Query.Direction.ASCENDING)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(25);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);
                        PostsDataHolder.add(post);

                    }
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(VISIBLE);
                    if (task.getResult().size() > 0)
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    BuildRecyclerView();

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            int firstVisibileItem = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibileItem + visibleItemCount == totalItemCount) && !isLastItemPaged) {
                                isScrolling = false;

                                Query nextQuery = userInfos.collection("Feed").orderBy("date", Query.Direction.DESCENDING)
                                        .startAfter(lastVisible)
                                        .limit(25);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            PostModel post = document.toObject(PostModel.class);
                                            PostsDataHolder.add(post);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (task.getResult().size() < 1) isLastItemPaged = true;
                                        if (task.getResult().size() > 0)
                                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                    }
                                });

                            }
                        }
                    };
                    recyclerView.addOnScrollListener(onScrollListener);
                }
            }
        });

    }


    //Build the recyclerView
    private void BuildRecyclerView() {
        recyclerView = parentHolder.findViewById(R.id.recview);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PostAdapter(PostsDataHolder, this);
        recyclerView.setAdapter(adapter);

    }

    //Start Question Bloc Activity
    private void StartQuestionBlocActivity(int position) {
        PostModel Post1 = new PostModel(PostsDataHolder.get(position).getAskedBy(), PostsDataHolder.get(position).getPublisher()
                , PostsDataHolder.get(position).getUsername(), PostsDataHolder.get(position).getQuestion(),
                PostsDataHolder.get(position).getBody(), PostsDataHolder.get(position).getPostid(),
                PostsDataHolder.get(position).getDate(), PostsDataHolder.get(position).getPublisherPic(),
                PostsDataHolder.get(position).getLikesCount(), PostsDataHolder.get(position).getAnswersCount(), PostsDataHolder.get(position).getTags());
        Intent intent = new Intent(getActivity(), QuestionBlocActivity.class);
        intent.putExtra("Tag", (Serializable) Post1);
        intent.putExtra("position", position);
        intent.putExtra("tagsMap",tagsMap);
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
                        ArrayList<String> likes = (ArrayList<String>) data.getExtras().getSerializable("likes");
                        int position = data.getIntExtra("position", 0);
                        Refresh(likes, position);
                    }
                }
            });

    private void Refresh(ArrayList<String> likes, int position) {
        if (likes != null && position >= 0) {
            PostsDataHolder.get(position).setLikes(likes);
            PostsDataHolder.get(position).setLikesCount(likes.size());
            adapter.notifyItemChanged(position);
            adapter.notifyDataSetChanged();
            BuildRecyclerView();
        }
    }

    //Start User Profile Activity
    private void StartUserProfileActivity(int position) {
        if (PostsDataHolder.get(position).getPublisher().equals(user.getUid())) {
            //switch to profile
        } else {
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
    public void onPictureClick(int position) {
        StartUserProfileActivity(position);
    }

    @Override
    public void onNameClick(int position) {
        StartUserProfileActivity(position);
    }

    @Override
    public void onShareClick(int position) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String Body = "Download this app";
        intent.putExtra(Intent.EXTRA_TEXT, Body);
        intent.putExtra(Intent.EXTRA_TEXT, "URL");
        startActivity(Intent.createChooser(intent, "Share using"));
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
                    if (task.isSuccessful() && s.isSuccessful()) {
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
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            likes = (ArrayList<String>) doc.get("likes");
                            if (!likes.contains(user.getUid())) likes.add(user.getUid());
                            PostsDataHolder.get(position).setLikes(likes);
                            PostsDataHolder.get(position).setLikesCount(likes.size());
                            //likes are stored for user for faster query
                            Map<String, Object> hm = new HashMap<>();
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
                    } else LikeFailure(lottieAnimationView, likesTxt, position);
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
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            likes = (ArrayList<String>) doc.get("likes");
                            likes.remove(user.getUid());
                            PostsDataHolder.get(position).setLikes(likes);
                            PostsDataHolder.get(position).setLikesCount(likes.size());
                            likesRef = fstore.collection("Users").document(user.getUid()).
                                    collection("Likes").document(post.getPostid());
                            Map<String, Object> hm = new HashMap<>();
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
                    } else DislikeFailure(lottieAnimationView, likesTxt, position);
                }
            });
        }
        //updateToken();
    }

    private void SetLikesForUser(ArrayList<String> tags, int i) {
        DocumentReference userRef = fstore.collection("Users").document(user.getUid());
        if (tagsMap == null) tagsMap = new HashMap<String, Long>();
        HashMap<String, Long> updatedTags = tagsMap;

        for (String tag : tags) {
            long occ = 1;
            if (tagsMap.containsKey(tag)) occ = tagsMap.get(tag) + 1;
            updatedTags.put(tag, occ);
        }
        userRef.update("LikedTags", updatedTags);
    }

    private void DislikeFailure(LottieAnimationView lottieAnimationView, TextView likesTxt, int position) {
        lottieAnimationView.setSpeed(2);
        lottieAnimationView.playAnimation();//play like animation
        lottieAnimationView.setTag("Liked");
        int i = Integer.parseInt(likesTxt.getText().toString());
        i++;
        likesTxt.setText(Integer.toString(i));
    }

    private void LikeFailure(LottieAnimationView lottieAnimationView, TextView likesTxt, int position) {
        lottieAnimationView.setSpeed(-2);
        lottieAnimationView.playAnimation();//play like animation
        lottieAnimationView.setTag("Like");
        int i = Integer.parseInt(likesTxt.getText().toString());
        i--;
        likesTxt.setText(Integer.toString(i));
    }


    public void Notify(String publisherToken, String title, Activity activity, int position) {

        fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().getString("Token").equals(publisherToken) || true) {
                        FcmNotificationsSender send = new FcmNotificationsSender(
                                publisherToken,
                                title + " Liked your Question !",
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
        notif.put("Image", downloadUrl);
        notif.put("UserId", auth.getCurrentUser().getUid());
        notif.put("Seen", false);
        //add the document to the notification collection
        DocRef.add(notif);

        fstore.collection("Users").document(PostsDataHolder.get(position).getPublisher()).update("unseenNotifications", FieldValue.increment(1));

        userInfos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getLong("unseenNotifications") != null) {
//                    if(task.getResult().getLong("unseenNotifications").intValue() >99) {
//                        notificationBadge.setText("99+");
//                    }
//                    else{
//                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
//                    }
                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                    } else {
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
 *    4 => Arduino Temperature
 *    5 => Arduino Movement
 *    6 => Arduino Sound
 * */
