package dz.esisba.a2cpi_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
import dz.esisba.a2cpi_project.navigation_fragments.HomeFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SearchActivity extends AppCompatActivity{

    ImageButton sbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sbtn = findViewById(R.id.container_searchBtn);

        getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer,
                new TagsFragment()).commit();

        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer,
                        new SearchFragment()).commit();
            }
        });
    }
}