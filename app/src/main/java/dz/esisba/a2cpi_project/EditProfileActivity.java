package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;

    private EditText name1, name2, bio;
    private ImageButton confirmButton, returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name1 = findViewById(R.id.nameEditText);
        name2 = findViewById(R.id.nameEditText2);
        bio = findViewById(R.id.bioEditText);
        confirmButton = findViewById(R.id.confirmButton);
        returnButton = findViewById(R.id.returnButton);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        DocumentReference df = fstore.collection("Users").document(user.getUid());
        
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists())
                            {
                                Map<String,Object> userInfor = new HashMap<>(); //represents key, value
                                userInfor.put("Name", name1.getText().toString()+" "+name2.getText().toString());
                                userInfor.put("Bio", bio.getText().toString());
                                df.update(userInfor);
                                Toast.makeText(EditProfileActivity.this, "Information updated!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("Name", name1.getText().toString()+" "+name2.getText().toString());
                i.putExtra("Bio", bio.getText().toString());
                startActivity(i);
                finish();
            }
        });
        
    }
}