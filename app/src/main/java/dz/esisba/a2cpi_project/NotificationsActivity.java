package dz.esisba.a2cpi_project;

import static android.view.View.GONE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

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
    String notCount ="000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        progressBar = findViewById(R.id.notificationsProgressBar);
        recview = findViewById(R.id.notifrecview);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        progressBar.setVisibility(View.VISIBLE);
        FetchNotifications();

        Bundle bundle = new Bundle();
        bundle.putString("edttext", "From Activity");
        HomeFragment fragobj = new HomeFragment();
        fragobj.setArguments(bundle);

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
                    buildRecyclerView();
                    progressBar.setVisibility(GONE);
                    recview.setVisibility(View.VISIBLE);
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

    @Override
    public void onRemoveClick(View view ,int position) {
        //get the id of clicked notification
        String id = NotificationsDataHolder.get(position).getNotifId();
        //delete the notification from firebase and rebuild the recycler view
        fstore.collection("Users").document(auth.getUid()).collection("Notifications").document(id).delete();
        FetchNotifications();
        fstore.collection("Users").document(user.getUid()).update("unseenNotification", FieldValue.increment(-1));
        Toast.makeText(this,"Notification deleted Successfuly", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view , int position) {
        PostModel postModel = new PostModel(NotificationsDataHolder.get(position).getPostId());
        switch (NotificationsDataHolder.get(position).getType()){

            //follow = open profile
            case 0:
                Log.d("__________________", "follow note");

                Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
                intent.putExtra("Tag", (Serializable) NotificationsDataHolder.get(position).getUserId());
                view.getContext().startActivity(intent);

                break;
                //like post = open post
            case 1 :
                Log.d("__________________", "like post");
                Intent intent1 = new Intent(view.getContext(), QuestionBlocActivity.class);
                intent1.putExtra("Tag", (Serializable) postModel);
                intent1.putExtra("position", position);
                view.getContext().startActivity(intent1);

                break;
                //liked answer = open post (see answer)
            case 2 :
                Log.d("__________________", "liked answer");
                Intent intent2 = new Intent(view.getContext(), QuestionBlocActivity.class);
                intent2.putExtra("Tag", (Serializable) postModel);
                intent2.putExtra("position", position);
                view.getContext().startActivity(intent2);

                break;
                //answered your question = open post (see answer)
            case 3 :
                Log.d("__________________", "answered your question");
                Intent intent3 = new Intent(view.getContext(), QuestionBlocActivity.class);
                intent3.putExtra("Tag", (Serializable) postModel);
                intent3.putExtra("position", position);
                view.getContext().startActivity(intent3);
                break;
        }


    }
    public String getMyData() {
        return notCount;
    }

    // fel OnClick te3 Follow Notification
//    private  void openProfile(View view , String id){
//        Intent intent =  new Intent(view.getContext() , UserProfileActivity.class);
//        UserModel userModel = new UserModel(id);
//        intent.putExtra("Tag", (Serializable) userModel);
//        view.getContext().startActivity(intent);
//    }
}