package dz.esisba.a2cpi_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPostActivity extends AppCompatActivity {

    private EditText questionEditTxt, bodyEditTxt;
    private ImageButton returnButton;
    private Button postButton;
    private CircleImageView profileImg;

    private Chip newborn, kid, baby,
                 sleeping,healthcare,breastfeeding,needs,circumcision,routine,food,
                 fever, influenza,hepatitis,conjunctivitis,other,
                 experience,motherhood,tools,guidance;


    private String askedByName,askedByUsername = "";
    private DocumentReference askedByRef;
    private ProgressDialog loader;

    private Date postBlockDate;

    private ArrayList<String> selectedTags = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private String onlineUserId = "";
    private String downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        questionEditTxt = findViewById(R.id.questionEdittxt);
        bodyEditTxt = findViewById(R.id.bodyEditTxt);
        returnButton = findViewById(R.id.returnBtn);
        postButton = findViewById(R.id.postBtn);
        profileImg = findViewById(R.id.profileImg);

        InitChips();

        loader = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        onlineUserId = user.getUid();

        askedByRef = FirebaseFirestore.getInstance().collection("Users").document(onlineUserId);
        //get username of poster
        askedByRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(this.toString(), "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    askedByUsername = snapshot.get("Username").toString();
                    if (snapshot.get("profilePictureUrl") != null) {
                         downloadUrl = snapshot.get("profilePictureUrl").toString();
                        Glide.with(getApplicationContext()).load(downloadUrl).into(profileImg);
                    }
                    if (snapshot.get("Name")!= null)
                    {
                        askedByName = snapshot.get("Name").toString();
                    }
                    if (snapshot.get("postBlockedUntil")!=null)
                    {
                        postBlockDate = snapshot.getDate("postBlockedUntil");
                    }

                }
                else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });

        SetCheckedTags();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable())
                {
                    View parentLayout = findViewById(android.R.id.content);
                    final Snackbar snackbar = Snackbar.make(parentLayout, "Please check your internet connection", Snackbar.LENGTH_LONG)
                            .setAction("TRY AGAIN", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    postButton.performClick();
                                }
                            });
                    snackbar.show();
                    return;
                }
                else if (isPostBlocked())
                {
                    SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy • HH:mm");
                    Toast.makeText(AddPostActivity.this, "You cannot perform this operation you are post blocked until "+sfd.format(postBlockDate)
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    performValidation();
                }
            }
        });


    }

    private boolean isPostBlocked() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        if (postBlockDate ==null) return false;
        else if (date.after(postBlockDate)) return false;
        else return true;
    }

    private void SetCheckedTags()
    {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    selectedTags.add(compoundButton.getText().toString());
                }
                else
                {
                    selectedTags.remove(compoundButton.getText().toString());
                }
            }
        };

        newborn.setOnCheckedChangeListener(checkedChangeListener);
        baby.setOnCheckedChangeListener(checkedChangeListener);
        kid.setOnCheckedChangeListener(checkedChangeListener);
        /*sleeping,healthcare,breastfeeding,needs,circumcision,routine,food,
                fever, influenza,hepatitis,conjunctivitis,other,
                experience,motherhood,tools,guidance;*/
        sleeping.setOnCheckedChangeListener(checkedChangeListener);
        healthcare.setOnCheckedChangeListener(checkedChangeListener);
        breastfeeding.setOnCheckedChangeListener(checkedChangeListener);
        needs.setOnCheckedChangeListener(checkedChangeListener); // i love programming v2
        circumcision.setOnCheckedChangeListener(checkedChangeListener);
        routine.setOnCheckedChangeListener(checkedChangeListener);
        fever.setOnCheckedChangeListener(checkedChangeListener);
        influenza.setOnCheckedChangeListener(checkedChangeListener);
        hepatitis.setOnCheckedChangeListener(checkedChangeListener);
        conjunctivitis.setOnCheckedChangeListener(checkedChangeListener);
        other.setOnCheckedChangeListener(checkedChangeListener);
        experience.setOnCheckedChangeListener(checkedChangeListener);
        motherhood.setOnCheckedChangeListener(checkedChangeListener);
        food.setOnCheckedChangeListener(checkedChangeListener);
        tools.setOnCheckedChangeListener(checkedChangeListener);
        guidance.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void InitChips() {

        newborn = findViewById(R.id.newborn);
        kid = findViewById(R.id.kid);
        baby = findViewById(R.id.baby);
        /*sleeping,healthcare,breastfeeding,needs,circumcision,routine,food,
                fever, influenza,hepatitis,conjunctivitis,other,
                experience,motherhood,tools,guidance;*/
        sleeping = findViewById(R.id.sleeping);
        healthcare = findViewById(R.id.healthcare);
        breastfeeding = findViewById(R.id.breastfeeding);
        needs = findViewById(R.id.needs);
        circumcision = findViewById(R.id.circumcision);
        routine = findViewById(R.id.routine);
        food = findViewById(R.id.food);
        fever = findViewById(R.id.fever);
        influenza = findViewById(R.id.influenza); // i love programming :)
        hepatitis = findViewById(R.id.hepatitis);
        conjunctivitis = findViewById(R.id.conjunctivitis);
        other = findViewById(R.id.other);
        experience = findViewById(R.id.experience); //is there no other fucking better way to do this shit???
        motherhood = findViewById(R.id.motherhood);
        tools = findViewById(R.id.tools);
        guidance = findViewById(R.id.guidance);
    }

    String getQuestionEditTxt() {
        return questionEditTxt.getText().toString().trim();
    }

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Date date = new Date();
    DocumentReference ref = FirebaseFirestore.getInstance().collection("Posts").document();

    String getBodyEditTxt() {
        return bodyEditTxt.getText().toString().trim();
    }

    private void performValidation()
    {
        if (selectedTags.size()<1)
        {
            Toast.makeText(this, "Please select atleast one tag", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getQuestionEditTxt().isEmpty())
        {
            questionEditTxt.setError("Enter your question");
            return;
        }
        if (getBodyEditTxt().isEmpty())
        {
            bodyEditTxt.setError("Please enter some details");
            return;
        }

            startLoader();
            String postId = ref.getId();
            HashMap<String, Object> data = new HashMap<>();
            data.put("postid", postId);
            data.put("publisherPic", downloadUrl);
            data.put("question", getQuestionEditTxt());
            data.put("body", getBodyEditTxt());
            data.put("publisher", onlineUserId);
            data.put("askedBy", askedByName);
            data.put("Username", askedByUsername);
            data.put("Date", date);
            data.put("tags",selectedTags);
            data.put("answersCount", 0);
            data.put("reportsCount", 0);
            ArrayList likes = new ArrayList();
            data.put("likes", likes);
            data.put("likesCount", 0);

           DocumentReference df = fstore.collection("Posts").document(postId);
           df.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   Log.d("____________", "onComplete: 11111");
                   if (task.isSuccessful()) {
                       Log.d("____________", "onComplete: 2222222222");
                       askedByRef.update("posts", FieldValue.arrayUnion(postId));
                       Toast.makeText(AddPostActivity.this, "Question posted successfully", Toast.LENGTH_SHORT).show();
                       loader.dismiss();
                       startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
                       finish();
                   }
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(AddPostActivity.this, "Could not post question " + e.getMessage(),
                           Toast.LENGTH_SHORT).show();
                   loader.dismiss();
               }
           });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void startLoader()
    {
        loader.setMessage("Publishing...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }
}