package dz.esisba.a2cpi_project.navigation_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.UserProfileActivity;
import dz.esisba.a2cpi_project.adapter.NotificationAdapter;
import dz.esisba.a2cpi_project.adapter.SearchAdapter;
import dz.esisba.a2cpi_project.models.NotificationModel;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.UserModel;

public class NotificationsFragment extends Fragment {

    private View parentHolder;
    private RecyclerView recyclerView;
    private ArrayList<NotificationModel> NotificationsDataHolder;
    private NotificationAdapter mAdapter;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private CollectionReference notifRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_notifications,container,false);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        FetchNotifications();


        return parentHolder;
    }

    public void FetchNotifications(){
        NotificationsDataHolder = new ArrayList<>();
        NotificationModel Notification1 = new NotificationModel(null, -1 , null , null , null ,null);
        NotificationsDataHolder.add(Notification1);
        notifRef = fstore.collection("Users").document(user.getUid()).collection("Notifications");
        notifRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NotificationModel notification = document.toObject(NotificationModel.class);
                        NotificationsDataHolder.add(notification);
                    }
                    buildRecyclerView();
                }else{
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void buildRecyclerView(){
        recyclerView = parentHolder.findViewById(R.id.notifrecview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new NotificationAdapter(NotificationsDataHolder);
        recyclerView.setAdapter(mAdapter);
    }

    // fel OnClick te3 Follow Notification
    private  void openProfile(View view , String id){
        Intent intent =  new Intent(view.getContext() , UserProfileActivity.class);
        UserModel userModel = new UserModel(id);
        intent.putExtra("Tag", (Serializable) userModel);
        view.getContext().startActivity(intent);
    }
}
