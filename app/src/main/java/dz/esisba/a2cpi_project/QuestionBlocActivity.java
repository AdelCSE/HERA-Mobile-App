package dz.esisba.a2cpi_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import NotificationTest.FcmNotificationsSender;
import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.adapter.QuestionBlocAdapter;
import dz.esisba.a2cpi_project.interfaces.QuestionsOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.UserModel;
import dz.esisba.a2cpi_project.navigation_fragments.HomeFragment;

public class QuestionBlocActivity extends AppCompatActivity implements QuestionsOnItemClickListner {

    private RecyclerView recyclerView;
    private ArrayList<PostModel> postsDataHolder;
    private ImageButton AnswerBtn;
    private QuestionBlocAdapter adapter;
    private BottomSheetDialog dialog;
    private ProgressBar progressBar;
    private ImageButton backBtn;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private String askedByName,askedByUsername = "";
    private DocumentReference userRef;
    private DocumentReference likesRef;
    private String downloadUrl;
    private PostModel post;
    private int count;
    private ArrayList<String> likes;
    private DocumentReference postRef;
    private int absolutePosition;

    NotificationBadge notificationBadge;
    public static HomeFragment homeFragment;
    private HashMap<String, Long> tagsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_bloc);

        backBtn = findViewById(R.id.btnReturnQuestionBloc);
        progressBar = findViewById(R.id.questionBlocProgressBar);
        recyclerView = findViewById(R.id.recviewa);
        setAnswerBtn(findViewById(R.id.answerBtn));
        setDialog(new BottomSheetDialog(this));


        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        userRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());

        notificationBadge = homeFragment.getActivity().findViewById(R.id.badge);

        //get username of poster
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(this.toString(), "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    askedByUsername = snapshot.get("Username").toString();
                    if (snapshot.get("profilePictureUrl") != null) {
                        downloadUrl = snapshot.get("profilePictureUrl").toString();
                    }
                    if (snapshot.get("Name")!= null)
                    {
                        askedByName = snapshot.get("Name").toString();
                    }
                }
                else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });

        absolutePosition = getIntent().getIntExtra("position", -1);
        post = (PostModel) getIntent().getSerializableExtra("Tag");
        //ArrayList<String> likes = (ArrayList<String>) data.getExtras().getSerializable("likes");
       tagsMap = (HashMap<String, Long>) getIntent().getExtras().getSerializable("tagsMap");
        postRef = fstore.collection("Posts").document(post.getPostid());
        count = post.getAnswersCount();
        postsDataHolder = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        FetchAnswers();

        fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    //Display answers
    private void FetchAnswers() {
        postsDataHolder = new ArrayList<>();
        postsDataHolder.add(post);

        PostModel text = new PostModel(null,null,null,null,null,null,null,null,-1,post.getAnswersCount(),null);
        postsDataHolder.add(text);

        postRef.collection("Answers").orderBy("likesCount", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i=0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel answer = document.toObject(PostModel.class);
                        answer.setAnswersCount(-1);
                        postsDataHolder.add(answer);
                    }

                    buildRecyclerView();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(QuestionBlocActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void StartUserProfileActivity(int position){
        if (postsDataHolder.get(position).getPublisher().equals(user.getUid())){
            //switch to profile
        }else{
            DocumentReference DocRef = fstore.collection("Users").document(postsDataHolder.get(position).getPublisher());
            DocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        UserModel User;
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            User = doc.toObject(UserModel.class);
                            Intent intent = new Intent(QuestionBlocActivity.this, UserProfileActivity.class);
                            intent.putExtra("Tag", (Serializable) User);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    //Add an Answer
    @Override
    public void onAnswerClick(int position) {
        showAnswerDialog();
        getDialog().show();
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onPictureClick(int position) {StartUserProfileActivity(position);}

    @Override
    public void onNameClick(int position) {StartUserProfileActivity(position);}

    //Share APP
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

        PostModel post = postsDataHolder.get(position);
        if (!isAnswer) {
            PerformLikeAction(lottieAnimationView, likesTxt,  post, position);
        }
        else {
            PerformLikeActionForAnswer(lottieAnimationView, likesTxt, post, position);
        }
    }

    private void PerformLikeActionForAnswer(LottieAnimationView lottieAnimationView, TextView likesTxt, PostModel p, int position) {
        postRef = fstore.collection("Posts").document(post.getPostid()).collection("Answers")
        .document(p.getPostid());
        if (lottieAnimationView.getTag().equals("Like"))
        {
            //sending like answer notification to the publisher ********************************************************************************************************************
            Task<DocumentSnapshot> s = fstore.collection("Users").document(user.getUid()).get(); //current user data
            fstore.collection("Users").document(p.getPublisher()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()&& s.isSuccessful()){
                        NotifylikedAnswer(task.getResult().getString("Token"),
                                s.getResult().getString("Username"), //current user name
                                QuestionBlocActivity.this,
                                position);
                    }
                    else{
                        //Toast.maketext
                        Log.d("Calling notify", "onComplete: Failure");
                    }
                }
            });

            lottieAnimationView.setSpeed(2);
            lottieAnimationView.playAnimation();//play like animation
            lottieAnimationView.setTag("Liked");

            int i = Integer.parseInt(likesTxt.getText().toString());
            i++;
            likesTxt.setText(Integer.toString(i));

            postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists())
                        {
                            likes = (ArrayList<String>) doc.get("likes");
                            if(!likes.contains(user.getUid())) likes.add(user.getUid());
                            postsDataHolder.get(position).setLikes(likes);
                            postsDataHolder.get(position).setLikesCount(likes.size());
                            Map<String,Object> hm = new HashMap<>();
                            hm.put("likes", likes);
                            hm.put("likesCount", likes.size());
                            //update likes count for the post
                            postRef.update(hm).addOnFailureListener(new OnFailureListener() {
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


            postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists())
                        {
                            likes = (ArrayList<String>) doc.get("likes");
                            likes.remove(user.getUid());
                            postsDataHolder.get(position).setLikes(likes);
                            postsDataHolder.get(position).setLikesCount(likes.size());
                            Map<String,Object> hm = new HashMap<>();
                            hm.put("likes", likes);
                            hm.put("likesCount", likes.size());
                            postRef.update(hm).addOnFailureListener(new OnFailureListener() {
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
    }

    private void PerformLikeAction(LottieAnimationView lottieAnimationView, TextView likesTxt, PostModel p, int position) {
        postRef = fstore.collection("Posts").document(p.getPostid());
        if (lottieAnimationView.getTag().equals("Like"))
        {
            //sending like notification to the publisher ********************************************************************************************************************
            Task<DocumentSnapshot> s = fstore.collection("Users").document(user.getUid()).get();
            fstore.collection("Users").document(p.getPublisher()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()&& s.isSuccessful()){
                        NotifyLikedPost(task.getResult().getString("Token"),
                                s.getResult().getString("Username"),
                                QuestionBlocActivity.this,
                                0);
                    }
                    else{
                        //Toast.maketext
                        Log.d("Calling notify", "onComplete: Failure");
                    }
                }
            });

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            lottieAnimationView.setSpeed(2);
            lottieAnimationView.playAnimation();//play like animation
            lottieAnimationView.setTag("Liked");

            int i = Integer.parseInt(likesTxt.getText().toString());
            i++;
            likesTxt.setText(Integer.toString(i));

            postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists())
                        {
                            likes = (ArrayList<String>) doc.get("likes");
                            if(!likes.contains(user.getUid()))likes.add(user.getUid());
                            postsDataHolder.get(0).setLikes(likes);
                            postsDataHolder.get(0).setLikesCount(likes.size());
                            Map<String,Object> hm = new HashMap<>();
                            hm.put("likes", likes);
                            hm.put("likesCount", likes.size());
                            //update likes count for the post
                            postRef.update(hm).addOnFailureListener(new OnFailureListener() {
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

            postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists())
                        {
                            likes = (ArrayList<String>) doc.get("likes");
                            likes.remove(user.getUid());
                            postsDataHolder.get(0).setLikes(likes);
                            post.setLikes(likes);
                            postsDataHolder.get(0).setLikesCount(likes.size());
                            post.setLikesCount(likes.size());
                            Map<String,Object> hm = new HashMap<>();
                            hm.put("likes", likes);
                            hm.put("likesCount", likes.size());
                            //update likes count for the post
                            postRef.update(hm).addOnFailureListener(new OnFailureListener() {
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

    private void buildRecyclerView(){
        setRecyclerView(findViewById(R.id.recviewa));
        getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionBlocAdapter(postsDataHolder,this);
        recyclerView.setAdapter(adapter);
    }

    Date date = new Date();

    private void showAnswerDialog(){
        View view = getLayoutInflater().inflate(R.layout.activty_add_answer,null,false);

        ImageButton closeAnswerBtn = view.findViewById(R.id.closeAnswerBtn);
        EditText addAnswer =  view.findViewById(R.id.answerEditTxt);
        TextView postAnswerBtn =  view.findViewById(R.id.postAnswerBtn);
        CircleImageView profileimg = view.findViewById(R.id.bottomsheetimg);

        Glide.with(QuestionBlocActivity.this).load(downloadUrl).into(profileimg);

        closeAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        postAnswerBtn.setEnabled(false);
        addAnswer.addTextChangedListener(new TextWatcher() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().trim().length()==0){
                    postAnswerBtn.setEnabled(false);
                } else {
                    postAnswerBtn.setEnabled(true);
                    postAnswerBtn.setTextColor(R.color.button);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        postAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable())
                {
                    Toast.makeText(QuestionBlocActivity.this, "Network error please try again later", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    if(!addAnswer.getText().toString().isEmpty())
                    {
                        Task<DocumentSnapshot> s = fstore.collection("Users").document(user.getUid()).get();
                        fstore.collection("Users").document(post.getPublisher()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&& s.isSuccessful()){
                                    NotifyAnswer(task.getResult().getString("Token"),
                                            s.getResult().getString("Username"),
                                            QuestionBlocActivity.this);
                                }
                                else{
                                    //Toast.maketext
                                    Log.d("Calling notify", "onComplete: Failure");
                                }
                            }
                        });
                        PerformValidation(addAnswer.getText().toString());
                    }else {
                        addAnswer.setError("You have to type your answer");
                    }
                }
            }
        });
        getDialog().setContentView(view);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
        lottieAnimationView.playAnimation();
        lottieAnimationView.setTag("Like");
        int i = Integer.parseInt(likesTxt.getText().toString());
        i--;
        likesTxt.setText(Integer.toString(i));

    }

    private void PerformValidation(String answer) {
        DocumentReference id = fstore.collection("Posts").document();
        String answerId = post.getPostid() +"#"+id.getId();
        Toast.makeText(QuestionBlocActivity.this, "Posting...", Toast.LENGTH_SHORT).show();
        HashMap<String, Object> data = new HashMap<>();
        data.put("postid", answerId);
        data.put("question", post.getQuestion());
        data.put("publisherPic", downloadUrl);
        data.put("body", answer);
        data.put("publisher", user.getUid().toString());
        data.put("answerBy", askedByName);
        data.put("Username", askedByUsername);
        data.put("Date", date);
        data.put("likesCount", 0);
        data.put("reportsCount", 0);
        ArrayList<String> likes = new ArrayList();
        data.put("likes", likes);

        DocumentReference df = postRef.collection("Answers").document(answerId);
        df.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DocumentReference dr = fstore.collection("Posts").document(post.getPostid());
                    dr.update("answersCount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            userRef.update("answers", FieldValue.arrayUnion(answerId));
                            post.setAnswersCount(post.getAnswersCount()+1);
                            Toast.makeText(QuestionBlocActivity.this, "Answer posted successfully", Toast.LENGTH_SHORT).show();
                            getDialog().dismiss();
                            startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
                            finish();
                        }
                    });
                }
                else
                {
                    Toast.makeText(QuestionBlocActivity.this, "Could not post answer " + task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public ArrayList<PostModel> getPostsDataHolder() {
        return postsDataHolder;
    }

    public void setPostsDataHolder(ArrayList<PostModel> postsDataHolder) {
        this.postsDataHolder = postsDataHolder;
    }

    public ImageButton getAnswerBtn() {
        return AnswerBtn;
    }

    public void setAnswerBtn(ImageButton answerBtn) {
        AnswerBtn = answerBtn;
    }

    public QuestionBlocAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(QuestionBlocAdapter adapter) {
        this.adapter = adapter;
    }

     public BottomSheetDialog getDialog() {
        return dialog;
    }

    public void setDialog(BottomSheetDialog dialog) {
        this.dialog = dialog;
    }

    //Notifications
    public void NotifyLikedPost(String publisherToken, String title, Activity activity, int position){

        fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().getString("Token").equals(publisherToken) || true) {           //****************** check  this condition ***true
                        FcmNotificationsSender send = new FcmNotificationsSender(
                                publisherToken,
                                title+" Liked your Post",
                                "Click To See All Notifications",
                                activity);
                        send.SendNotifications();
                    }
                }
            }
        });
        //add notifier data to notified user (name )
        PostModel postModel = new PostModel(postsDataHolder.get(position).getPostid());
        CollectionReference DocRef = fstore.collection("Users").document(postsDataHolder.get(position).getPublisher()).collection("Notifications");
        //add the notification data to the notification collection of the notified user
        Map<String, Object> notif = new HashMap<>();
        notif.put("Type", 1);
        notif.put("PostId", postModel.getPostid());
        notif.put("Username", title);
        notif.put("Date", Timestamp.now());
        notif.put("Image",downloadUrl);
        notif.put("UserId",user.getUid() );
        notif.put("Seen", false);
        //add the document to the notification collection
        DocRef.add(notif);

        fstore.collection("Users").document(postsDataHolder.get(position).getPublisher()).update("unseenNotifications", FieldValue.increment(1));

    }

    public void NotifylikedAnswer(String publisherToken, String title,  Activity activity, int position){

        fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().getString("Token").equals(publisherToken) || true) {           //****************** check  this condition ***true
                        FcmNotificationsSender send = new FcmNotificationsSender(
                                publisherToken,
                                title+" Liked your Answer !",
                                "Click To See All Notifications",
                                activity);
                        send.SendNotifications();
                    }
                }
            }
        });
        //add notifier data to notified user (name )  ******* this is for the recyclerView **********
        PostModel postModel = new PostModel(postsDataHolder.get(0).getPostid());
        CollectionReference DocRef = fstore.collection("Users").document(postsDataHolder.get(0).getPublisher()).collection("Notifications");
        //add the notification data to the notification collection of the notified user
        Map<String, Object> notif = new HashMap<>();
        notif.put("Type", 2);
        notif.put("PostId", postModel.getPostid());
        notif.put("Username", title);
        notif.put("Date", Timestamp.now());
        notif.put("Image",downloadUrl);
        notif.put("UserId",user.getUid() );
        notif.put("Seen", false);
        //add the document to the notification collection
        DocRef.add(notif);

        fstore.collection("Users").document(postsDataHolder.get(position).getPublisher()).update("unseenNotifications", FieldValue.increment(1));

    }

    public void NotifyAnswer(String publisherToken, String title,  Activity activity){

        fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().getString("Token").equals(publisherToken) || true) {           //****************** check  this condition ***true
                        FcmNotificationsSender send = new FcmNotificationsSender(
                                publisherToken,
                                title+" Answered Your Question !",
                                "Click To See All Notifications",
                                activity);
                        send.SendNotifications();
                    }
                }
            }
        });
        //add notifier data to notified user (name )  ******* this is for the recyclerView **********
        PostModel postModel = new PostModel(postsDataHolder.get(0).getPostid());
        CollectionReference DocRef = fstore.collection("Users").document(postsDataHolder.get(0).getPublisher()).collection("Notifications");
        //add the notification data to the notification collection of the notified user
        Map<String, Object> notif = new HashMap<>();
        notif.put("Type", 3);
        notif.put("PostId", postModel.getPostid());
        notif.put("Username", title);
        notif.put("Date", Timestamp.now());
        notif.put("Image",downloadUrl);
        notif.put("UserId",user.getUid() );
        notif.put("id",DocRef.getId());
        notif.put("Seen", false);
        //add the document to the notification collection
        DocRef.add(notif);

        fstore.collection("Users").document(postsDataHolder.get(0).getPublisher()).update("unseenNotifications", FieldValue.increment(1));

    }
    //end Notifications

    @Override
    public void onBackPressed() {

        fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getLong("unseenNotifications") != null) {
                        if(task.getResult().getLong("unseenNotifications").intValue() >99) {
                            notificationBadge.setText("99+");
                        }else{
                            notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                    }
//                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                    } else {
                        Log.d("____________", "onComplete: null value");
                    }
                }
            }
        });

        ArrayList<String> likes = postsDataHolder.get(0).getLikes();
        Intent intent = new Intent();
        intent.putExtra("likes", likes);
        intent.putExtra("position", absolutePosition);
        setResult(RESULT_OK, intent);
        finish();
    }

}