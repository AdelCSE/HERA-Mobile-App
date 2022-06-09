package dz.esisba.a2cpi_project.navigation_fragments.profile_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.UserProfileActivity;
import dz.esisba.a2cpi_project.adapter.RequestAdapter;
import dz.esisba.a2cpi_project.interfaces.SearchOnItemClick;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.models.RequestModel;

public class RequestsFragment extends Fragment implements SearchOnItemClick {

    private View parentHolder;
    private RecyclerView recyclerView;
    private ArrayList<RequestModel> RequestsDataHolder;
    private RequestAdapter adapter;
    private BottomSheetDialog dialog;
    private ProgressBar progressBar;
    private LinearLayout emptyRequests;

    private String downloadUrl;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference repliedByRef;
    private CollectionReference requestRef;
    private String repliedByName,repliedByUsername = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_requests, container, false);
        progressBar = parentHolder.findViewById(R.id.requestsProgressBar);
        emptyRequests = parentHolder.findViewById(R.id.emptyRequests);

        RequestsDataHolder = new ArrayList<>();
        dialog = new BottomSheetDialog(getActivity());

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        requestRef = fstore.collection("Users").document(user.getUid()).collection("Requests");

        //Get the Infos of the online user (YOU) //
        repliedByRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        repliedByRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(this.toString(), "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    repliedByUsername = snapshot.get("Username").toString();
                    if (snapshot.get("profilePictureUrl") != null) {
                        downloadUrl = snapshot.get("profilePictureUrl").toString();
                    }
                    if (snapshot.get("Name")!= null)
                    {
                        repliedByName = snapshot.get("Name").toString();
                    }
                }
                else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        fetchRequests();

        return parentHolder;
    }

    private void fetchRequests(){
        RequestsDataHolder = new ArrayList<>();

        requestRef.orderBy("Date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        RequestModel request = document.toObject(RequestModel.class);
                        RequestsDataHolder.add(request);
                    }
                    RequestModel lastItem = new RequestModel(null,null,null,null,null,null,null);
                    RequestsDataHolder.add(lastItem);
                    if(RequestsDataHolder.size() > 1){
                        buildRecyclerView();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else{
                        progressBar.setVisibility(View.GONE);
                        emptyRequests.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showReplyDialog(int position){
        View view = getLayoutInflater().inflate(R.layout.activty_add_answer,null,false);

        ImageButton closeReplyBtn = view.findViewById(R.id.closeAnswerBtn);
        EditText replyQuestion =  view.findViewById(R.id.answerEditTxt);
        TextView sendReply =  view.findViewById(R.id.postAnswerBtn);
        CircleImageView profileImg = view.findViewById(R.id.bottomsheetimg);

        Glide.with(getActivity()).load(downloadUrl).into(profileImg);

        closeReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        sendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!replyQuestion.getText().toString().isEmpty())
                {
                    PerformValidation(replyQuestion.getText().toString(),position);
                    dialog.dismiss();
                    DocumentReference requestRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                            .collection("Requests").document(RequestsDataHolder.get(position).getRequestId());
                    requestRef.delete();
                    RequestsDataHolder.remove(position);
                    adapter.notifyItemRemoved(position);
                    if(RequestsDataHolder.size()==1){
                        recyclerView.setVisibility(View.GONE);
                        emptyRequests.setVisibility(View.VISIBLE);
                        RequestsDataHolder.remove(0);
                    }
                }else {
                    replyQuestion.setError("Enter your question");
                }
            }
        });
        dialog.setContentView(view);
    }

    Date replyDate = new Date();
    private void PerformValidation(String reply,int position){

        DocumentReference replyRef = FirebaseFirestore.getInstance().collection("Users").document(RequestsDataHolder.get(position).getUid())
                .collection("Replies").document();

        String replyId = replyRef.getId();
        HashMap<String, Object> data = new HashMap<>();

        data.put("RequestId",replyId);
        data.put("Question",RequestsDataHolder.get(position).getQuestion());
        data.put("Reply", reply);
        data.put("Name",repliedByName);
        data.put("Username",repliedByUsername);
        data.put("Date",replyDate);
        data.put("ProfilePictureUrl",downloadUrl);
        data.put("Uid",user.getUid());

        DocumentReference df = fstore.collection("Users").document(RequestsDataHolder.get(position).getUid())
                .collection("Replies").document(replyId);
        df.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Reply has been sent successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Could not send reply, try again! " + task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void buildRecyclerView(){
        recyclerView = parentHolder.findViewById(R.id.recviewR);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RequestAdapter(RequestsDataHolder,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        RequestModel requestModel = RequestsDataHolder.get(position);
            DocumentReference requestRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                    .collection("Requests").document(RequestsDataHolder.get(position).getRequestId());
            requestRef.delete();
        RequestsDataHolder.remove(position);
        adapter.notifyItemRemoved(position);
        if(RequestsDataHolder.size()==1){
            recyclerView.setVisibility(View.GONE);
            emptyRequests.setVisibility(View.VISIBLE);
            RequestsDataHolder.remove(0);
        }
    }

    @Override
    public void onAnswerClick(int position) {
        showReplyDialog(position);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}