package dz.esisba.a2cpi_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jakewharton.rxbinding3.appcompat.RxSearchView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dz.esisba.a2cpi_project.adapter.SearchAdapter;
import dz.esisba.a2cpi_project.models.UserSearchModel;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListener {


    RecyclerView recyclerView1;
    ArrayList<UserSearchModel> usersArrayList;
    SearchAdapter mAdapter;

    FirebaseFirestore db ;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        recyclerView1 = findViewById(R.id.recycleview1);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        db=FirebaseFirestore.getInstance();

        searchView = findViewById(R.id.searches);

        usersArrayList = new ArrayList<>();
        mAdapter = new SearchAdapter(SearchActivity.this, usersArrayList, this);
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

    }

    @Override
    public void onItemClick(int position) {

    }


    private void EventChangeListener(String s) {

        db
                .collection("Users")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot dc : queryDocumentSnapshots){
                String dataName = Objects.requireNonNull(dc.getString("Name")).toLowerCase().trim();
                String dataUserName = Objects.requireNonNull(dc.getString("Username")).toLowerCase().trim();
                if(dataName.contains(s) || dataUserName.contains(s)){
                    if(!usersArrayList.contains(dc.toObject(UserSearchModel.class))) {
                        usersArrayList.add(dc.toObject(UserSearchModel.class));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        usersArrayList.clear();

    }
}