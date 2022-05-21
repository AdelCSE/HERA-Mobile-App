package dz.esisba.a2cpi_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class HelpActivity extends AppCompatActivity {

    ImageButton backBtn;
    EditText feedBackEditTxt , reportProblemEditTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        feedBackEditTxt = findViewById(R.id.sendFeedBackEditTxt);
        reportProblemEditTxt = findViewById(R.id.reportProblemEditTxt);
        backBtn = findViewById(R.id.helpBackBtn);

        String tag = (String) getIntent().getStringExtra("helpTag");

        if(tag.equals("problem")){
            reportProblemEditTxt.setVisibility(View.VISIBLE);
        }else{
            feedBackEditTxt.setVisibility(View.VISIBLE);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}