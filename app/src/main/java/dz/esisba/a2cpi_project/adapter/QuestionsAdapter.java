package dz.esisba.a2cpi_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.interfaces.PostsOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.myviewholder>{

    private ArrayList<PostModel> QuestionsHolder;
    private PostsOnItemClickListner aListner;
    private Context context;

    public QuestionsAdapter(ArrayList<PostModel> questionsHolder, PostsOnItemClickListner listner) {
        QuestionsHolder = questionsHolder;
        aListner = listner;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profile_question,parent,false);
        context = parent.getContext();
        return new myviewholder(view, aListner , this);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Glide.with(context).load(QuestionsHolder.get(position).getPublisherPic()).into(holder.img);
        holder.Question.setText(QuestionsHolder.get(position).getQuestion());
        holder.Name.setText(QuestionsHolder.get(position).getAskedBy());
        holder.Username.setText("@"+QuestionsHolder.get(position).getUsername());
        holder.Details.setText(QuestionsHolder.get(position).getBody());
        holder.Likes.setText(Integer.toString(QuestionsHolder.get(position).getLikesCount())+" Likes");
        holder.Answers.setText(Integer.toString(QuestionsHolder.get(position).getAnswersCount()));
        holder.Date.setText(QuestionsHolder.get(position).ConvertDate());
    }

    @Override
    public int getItemCount() {
        return QuestionsHolder.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder {

        TextView Question,Details,Username,Name,Likes,Answers,Date;
        CircleImageView img;

        private FirebaseAuth auth;
        private FirebaseUser user;
        private FirebaseFirestore fstore;
        private LinearLayout emptyPosts;

        public myviewholder(@NonNull View itemView  , PostsOnItemClickListner listner , QuestionsAdapter adapter) {
            super(itemView);
            img = itemView.findViewById(R.id.imgp);
            Question = itemView.findViewById(R.id.questionp);
            Name = itemView.findViewById(R.id.namep);
            Username = itemView.findViewById(R.id.usernamepq);
            Details = itemView.findViewById(R.id.detailsp);
            Likes = itemView.findViewById(R.id.likesp);
            Answers = itemView.findViewById(R.id.answersp);
            Date = itemView.findViewById(R.id.postDatep);

            emptyPosts = itemView.findViewById(R.id.emptyQuestions);
            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            user = auth.getCurrentUser();


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onItemClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.answerBtnp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onAnswerClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.questionMenuBtnp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    if (user.getUid().equals(QuestionsHolder.get(position).getPublisher()))
                        popupMenu.inflate(R.menu.my_post_menu);
                    else popupMenu.inflate(R.menu.post_menu);


                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            boolean b = false;
                            PostModel postModel = QuestionsHolder.get(position);
                            if (menuItem.getTitle().equals("Delete")) {
                                QuestionsHolder.remove(position);
                                adapter.notifyItemRemoved(position);
                                //DeleteLikes(postModel, "Likes");
                                DocumentReference postRef = FirebaseFirestore.getInstance().collection("Posts").document(postModel.getPostid());
                                CollectionReference answers = FirebaseFirestore.getInstance().collection("Posts").
                                        document(postModel.getPostid()).collection("Answers");
                                answers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                document.getReference().delete();
                                            }
                                            postRef.delete();
                                            DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users")
                                                    .document(user.getUid());
                                            userRef.update("posts", FieldValue.arrayRemove(postModel.getPostid()));
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
    }
}
