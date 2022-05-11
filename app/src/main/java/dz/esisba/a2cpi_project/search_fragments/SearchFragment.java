package dz.esisba.a2cpi_project.search_fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jakewharton.rxbinding3.appcompat.RxSearchView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.SearchAdapter;
import dz.esisba.a2cpi_project.models.UserModel;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SearchFragment extends Fragment implements SearchAdapter.OnItemClickListener {

    RecyclerView recyclerView1;
    View parentHolder;
    ArrayList<UserModel> usersArrayList;
    SearchAdapter mAdapter;
    FirebaseFirestore db ;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_search, container, false);

        db=FirebaseFirestore.getInstance();

        recyclerView1 = parentHolder.findViewById(R.id.recycleview1);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));

        usersArrayList = new ArrayList<>();
        searchView = parentHolder.findViewById(R.id.searches);

        mAdapter = new SearchAdapter(getActivity(), usersArrayList, this);
        recyclerView1.setAdapter(mAdapter);

        RxSearchView.queryTextChanges(searchView)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    String s= charSequence.toString().trim().toLowerCase();
                    usersArrayList.clear();
                    if(s.length()<2){
                        mAdapter.notifyDataSetChanged();
                    }else {
                        EventChangeListener(s);
                    }
                });

        return parentHolder;
    }

    private void EventChangeListener(String s) {
        db
                .collection("Users")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot dc : queryDocumentSnapshots){
                String dataName = Objects.requireNonNull(dc.getString("Name")).toLowerCase().trim();
                String dataUserName = Objects.requireNonNull(dc.getString("Username")).toLowerCase().trim();
                if(dataName.contains(s) || dataUserName.contains(s)){
                    if(!usersArrayList.contains(dc.toObject(UserModel.class))) {
                        usersArrayList.add(dc.toObject(UserModel.class));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        usersArrayList.clear();
    }

    @Override
    public void onItemClick(int position) {

    }
}