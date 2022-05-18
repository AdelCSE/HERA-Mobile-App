package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.io.Serializable;
import java.util.ArrayList;

import dz.esisba.a2cpi_project.adapter.NotificationAdapter;
import dz.esisba.a2cpi_project.models.NotificationModel;
import dz.esisba.a2cpi_project.models.UserModel;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<NotificationModel> NotificationsDataHolder;
    private NotificationAdapter mAdapter;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private CollectionReference notifRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        FetchNotifications();

    }

    public void FetchNotifications(){
        NotificationsDataHolder = new ArrayList<>();
        NotificationModel Notification1 = new NotificationModel(null, -1 , null , null , null ,null);
        NotificationsDataHolder.add(Notification1);
        notifRef = fstore.collection("Users").document(user.getUid()).collection("Notifications");
        notifRef.orderBy("Date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NotificationModel notification = document.toObject(NotificationModel.class);
                        NotificationsDataHolder.add(notification);
                    }
                    buildRecyclerView();
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
        mAdapter = new NotificationAdapter(NotificationsDataHolder);
        recyclerView.setAdapter(mAdapter);
    }

    // fel OnClick te3 Follow Notification
//    private  void openProfile(View view , String id){
//        Intent intent =  new Intent(view.getContext() , UserProfileActivity.class);
//        UserModel userModel = new UserModel(id);
//        intent.putExtra("Tag", (Serializable) userModel);
//        view.getContext().startActivity(intent);
//    }
}