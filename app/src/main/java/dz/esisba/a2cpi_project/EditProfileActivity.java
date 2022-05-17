package dz.esisba.a2cpi_project;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private StorageReference storageReference;
    private Uri resultUri;

    private EditText name1, name2, bio;
    private ImageButton confirmButton, returnButton;
    private CircleImageView profilePic;

    private String ppURL;
    private boolean imageUploaded;
    private String imageUrl;
    private boolean ppUpdated = false;
    private int i =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name1 = findViewById(R.id.nameEditText);
        name2 = findViewById(R.id.nameEditText2);
        bio = findViewById(R.id.bioEditText);
        confirmButton = findViewById(R.id.confirmButton);
        returnButton = findViewById(R.id.returnButton);
        profilePic = findViewById(R.id.profilePic);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
        DocumentReference df = fstore.collection("Users").document(user.getUid());

        //grab the existing info
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        /*if (doc.get("profilePictureUrl")!= null) {
                            String downloadUrl = doc.get("profilePictureUrl").toString();
                            Glide.with(EditProfileActivity.this).load(downloadUrl).into(profilePic);
                        }*/
                        if (doc.get("Name")!= null) {
                            String n = doc.get("Name").toString();
                            String n1 = "";
                            if (n.equals(" ") || n.isEmpty()) {
                                name1.setText(n); // put them in name1
                                name2.setText(n); //put last string in name2
                            }
                            else
                            {
                                String[] splitStr = n.split("\\s+"); //split words into spaces
                                for (int i = 0; i < splitStr.length - 1; i++) { //get all strings from first to before last
                                    n1 += splitStr[i] + " ";
                                }
                                name1.setText(n1); // put them in name1
                                name2.setText(splitStr[splitStr.length - 1]); //put last string in name2
                            }
                        }
                        if(doc.get("Bio")!=null)
                        {
                            bio.setText(doc.get("Bio").toString());
                        }
                    }
                }
            }
        });

        returnButton.setEnabled(false);
        returnButton.setBackgroundResource(R.drawable.btndisabled);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(EditProfileActivity.this)
                        .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

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
                                Map<String,Object> hashMap = new HashMap(); //represents key, value
                                hashMap.put("Name", name1.getText().toString()+" "+name2.getText().toString());
                                hashMap.put("Bio", bio.getText().toString());
                                StorageReference fileReference = storageReference.child(user.getUid());
                                if(resultUri!=null) {
                                    UploadImage(fileReference, hashMap, df);
                                }
                                else
                                {
                                    df.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            returnButton.setEnabled(true);
                                            returnButton.setBackgroundResource(R.drawable.mainbtn);
                                            UpdateNameInPosts();
                                            Toast.makeText(EditProfileActivity.this, "Information updated!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });



        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BottomNavigationActivity.class);
                i.putExtra("Name", name1.getText().toString()+" "+name2.getText().toString());
                i.putExtra("Bio", bio.getText().toString());
                startActivity(i);
                finish();
            }
        });

    }

    private void UpdateNameInPosts() {
        fstore.collection("Posts").whereEqualTo("publisher", user.getUid())
                .get() //update picture in posts
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference pr = document.getReference();
                                Map<String,Object> hm = new HashMap();
                                hm.put("askedBy", name1.getText().toString()+" "+name2.getText().toString());
                                pr.update(hm);
                            }
                        }
                    }
                });
    }

    private void UploadImage(StorageReference fileReference, Map<String, Object> hashMap, DocumentReference df)
    {
        //upload to storage
        fileReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        hashMap.put("profilePictureUrl", imageUrl);
                        fstore.collection("Posts").whereEqualTo("publisher", user.getUid())
                                .get() //update picture in posts
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            i=0;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                DocumentReference pr = document.getReference();
                                                Map<String,Object> hm = new HashMap();
                                                hm.put("publisherPic", imageUrl);
                                                pr.update(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        df.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                returnButton.setEnabled(true);
                                                                returnButton.setBackgroundResource(R.drawable.mainbtn);
                                                                UpdateNameInPosts();
                                                                Toast.makeText(EditProfileActivity.this, "Information updated", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Unknow error occured please try again later", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        resultUri = data.getData();
       if (resultUri != null)profilePic.setImageURI(resultUri);
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /*private void UploadFile()
    {
        if (resultUri!=null)
        {
            StorageReference fileReference = storageReference.child(user.getUid()+"."+getFileExtension(resultUri));
            fileReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata().getReference()!=null)
                    {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) { //TODO: ADD ON PROGRESS LISTENER LATER
                    //double progress = (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    // progressbar.setprogress((int) progress);
                }
            });
        }
    }*/
}

//TODO: change all profile pictures to circular image view