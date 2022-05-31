package dz.esisba.a2cpi_project;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dz.esisba.a2cpi_project.adapter.NotificationAdapter;
import dz.esisba.a2cpi_project.interfaces.NotificationOnItemListener;
import dz.esisba.a2cpi_project.models.NotificationModel;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.UserModel;
import dz.esisba.a2cpi_project.navigation_fragments.HomeFragment;

public class NotificationsActivity extends AppCompatActivity implements NotificationOnItemListener {

    private RecyclerView recyclerView;
    private ArrayList<NotificationModel> NotificationsDataHolder;
    private NotificationAdapter mAdapter;
    private ProgressBar progressBar;
    private RecyclerView recview;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private CollectionReference notifRef;
    private Button clearAll;
    private ImageButton returnBtn;
    private TextView noNotifications;
//    CardView notification_icon;

    private NotificationBadge notificationBadge;
    public static HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

//        notification_icon = (CardView) findViewById(R.id.notification_icon);

        notificationBadge = homeFragment.getActivity().findViewById(R.id.badge);
        progressBar = findViewById(R.id.notificationsProgressBar);
        recview = findViewById(R.id.notifrecview);
        clearAll = findViewById(R.id.clearAll);
        returnBtn = findViewById(R.id.btnReturn);
        noNotifications= findViewById(R.id.noNotificarions);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        progressBar.setVisibility(View.VISIBLE);
//        noNotifications.setVisibility(View.VISIBLE);
        FetchNotifications();


        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NotificationsDataHolder.size()==1) {
                    Toast.makeText(NotificationsActivity.this, "Notifications Already Cleared", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(NotificationsActivity.this)
                            .setMessage("Are you sure you want to Delete all Notifications ?")
                            .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //clear recView from items
                                    NotificationsDataHolder.clear();
                                    mAdapter.notifyDataSetChanged();

                                    noNotifications.setVisibility(View.VISIBLE);
                                    //delete Notifications (documents) from firestore
                                    fstore.collection("Users").document(user.getUid()).collection("Notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                fstore.collection("Users").document(user.getUid()).collection("Notifications").document(snapshot.getId()).delete();
                                            }
                                        }
                                    });
                                    //set the notification badge to 0
                                    fstore.collection("Users").document(user.getUid()).update("unseenNotifications", 0);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    public void FetchNotifications(){
        NotificationsDataHolder = new ArrayList<>();
        NotificationModel Notification1 = new NotificationModel(null, -1 , null , null , null ,null,null);
        NotificationsDataHolder.add(Notification1);
        notifRef = fstore.collection("Users").document(user.getUid()).collection("Notifications");
        notifRef.orderBy("Date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NotificationModel notification = document.toObject(NotificationModel.class);
                        notification.setNotifId(document.getId());
                        NotificationsDataHolder.add(notification);
                    }
                    if(NotificationsDataHolder.size() <= 1){
                        noNotifications.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(GONE);
                        recview.setVisibility(GONE);
                    }else {
                        buildRecyclerView();
                        noNotifications.setVisibility(GONE);
                        progressBar.setVisibility(GONE);
                        recview.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(NotificationsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.notifrecview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NotificationAdapter(NotificationsDataHolder,this);
        recyclerView.setAdapter(mAdapter);
    }

    private void updateBadge(){
        fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getLong("unseenNotifications") != null) {
                        if(task.getResult().getLong("unseenNotifications").intValue() >99) {
                            notificationBadge.setText("99+");
                        }
                        else{
                            notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                        }
                    } else {
                        Toast.makeText(HomeFragment.homeFragment.getContext() , "You Don't Have Notifications ! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onRemoveClick(View view ,int position) {

        //get the id of clicked notification
        String id = NotificationsDataHolder.get(position).getNotifId();
        //remove the item from recView
            NotificationsDataHolder.remove(position);
            mAdapter.notifyItemRemoved(position);
        //delete the notification from firebase
        fstore.collection("Users").document(auth.getUid()).collection("Notifications").document(id).delete();
        fstore.collection("Users").document(user.getUid()).update("unseenNotifications", FieldValue.increment(-1));
        Toast.makeText(this,"Notification Deleted Successfully", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(View view , int position) {

//        PostModel postModel = new PostModel(NotificationsDataHolder.get(position).getPostId());
        String id = NotificationsDataHolder.get(position).getNotifId();

        fstore.collection("Users").document(user.getUid()).update("unseenNotifications", FieldValue.increment(-1));
        fstore.collection("Users").document(user.getUid()).collection("Notifications").document(id).update("seen",true);
//        FetchNotifications(); // we need to update only the selected item ,Because there's no need to Fetch and update the whole recyclerView...
//        Optimization purposes.



        switch (NotificationsDataHolder.get(position).getType()){

            //follow = open profile
            case 0:
                Log.d("__________________", "follow note");

//                Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
//                intent.putExtra("Tag", (Serializable) NotificationsDataHolder.get(position).getUserId());
//                view.getContext().startActivity(intent);

                break;
                //like post = open post
            case 1 :
                Log.d("__________________", "like post");
//                Intent intent1 = new Intent(view.getContext(), QuestionBlocActivity.class);
//                intent1.putExtra("Tag", (Serializable) postModel);
//                intent1.putExtra("position", position);
//                view.getContext().startActivity(intent1);

                break;
                //liked answer = open post (see answer)
            case 2 :
                Log.d("__________________", "liked answer");
//                Intent intent2 = new Intent(view.getContext(), QuestionBlocActivity.class);
//                intent2.putExtra("Tag", (Serializable) postModel);
//                intent2.putExtra("position", position);
//                view.getContext().startActivity(intent2);

                break;
                //answered your question = open post (see answer)
            case 3 :
                Log.d("__________________", "answered your question");
//                Intent intent3 = new Intent(view.getContext(), QuestionBlocActivity.class);
//                intent3.putExtra("Tag", (Serializable) postModel);
//                intent3.putExtra("position", position);
//                view.getContext().startActivity(intent3);
                break;
        }


    }

    @Override
    public void onBackPressed() {
        updateBadge();
        super.onBackPressed();
    }
}

//TODO OnRemoveClick crashing when pressing twice too fast [not very important]