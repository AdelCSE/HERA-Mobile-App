package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import dz.esisba.a2cpi_project.navigation_fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private Button postBtn,user1,user2;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fstore;
    private static String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postBtn = findViewById(R.id.postQs);
        user1 = findViewById(R.id.user1);
        user2 = findViewById(R.id.user2);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();



        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPostActivity.class));
                finish();
            }
        });


        //this is not very efficient
        //we need to already provide an id instead of looking for it
        //each post must come with its user id

        user1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fstore.collection("Users")
                        .whereEqualTo("Username", user1.getText().toString())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                if (error != null) {
                                    uid = "";
                                    return;
                                }


                                for (QueryDocumentSnapshot document : value) {
                                    if (value.size()>0)
                                    {
                                        uid = document.get("uid").toString();
                                        if (auth.getCurrentUser().getUid().equals(uid)) {
                                            startActivity(new Intent(getApplicationContext(), ProfileFragment.class));
                                            finish();
                                        }
                                        else if (!auth.getCurrentUser().getUid().equals(uid) && !uid.isEmpty())
                                        {
                                            Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
                                            i.putExtra("Username", user1.getText().toString());
                                            i.putExtra("uid", uid);
                                            startActivity(i);
                                            finish();
                                        }
                                        break;
                                    }
                                    else uid = "";
                                }
                            }
                        });
            }
        });

        user2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fstore.collection("Users")
                        .whereEqualTo("Username", user2.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (task.getResult().size()>0)
                                        {
                                            uid = document.get("uid").toString();
                                            if (auth.getCurrentUser().getUid().equals(uid)) {
                                                startActivity(new Intent(getApplicationContext(), ProfileFragment.class));
                                                finish();
                                            }
                                            else if (!auth.getCurrentUser().getUid().equals(uid) && !uid.isEmpty())
                                            {
                                                Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
                                                i.putExtra("Username", user2.getText().toString());
                                                i.putExtra("uid", uid);
                                                startActivity(i);
                                                finish();
                                            }
                                            break;
                                        }
                                        else uid = "";
                                    }

                                }
                                else uid = "";
                            }
                        });
            }
        });
    }


    //method to logout the user
    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }


    public void profile(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileFragment.class));
        finish();
    }
}
