package dz.esisba.a2cpi_project.navigation_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.NotificationAdapter;
import dz.esisba.a2cpi_project.models.NotificationModel;

public class NotificationsFragment extends Fragment {

    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<NotificationModel> NotificationsDataHolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_notifications,container,false);

        recyclerView = parentHolder.findViewById(R.id.notifrecview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        NotificationsDataHolder = new ArrayList<>();

        NotificationModel Notification1 = new NotificationModel(null, null , null , -1);
        NotificationsDataHolder.add(Notification1);
        NotificationModel Notification2 = new NotificationModel("Yassine", "liked your question" , "24m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification2);
        NotificationModel Notification3 = new NotificationModel("Ribel", "answered your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification3);
        NotificationModel Notification4 = new NotificationModel("yacine", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification4);
        NotificationModel Notification5 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification5);
        NotificationModel Notification6 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification6);
        NotificationModel Notification7 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification7);
        NotificationModel Notification8 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification8);
        NotificationModel Notification9 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification9);
        NotificationModel Notification10 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification10);
        NotificationModel Notification11 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification11);
        NotificationModel Notification12 = new NotificationModel("Rachid", "liked your question" , "30m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification12);
        NotificationModel Notification13 = new NotificationModel("Addy1001", "liked your question" , "23m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification13);
        NotificationModel Notification14 = new NotificationModel("Addy1001", "liked your question" , "23m" , R.drawable.exemple);
        NotificationsDataHolder.add(Notification14);

        recyclerView.setAdapter(new NotificationAdapter(NotificationsDataHolder));


        return parentHolder;
    }
}
