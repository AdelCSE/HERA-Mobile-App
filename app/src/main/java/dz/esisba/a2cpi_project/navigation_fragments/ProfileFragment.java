package dz.esisba.a2cpi_project.navigation_fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.EditProfileActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.adapter.ProfileAdapter;
import dz.esisba.a2cpi_project.interfaces.GetUserInterface;
import dz.esisba.a2cpi_project.models.UserModel;

public class ProfileFragment extends Fragment {

    private UserModel userModel;
    private ArrayList<String> followers, followings;

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


    private String[] titles = {"Questions" , "Answers" , "Requests", "Replies"};
    private boolean loadbanner = false;



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
        //Toast.makeText(getActivity(), Boolean.toString(loadbanner), Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
        bannerReference = FirebaseStorage.getInstance().getReference().child("profileBanners");

        DocumentReference df = fstore.collection("Users").document(user.getUid());

        //change banner
        bannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadbanner = false;
               pickMedia();
            }
        });

        confirmBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmBanner.setVisibility(View.GONE);

                StorageReference fileReference = bannerReference.child(user.getUid());
                if(resultUri!=null) {
                    fileReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    userModel.setBannerUrl(imageUrl);
                                    Map<String,Object> hashMap = new HashMap();
                                    hashMap.put("bannerUrl", imageUrl);
                                    df.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(getActivity(), "Banner updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
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
            }
        });

        GetCurrentUserModelAndSetInfo();

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

    private void GetCurrentUserModelAndSetInfo()
    {
        GetUserInterface id = (GetUserInterface) getActivity();
        userModel = id.getUserModel();

        followers = userModel.getFollowers();
        followings = userModel.getFollowing();

        if (followers!=null) {
            String i = Integer.toString(followers.size());
            followersCount.setText(i);
        }
        if (followings!=null) {
            String j = Integer.toString(followings.size());
            followingCount.setText(j);
        }

        //set username
        toolbarLayout.setTitle(userModel.getUsername());

        if (userModel.getProfilePictureUrl()!= null) { //set profile pic
            String downloadUrl = userModel.getProfilePictureUrl();
            Glide.with(ProfileFragment.this).load(downloadUrl).into(profileImg);
        }
        if (userModel.getBannerUrl()!= null && loadbanner) { //set banner
            String downloadUrl = userModel.getBannerUrl();
            Glide.with(ProfileFragment.this).load(downloadUrl).into(banner);
        }
        if (userModel.getName()!= null) { //set name
            name.setText(userModel.getName());
        }
        if(userModel.getBio()!=null)
        { //set bio
            bio.setText(userModel.getBio());
        }
    }

    private final ActivityResultLauncher<Intent> startForMediaPickerResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                loadbanner = false;
                Intent data = result.getData();
                if (data != null && result.getResultCode() == Activity.RESULT_OK) {
                    confirmBanner.setVisibility(View.VISIBLE);
                    resultUri = data.getData();
                    if (resultUri != null)banner.setImageURI(resultUri);

                }
                else {
                    Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                }
            });

//TODO: add cancel banner
}
