package dz.esisba.a2cpi_project.navigation_fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
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
    private CollectionReference requestRef,repliesRef;


    private TextView username, name, bio, followersCount, followingCount, reputationText;
    private ImageButton editProfile;
    private Button bannerBtn,confirmBanner;
    private CollapsingToolbarLayout toolbarLayout;
    private CircleImageView profileImg;
    private ImageView banner;
    private ProgressDialog loader;

    View Holder;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    ProfileAdapter adapter;

    private boolean loadbanner = true;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Holder = inflater.inflate(R.layout.fragment_profile,container,false);

        viewPager = Holder.findViewById(R.id.viewPagerp);
        tabLayout = Holder.findViewById(R.id.tabLayoutp);

        adapter = new ProfileAdapter(getChildFragmentManager(),getLifecycle());
        viewPager.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
        bannerReference = FirebaseStorage.getInstance().getReference().child("profileBanners");

        requestRef = fstore.collection("Users").document(user.getUid()).collection("Requests");
        repliesRef = fstore.collection("Users").document(user.getUid()).collection("Replies");

        TabLayoutMediator  tabLayoutMediator =
                new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch(position){
                    case 0:
                        tab.setText("Questions");
                        break;
                    case 1:
                        tab.setText("Answers");
                        break;
                    case 2:
                        tab.setText("Requests");
                        BadgeDrawable badge = tab.getOrCreateBadge();
                        badge.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.button));
                        badge.setVisible(false);
                        requestRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().size() != 0){
                                        badge.setVisible(true);
                                    }else{
                                        badge.setVisible(false);
                                    }
                                }
                            }
                        });

                        break;
                    case 3:
                        tab.setText("Replies");
                        BadgeDrawable badge1 = tab.getOrCreateBadge();
                        badge1.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.button));
                        badge1.setVisible(false);
                        repliesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().size() != 0){
                                        badge1.setVisible(true);
                                    }else{
                                        badge1.setVisible(false);
                                    }
                                }
                            }
                        });
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

        username = Holder.findViewById(R.id.usernameTxt);
        name = Holder.findViewById(R.id.profileName);
        reputationText = Holder.findViewById(R.id.reputationText);
        bio = Holder.findViewById(R.id.bioText);
        followersCount = Holder.findViewById(R.id.fllwNb2);
        followingCount = Holder.findViewById(R.id.fllwingNb2);
        editProfile = Holder.findViewById(R.id.editProfile);
        bannerBtn = Holder.findViewById(R.id.bannerBtn);
        banner = Holder.findViewById(R.id.banner);
        confirmBanner = Holder.findViewById(R.id.bannerConfirm);
        toolbarLayout = Holder.findViewById(R.id.CollapsingToolBarLayout);
        profileImg = Holder.findViewById(R.id.profileImg);
        loader = new ProgressDialog(getActivity());

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });


        DocumentReference df = fstore.collection("Users").document(user.getUid());

        //change banner
        bannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ChangeBanner();
            }
        });

        confirmBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmBanner.setVisibility(View.GONE);
                startLoader();
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
                                            loader.dismiss();
                                            Toast.makeText(getActivity(), "Banner has been updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Unknown error occured, please try again later!", Toast.LENGTH_SHORT).show();
                                    loader.dismiss();
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

    private void ChangeBanner() {
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
        if (userModel!=null) {
            followers = userModel.getFollowers();
            followings = userModel.getFollowing();

            if (followers != null) {
                String i = Integer.toString(followers.size());
                followersCount.setText(i);
            }
            if (followings != null) {
                String j = Integer.toString(followings.size());
                followingCount.setText(j);
            }

            //set username
            toolbarLayout.setTitle(userModel.getUsername());

            if (userModel.getProfilePictureUrl() != null) { //set profile pic
                String downloadUrl = userModel.getProfilePictureUrl();
                Glide.with(ProfileFragment.this).load(downloadUrl).into(profileImg);
            }
            if (userModel.getBannerUrl() != null && loadbanner) { //set banner
                String downloadUrl = userModel.getBannerUrl();
                Glide.with(ProfileFragment.this).load(downloadUrl).into(banner);
            }
            if (userModel.getName() != null) { //set name
                name.setText(userModel.getName());
            }
            if (userModel.getBio() != null) { //set bio
                bio.setText(userModel.getBio());
            }
            reputationText.setText(userModel.getReputation()+"");
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
                    if (resultUri != null)
                    {
                        banner.setImageURI(resultUri);
                        loadbanner = false;
                    }

                }
                else {
                    Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                }
            });

    private void startLoader()
    {
        loader.setMessage("Updating banner...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }

//TODO: add cancel banner
}
