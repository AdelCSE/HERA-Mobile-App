package dz.esisba.a2cpi_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.interfaces.PostsOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myviewholder> {

    private ArrayList<PostModel> PostsHolder;
    private Context context;
    private PostsOnItemClickListner mListner;



    public PostAdapter(ArrayList<PostModel> postsHolder ,PostsOnItemClickListner mlistner) {
        PostsHolder = postsHolder;
        mListner = mlistner;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post,parent,false);
        return new myviewholder(view , mListner, PostsHolder, this);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        Glide.with(context).load(PostsHolder.get(position).getPublisherPic()).into(holder.img);
        holder.Question.setText(PostsHolder.get(position).getQuestion());
        holder.Name.setText(PostsHolder.get(position).getAskedBy());
        holder.Username.setText("@"+PostsHolder.get(position).getUsername());
        holder.Details.setText(PostsHolder.get(position).getBody());
        holder.Likes.setText(Integer.toString(PostsHolder.get(position).getLikesCount()));
        holder.Answers.setText(Integer.toString(PostsHolder.get(position).getAnswersCount()));
        holder.Date.setText(PostsHolder.get(position).getDate().toString());
        RunCheckForLikes(PostsHolder.get(position), holder.likeBtn);
    }

    private void RunCheckForLikes(PostModel post, LottieAnimationView lottieAnimationView) {
        ArrayList<String> likes = post.getLikes();

        if (likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            lottieAnimationView.setSpeed(100);
            lottieAnimationView.playAnimation();
            lottieAnimationView.setTag("Liked");
        }
        else
        {
            lottieAnimationView.setTag("Like");
        }
        /*likesRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override //check if the document exists, i.e current user likes the post
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        lottieAnimationView.setSpeed(100);
                        lottieAnimationView.playAnimation();
                        lottieAnimationView.setTag("Liked");
                    }
                    else
                    {
                        lottieAnimationView.setTag("Like");
                    }
                }
            }
        });*/
    }



    @Override
    public int getItemCount() {
        return PostsHolder.size();
    }

    public static class myviewholder extends RecyclerView.ViewHolder {

        LottieAnimationView likeBtn;
        ImageView img;
        TextView Question,Details,Name,Username,Likes,Answers,Date;

        private FirebaseAuth auth;
        private FirebaseUser user;
        private FirebaseFirestore fstore;

        public myviewholder (@NonNull View itemView , PostsOnItemClickListner listner , ArrayList<PostModel> postHolder, PostAdapter adapter){
            super(itemView);
            img = itemView.findViewById(R.id.img);
            Question = itemView.findViewById(R.id.question);
            Name = itemView.findViewById(R.id.name);
            Username = itemView.findViewById(R.id.usernamep);
            Details = itemView.findViewById(R.id.details);
            Likes = itemView.findViewById(R.id.likes);
            Answers = itemView.findViewById(R.id.answers);
            Date = itemView.findViewById(R.id.postDate);
            likeBtn = itemView.findViewById(R.id.lottieLike);

            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            user = auth.getCurrentUser();


            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onLikeClick(position,likeBtn, Likes, false);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onItemClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.questionShareBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onShareClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.answerBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onAnswerClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onPictureClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onNameClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.questionMenuBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                    if (user.getUid().equals(postHolder.get(position).getPublisher()))
                        popupMenu.inflate(R.menu.my_post_menu);
                    else popupMenu.inflate(R.menu.post_menu);


                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            boolean b = false;
                            PostModel postModel = postHolder.get(position);
                            if (menuItem.getTitle().equals("Delete")) {
                                postHolder.remove(position);
                                adapter.notifyItemRemoved(position);
                                //DeleteLikes(postModel, "Likes");
                                DocumentReference postRef = FirebaseFirestore.getInstance().collection("Posts").document(postModel.getPostid());
                                CollectionReference answers =FirebaseFirestore.getInstance().collection("Posts").
                                        document(postModel.getPostid()).collection("Answers");
                                answers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                document.getReference().delete();
                                            }
                                            postRef.delete();
                                            Toast.makeText(view.getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(view.getContext(), "Some error occurred try again later", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_SHORT).show();
                                b = true;
                            }
                            else {
                                int reportCount = postModel.getReportsCount();
                                reportCount++;
                                DocumentReference postRef = FirebaseFirestore.getInstance().collection("Posts").document(postModel.getPostid());
                                HashMap<String, Object> hm = new HashMap<>();
                                hm.put("reportsCount", reportCount);
                                postRef.update(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "Your report has been sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                b = true;
                            }
                            return b;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        /*private void DeleteLikes(PostModel postModel, String collection)
        {
            CollectionReference userRef = FirebaseFirestore.getInstance().collection("Users");
            DocumentReference postRef = FirebaseFirestore.getInstance().collection("Posts")
                    .document(postModel.getPostid());
            postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot doc) {
                    ArrayList<String> likes = (ArrayList<String>) doc.get("likes");
                    for (String id: likes) {
                        Task<QuerySnapshot> cr = userRef.document(id).collection(collection).
                                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }*/
    }
}
