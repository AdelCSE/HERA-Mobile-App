package dz.esisba.a2cpi_project.navigation_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
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
import dz.esisba.a2cpi_project.models.UserModel;

public class NotificationsFragment extends Fragment {

    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<NotificationModel> NotificationsDataHolder;
    NotificationAdapter mAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_notifications,container,false);



        recyclerView = parentHolder.findViewById(R.id.notifrecview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        NotificationsDataHolder = new ArrayList<>();

        mAdapter = new NotificationAdapter(getActivity(), NotificationsDataHolder);
        recyclerView.setAdapter(mAdapter);



        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        Task<QuerySnapshot> s = FirebaseFirestore.getInstance()
//                .collection("Users")
//                .document(user)
//                .collection("Notifications")
//                .get();

        Query query = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user)
                .collection("Notifications")
                .orderBy("Time");



//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if(error!=null) {
//                            Log.d("______________", "Firestore error" + error.getMessage());
//                            return;
//                        }
//                            for(DocumentChange dc : value.getDocumentChanges()) {
//                                if (dc.getType() == DocumentChange.Type.ADDED) {
//                                    if(dc.getDocument().get("UserId")!=null){
//                                        QueryDocumentSnapshot d = dc.getDocument();
//                                        NotificationsDataHolder.add(new NotificationModel(d.getString("userName"),
//                                                "Liked your Post",
//                                                d.getString("Time"),
//                                                R.drawable.exemple));
//                                    }
//                                    //NotificationsDataHolder.add(dc.getDocument().toObject(NotificationModel.class));
////                                    else if()
//                                }
//                                mAdapter.notifyDataSetChanged();
//                            }
//                    }
//                });




//        NotificationModel Notification1 = new NotificationModel(null, null , null , -1);
//        NotificationsDataHolder.add(Notification1);
//        NotificationModel Notification2 = new NotificationModel("Yassine", "liked your question" , "24m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification2);
//        NotificationModel Notification3 = new NotificationModel("Ribel", "answered your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification3);
//        NotificationModel Notification4 = new NotificationModel("yacine", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification4);
//        NotificationModel Notification5 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification5);
//        NotificationModel Notification6 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification6);
//        NotificationModel Notification7 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification7);
//        NotificationModel Notification8 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification8);
//        NotificationModel Notification9 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification9);
//        NotificationModel Notification10 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification10);
//        NotificationModel Notification11 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification11);
//        NotificationModel Notification12 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification12);
//        NotificationModel Notification13 = new NotificationModel("Addy1001", "liked your question" , "23m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification13);
//        NotificationModel Notification14 = new NotificationModel("Addy1001", "liked your question" , "23m" , R.drawable.exemple);
//        NotificationsDataHolder.add(Notification14);




        return parentHolder;
    }

    // fel OnClick te3 Follow Notification
    private  void openProfile(View view , String id){
        Intent intent =  new Intent(view.getContext() , UserProfileActivity.class);
        UserModel userModel = new UserModel(id);
        intent.putExtra("Tag", (Serializable) userModel);
        view.getContext().startActivity(intent);
    }
}
