package dz.esisba.a2cpi_project.search_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchUserFragment extends Fragment implements SearchAdapter.OnItemClickListener {

    RecyclerView recyclerView1;
    View parentHolder;
    ArrayList<UserModel> usersArrayList;
    SearchAdapter mAdapter;
    FirebaseFirestore db ;
    SearchView searchView;
    private ImageButton backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_search, container, false);

        db=FirebaseFirestore.getInstance();

        backBtn = parentHolder.findViewById(R.id.backBtn);
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new TagsFilterFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.searchContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return parentHolder;
    }

    private void EventChangeListener(String s) {
        db
                .collection("Users")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot dc : queryDocumentSnapshots){
                String dataUserName = Objects.requireNonNull(dc.getString("Username")).toLowerCase().trim();
                if(dataUserName.contains(s)){
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