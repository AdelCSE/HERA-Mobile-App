package dz.esisba.a2cpi_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import dz.esisba.a2cpi_project.search_fragments.SearchFragment;
import dz.esisba.a2cpi_project.search_fragments.TagsFragment;

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