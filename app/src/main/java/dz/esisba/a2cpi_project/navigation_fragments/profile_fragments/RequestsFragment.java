package dz.esisba.a2cpi_project.navigation_fragments.profile_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.NotificationAdapter;
import dz.esisba.a2cpi_project.adapter.RequestAdapter;
import dz.esisba.a2cpi_project.models.RequestModel;

public class RequestsFragment extends Fragment {

    View parentHolder;
    RecyclerView recyclerView;
    ArrayList<RequestModel> RequestsDataHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_requests, container, false);

        recyclerView = parentHolder.findViewById(R.id.recviewR);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RequestsDataHolder = new ArrayList<>();

        RequestModel Request1 = new RequestModel("@yassine","What's your Questions1?","•15m•",R.drawable.exemple);
        RequestsDataHolder.add(Request1);
        RequestModel Request2 = new RequestModel("@ribel","What's your Questions2?","•1h•",R.drawable.exemple);
        RequestsDataHolder.add(Request2);
        RequestModel Request3 = new RequestModel("@yac_ine","What's your Questions3?","•5d•",R.drawable.exemple);
        RequestsDataHolder.add(Request3);
        RequestModel Request4 = new RequestModel("@rachid","What's your Questions4?","•7d•",R.drawable.exemple);
        RequestsDataHolder.add(Request4);
        RequestModel Request5 = new RequestModel("@rachid","What's your Questions4?","•7d•",R.drawable.exemple);
        RequestsDataHolder.add(Request5);
        RequestModel Request6 = new RequestModel("@rachid","What's your Questions4?","•7d•",R.drawable.exemple);
        RequestsDataHolder.add(Request6);

        recyclerView.setAdapter(new RequestAdapter(RequestsDataHolder));


        return parentHolder;
    }
}