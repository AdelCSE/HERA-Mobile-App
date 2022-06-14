package dz.esisba.a2cpi_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import dz.esisba.a2cpi_project.models.UserModel;

public class HelpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;

    ImageButton backBtn;
    EditText feedBackEditTxt , reportProblemEditTxt;
    TextView submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        submit = findViewById(R.id.submit);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        GetUserClick();

    }

    private void GetUserClick(){
        feedBackEditTxt = findViewById(R.id.sendFeedBackEditTxt);
        reportProblemEditTxt = findViewById(R.id.reportProblemEditTxt);
        backBtn = findViewById(R.id.helpBackBtn);

        String tag = (String) getIntent().getStringExtra("helpTag");

        if(tag.equals("problem")){
            reportProblemEditTxt.setVisibility(View.VISIBLE);
        }else{
            feedBackEditTxt.setVisibility(View.VISIBLE);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tag.equals("problem")){
                    String report = reportProblemEditTxt.getText().toString();
                    DocumentReference doc = fstore.collection("Reports").document();
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("report", report);
                    data.put("reportID", doc.getId());
                    data.put("uid", user.getUid());
                    data.put("email", user.getEmail());

                    DocumentReference reportRef = fstore.collection("Reports").document(doc.getId());
                    reportRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(HelpActivity.this, "Your report has been sent successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(HelpActivity.this, SettingsActivity.class));
                            finish();
                        }
                    });
                }else{
                    String feedback = feedBackEditTxt.getText().toString();
                    DocumentReference doc = fstore.collection("Feedbacks").document();
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("feedback", feedback);
                    data.put("feedbackID", doc.getId());
                    data.put("uid", user.getUid());
                    data.put("email", user.getEmail());
                    DocumentReference feedbacksRef = fstore.collection("Feedbacks").document(doc.getId());
                    feedbacksRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(HelpActivity.this, "Your feedback is appreciated!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(HelpActivity.this, SettingsActivity.class));
                            finish();
                        }
                    });
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}