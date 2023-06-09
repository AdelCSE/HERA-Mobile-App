package dz.esisba.a2cpi_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.interfaces.AnswersOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class AnswersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<PostModel> AnswerHolder;
    AnswersOnItemClickListner aListner;
    private Context context;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;

    public AnswersAdapter(ArrayList<PostModel> answersHolder) {
        AnswerHolder = answersHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (AnswerHolder.get(position).getPublisher() != null) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profile_answer, parent, false);
            context = parent.getContext();
            return new myviewholder(view, aListner, AnswerHolder, this);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_item_text, parent, false);
            context = parent.getContext();
            return new myviewholder2(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            myviewholder viewholder = (myviewholder) holder;
            Glide.with(context).load(AnswerHolder.get(position).getPublisherPic()).into(viewholder.img);
            viewholder.Question.setText(AnswerHolder.get(position).getQuestion());
            viewholder.Username.setText(AnswerHolder.get(position).getUsername());
            viewholder.Details.setText(AnswerHolder.get(position).getBody());
            viewholder.Likes.setText(Integer.toString(AnswerHolder.get(position).getLikesCount()) + " Likes");
            viewholder.Date.setText(AnswerHolder.get(position).ConvertDate());
        } else {
            myviewholder2 viewholder2 = (myviewholder2) holder;
            viewholder2.lastItem.setText("That's it");
            viewholder2.txt1.setText("Feel free to answer the questions you know ");
            viewholder2.txt2.setText("to help more people");
        }

    }

    @Override
    public int getItemCount() {
        return AnswerHolder.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder {

        TextView Question, Details, Username, Likes, Date;
        CircleImageView img;

        public myviewholder(@NonNull View itemView, AnswersOnItemClickListner listner, ArrayList<PostModel> postHolder, AnswersAdapter adapter) {
            super(itemView);
            img = itemView.findViewById(R.id.imgpa);
            Question = itemView.findViewById(R.id.questionpa);
            Username = itemView.findViewById(R.id.usernamepa);
            Details = itemView.findViewById(R.id.detailspa);
            Likes = itemView.findViewById(R.id.likespa);
            Date = itemView.findViewById(R.id.postDatepa);

            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            user = auth.getCurrentUser();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listner.onShareClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.answerMenuBtnpa).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    if (user.getUid().equals(AnswerHolder.get(position).getPublisher()))
                        popupMenu.inflate(R.menu.my_post_menu);
                    else popupMenu.inflate(R.menu.post_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            boolean b = false;
                            PostModel answerModel = postHolder.get(position);
                            String id = answerModel.getPostid().split("#")[0];
                            if (menuItem.getTitle().equals("Delete")) {
                                postHolder.remove(position);
                                adapter.notifyItemRemoved(position);
                                //       DeleteLikes(answerModel, "AnswerLikes");

                                DocumentReference dr = fstore.collection("Posts").document(id);
                                dr.update("answersCount", FieldValue.increment(-1));
                                DocumentReference answerRef = dr.collection("Answers").document(answerModel.getPostid());
                                answerRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users")
                                                .document(user.getUid());
                                        userRef.update("answers", FieldValue.arrayRemove(answerModel.getPostid()));
                                        Toast.makeText(view.getContext(), "Answer deleted", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Some error occurred try again later", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                b = true;
                            } else {
                                DocumentReference answerRef = FirebaseFirestore.getInstance().collection("Posts").
                                        document(id).collection("Answers").document(answerModel.getPostid());
                                answerRef.update("reportsCount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public class myviewholder2 extends RecyclerView.ViewHolder {
        TextView lastItem, txt1, txt2;

        public myviewholder2(@NonNull View itemView) {
            super(itemView);
            lastItem = itemView.findViewById(R.id.lastItem);
            txt1 = itemView.findViewById(R.id.txt);
            txt2 = itemView.findViewById(R.id.txt2);
        }
    }
}
