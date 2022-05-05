package dz.esisba.a2cpi_project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import dz.esisba.a2cpi_project.adapter.SearchAdapter;
import dz.esisba.a2cpi_project.models.UserSearchModel;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListener {

    RecyclerView recyclerView1;
    ArrayList<UserSearchModel> userSearchModelArrayList;
    SearchAdapter mAdapter;


    //Query
    FirebaseFirestore db ;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView1 = findViewById(R.id.recycleview1);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        db=FirebaseFirestore.getInstance();
        //query = FirebaseFirestore.getInstance().collection("Users");

        userSearchModelArrayList = new ArrayList<>();
        mAdapter = new SearchAdapter(SearchActivity.this, userSearchModelArrayList, this);
        recyclerView1.setAdapter(mAdapter);

        editText=findViewById(R.id.autoCompleteTextView);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = editText.getText().toString().trim();
                if(s.isEmpty()){
                    userSearchModelArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editText.getText().toString().trim();
                if(s.isEmpty()){
                    userSearchModelArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                }else {
                    EventChangeListener(s);
                }
            }
        });
    }

    private void EventChangeListener(String s) {

        db
                .collection("Users")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot dc : queryDocumentSnapshots){
                String dataName = Objects.requireNonNull(dc.getString("Name")).toLowerCase().trim();
                String dataUserName = Objects.requireNonNull(dc.getString("Username")).toLowerCase().trim();
                if(dataName.contains(s) || dataUserName.contains(s)){
                    userSearchModelArrayList.add(dc.toObject(UserSearchModel.class));
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        userSearchModelArrayList.clear();
    }

    @Override
    public void onItemClick(int position) {
        //i made this so that the clicked items would be saved and displayed when nothing
        // is typed in the edit text (not necessary)+to open the user's profile
        //ArrayList<Users> searchedUsers=new ArrayList();
//        searchedUsers.add(usersArrayList.get(position));
//        usersArrayList=searchedUsers;

    }
}

// Toast.makeText(SearchActivity.this, "Search Failed, Try Again !",
//                            Toast.LENGTH_LONG).show();


/*
        ProgressDialog p;

        p = new ProgressDialog(this);
        p.setCancelable(false);
        p.setMessage("Searching...");
        p.show();

        if(p.isShowing()){
        p.dismiss();
        }
 */