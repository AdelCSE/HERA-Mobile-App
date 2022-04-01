package dz.esisba.a2cpi_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.ToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingtoolbarlayout = findViewById(R.id.CollapsingToolBarLayout);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.exemple);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                if (palette != null){
                    collapsingtoolbarlayout.setContentScrimColor(palette.getMutedColor(androidx.appcompat.R.attr.colorPrimary));
                }
            }
        });

    }
}