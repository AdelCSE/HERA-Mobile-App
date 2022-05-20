package dz.esisba.a2cpi_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import dz.esisba.a2cpi_project.models.UserModel;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private StorageReference storageReference;
    private Uri resultUri;

    private EditText name1, name2, bio;
    private ImageButton confirmButton;
    private CircleImageView profilePic;
    private String ppURL;
    private boolean infoUploaded = false;
    private boolean datachange = false;
    private String imageUrl;
    private boolean loadpp = true;
    private int i =0;

    private UserModel previousInfo;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name1 = findViewById(R.id.nameEditText);
        name2 = findViewById(R.id.nameEditText2);
        bio = findViewById(R.id.bioEditText);
        confirmButton = findViewById(R.id.confirmButton);
        profilePic = findViewById(R.id.profilePic);
        loader = new ProgressDialog(this);


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
                        previousInfo = doc.toObject(UserModel.class);
                        if (doc.get("profilePictureUrl")!= null && loadpp) {
                            String downloadUrl = doc.get("profilePictureUrl").toString();
                            Glide.with(EditProfileActivity.this).load(downloadUrl).into(profilePic);
                        }
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


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMedia();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable())
                {
                    View parentLayout = findViewById(android.R.id.content);
                    final Snackbar snackbar = Snackbar.make(parentLayout, "Please check your internet connection", Snackbar.LENGTH_LONG)
                            .setAction("TRY AGAIN", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    confirmButton.performClick();
                                }
                            });
                    snackbar.show();
                    return;
                }
                else {
                    UpdateInfo(df);
                }
            }
        });

    }

    private void UpdateInfo(DocumentReference df) {
        datachange = true;
       // DisableEverything();
        startLoader();
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
                                    UpdateNameInPosts();
                                    View parentLayout = findViewById(android.R.id.content);
                                    final Snackbar snackbar = Snackbar.make(parentLayout, "Information updated", Snackbar.LENGTH_INDEFINITE)
                                            .setAction("RETURN", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    onBackPressed();
                                                }
                                            });
                                    snackbar.show();
                                    infoUploaded= true;
                                    loader.dismiss();
                                }
                            });
                        }
                    }
                }
                else loader.dismiss();
            }
        });
    }

    private void DisableEverything()
    {
        profilePic.setEnabled(false);
        name1.setEnabled(false);
        name2.setEnabled(false);
        bio.setEnabled(false);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                        df.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                UpdateNameInPosts();
                                UpdatePictureInPosts();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Failure();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Failure();
                        return;
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Failure();
            }
        });
    }

    private void UpdatePictureInPosts()
    {
        fstore.collection("Posts").whereEqualTo("publisher", user.getUid())
                .get() //update picture in posts
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            infoUploaded= true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference pr = document.getReference();
                                Map<String,Object> hm = new HashMap();
                                hm.put("publisherPic", imageUrl);
                                pr.update(hm).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Failure();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loader.dismiss();
                                        View parentLayout = findViewById(android.R.id.content);
                                        final Snackbar snackbar = Snackbar.make(parentLayout, "Information updated", Snackbar.LENGTH_LONG)
                                                .setAction("RETURN", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        onBackPressed();
                                                    }
                                                });
                                        snackbar.show();
                                    }
                                });
                            }
                        }
                        else loader.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Failure();
            }
        });
    }

    private void Failure()
    {
        Toast.makeText(EditProfileActivity.this, "Unknow error occured please try again later", Toast.LENGTH_SHORT).show();
        loader.dismiss();
    }

    private void pickMedia() {
        datachange = true;
        String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
        ImagePicker.Companion.with(this)
                .galleryMimeTypes(mimeTypes)
                .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent(intent -> {
                    startForMediaPickerResult.launch(intent);
                    return null;
                });
    }

    private final ActivityResultLauncher<Intent> startForMediaPickerResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data != null && result.getResultCode() == Activity.RESULT_OK) {
                    resultUri = data.getData();
                    if (resultUri != null)
                    {
                        loadpp = false;
                        profilePic.setImageURI(resultUri);
                    }
                }
                else {
                    Toast.makeText(EditProfileActivity.this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onBackPressed () {
        if (infoUploaded)
        super.onBackPressed ();
        else if (!infoUploaded && !datachange){
            View parentLayout = findViewById(android.R.id.content);
            final Snackbar snackbar = Snackbar.make(parentLayout, "Please confirm you info", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
            snackbar.show();
        }
        else if (!infoUploaded)
        {
            View parentLayout = findViewById(android.R.id.content);
            final Snackbar snackbar = Snackbar.make(parentLayout, "Please wait while we finish uploading your info", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
            snackbar.show();
        }
    }
    private void startLoader()
    {
        loader.setMessage("Updating your info...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }

}

//TODO: change all profile pictures to circular image view