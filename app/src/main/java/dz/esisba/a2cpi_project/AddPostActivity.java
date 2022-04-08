package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    private EditText questionEditTxt, bodyEditTxt;
    private ImageButton returnButton;
    private Button postButton;

    private String askedByName = "";
    private DocumentReference askedByRef;
    private ProgressDialog loader;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private String onlineUserId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        questionEditTxt = findViewById(R.id.questionEdittxt);
        bodyEditTxt = findViewById(R.id.bodyEditTxt);
        returnButton = findViewById(R.id.returnBtn);
        postButton = findViewById(R.id.postBtn);

        loader = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        onlineUserId = user.getUid();

        askedByRef = FirebaseFirestore.getInstance().collection("Users").document(onlineUserId);
        askedByRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(this.toString(), "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                  askedByName =  snapshot.get("Username").toString();
                } else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performValidation();
            }
        });
    }

    String getQuestionEditTxt() {
        return questionEditTxt.getText().toString().trim();
    }

    String date = DateFormat.getInstance().format(new Date());
    DocumentReference ref = FirebaseFirestore.getInstance().collection("Posts").document();

    String getBodyEditTxt() {
        return bodyEditTxt.getText().toString().trim();
    }

    private void performValidation()
    {
        if (getQuestionEditTxt().isEmpty()) questionEditTxt.setError("Enter your question");
        else if (getBodyEditTxt().isEmpty()) bodyEditTxt.setError("Please describe your question");
        else
        {
            startLoader();
            String postId = ref.getId();
            HashMap<String, Object> data = new HashMap<>();
            data.put("postid", postId);
            data.put("question", getQuestionEditTxt());
            data.put("body", getBodyEditTxt());
            data.put("publisher", onlineUserId);
            data.put("askedBy", askedByName);
            data.put("Date", date);

           DocumentReference df = fstore.collection("Posts").document(postId);
           df.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()) {
                       Toast.makeText(AddPostActivity.this, "Question posted successfully", Toast.LENGTH_SHORT).show();
                       loader.dismiss();
                       startActivity(new Intent(getApplicationContext(), MainActivity.class));
                       finish();
                   }
                   else
                   {
                       Toast.makeText(AddPostActivity.this, "Could not post question " + task.getException().toString(),
                               Toast.LENGTH_SHORT).show();
                       loader.dismiss();
                   }
               }
           });
        }

    }
    private void startLoader()
    {
        loader.setMessage("Publishing...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }
}