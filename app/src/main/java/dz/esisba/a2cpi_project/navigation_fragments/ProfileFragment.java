package dz.esisba.a2cpi_project.navigation_fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.EditProfileActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.ProfileAdapter;

public class ProfileFragment extends Fragment {


    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private StorageReference storageReference,bannerReference;
    private Uri resultUri;

    private TextView username, name, bio, followersCount, followingCount;
    private ImageButton editProfile;
    private Button bannerBtn,confirmBanner;
    private CollapsingToolbarLayout toolbarLayout;
    private CircleImageView profileImg;
    private ImageView banner;

    View Holder;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    ProfileAdapter adapter;


    private String[] titles = {"All" , "Questions" , "Answers" , "Requests"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Holder = inflater.inflate(R.layout.fragment_profile,container,false);

        viewPager = Holder.findViewById(R.id.viewPagerp);
        tabLayout = Holder.findViewById(R.id.tabLayoutp);

        adapter = new ProfileAdapter(getChildFragmentManager(),getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(titles[position]))).attach();

        username = Holder.findViewById(R.id.usernameTxt);
        name = Holder.findViewById(R.id.nameText);
        bio = Holder.findViewById(R.id.bioText);
        followersCount = Holder.findViewById(R.id.fllwNb2);
        followingCount = Holder.findViewById(R.id.fllwingNb2);
        editProfile = Holder.findViewById(R.id.editProfile);
        bannerBtn = Holder.findViewById(R.id.bannerBtn);
        banner = Holder.findViewById(R.id.banner);
        confirmBanner = Holder.findViewById(R.id.bannerConfirm);
        toolbarLayout = Holder.findViewById(R.id.CollapsingToolBarLayout);
        profileImg = Holder.findViewById(R.id.profileImg);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
        bannerReference = FirebaseStorage.getInstance().getReference().child("profileBanners");


        DocumentReference df = fstore.collection("Users").document(user.getUid());
        //set user info
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        //set username
                        username.setText("@"+ doc.get("Username").toString());
                        toolbarLayout.setTitle(doc.get("Username").toString());

                        if (doc.get("profilePictureUrl")!= null) { //set profile pic
                            String downloadUrl = doc.get("profilePictureUrl").toString(); //TODO: ADD ON SUCCESS LISTENER
                            Glide.with(ProfileFragment.this).load(downloadUrl).into(profileImg);
                        }
                        if (doc.get("bannerUrl")!= null) { //set banner
                            String downloadUrl = doc.get("bannerUrl").toString();
                            Glide.with(ProfileFragment.this).load(downloadUrl).into(banner);
                        }
                        if (doc.get("Name")!= null) { //set name
                            name.setText(doc.get("Name").toString());
                        }
                        if(doc.get("Bio")!=null)
                        { //set bio
                            bio.setText(doc.get("Bio").toString());
                        }
                    }
                }
            }
        });

        //get followers and following count
        Task<QuerySnapshot> followingReference = df.collection("following").
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size()>0) {
                        int size = task.getResult().size();
                        followingCount.setText(Integer.toString(size));
                    }
                }
            }
        });


        Task<QuerySnapshot> followerReference = df.collection("followers").
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size()>0) {
                        int size = task.getResult().size();
                        followersCount.setText(Integer.toString(size));
                    }
                }
            }
        });


        //change banner
        bannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               pickMedia();
               confirmBanner.setVisibility(View.VISIBLE);
            }
        });

        confirmBanner.setOnClickListener(new View.OnClickListener() {
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
                                StorageReference fileReference = bannerReference.child(user.getUid());
                                if(resultUri!=null) {
                                    fileReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    Map<String,Object> hashMap = new HashMap();
                                                    hashMap.put("bannerUrl", imageUrl);
                                                    df.update(hashMap);
                                                    Toast.makeText(getActivity(), "Banner updated!", Toast.LENGTH_SHORT).show();
                                                    confirmBanner.setVisibility(View.GONE);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), "Unknow error occured please try again later", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            });
                                        }
                                    });

                                }
                                else Toast.makeText(getActivity(), "uri null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        return Holder;
    }
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        resultUri = data.getData();
        if (resultUri != null)banner.setImageURI(resultUri);
    }*/

    private void pickMedia() {
        String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
        ImagePicker.Companion.with(this)
                .galleryMimeTypes(mimeTypes)
                .crop(8, 5)
                .compress(1024)
                .maxResultSize(1080, 1080)
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
                    if (resultUri != null)banner.setImageURI(resultUri);
                }
                else {
                    Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                }
            });
//TODO: add cancel banner
}