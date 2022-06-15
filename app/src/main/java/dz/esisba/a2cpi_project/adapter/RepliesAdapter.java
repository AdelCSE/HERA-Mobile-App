package dz.esisba.a2cpi_project.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.models.ReplyModel;

public class RepliesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<ReplyModel> RepliesHolder;
    Context context;

    public RepliesAdapter(ArrayList<ReplyModel> repliesHolder) {
        RepliesHolder = repliesHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(RepliesHolder.get(position).getUsername()!=null){
            return 1;
        }else{
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_reply,parent,false);
            context = parent.getContext();
            return new Myviewholder(view, RepliesHolder, this);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_item_text,parent,false);
            context = parent.getContext();
            return new Myviewholder2(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==1){
            Myviewholder myviewholder1 = (Myviewholder) holder;
            Glide.with(context).load(RepliesHolder.get(position).getProfilePictureUrl()).into(myviewholder1.Img);
            myviewholder1.Username.setText("@"+RepliesHolder.get(position).getUsername());
            myviewholder1.Date.setText(RepliesHolder.get(position).ConvertDate());
            myviewholder1.Question.setText(RepliesHolder.get(position).getQuestion());
            myviewholder1.Answer.setText(RepliesHolder.get(position).getReply());
            RunCheckForLikes(RepliesHolder.get(position), myviewholder1.star);
        }else{
            Myviewholder2 myviewholder2 = (Myviewholder2) holder;
            myviewholder2.lastItem.setText("That's it");
            myviewholder2.txt1.setText("If you liked someone's answers, don't forget to");
            myviewholder2.txt2.setText("give them a star!");
        }

    }

    private void RunCheckForLikes(ReplyModel post, ImageView star) {
        boolean likes = post.isLiked();

        if (post.isLiked())
        {
            star.setImageResource(R.drawable.ic_star__2_);
            star.setTag("Liked");
        }
        else
        {
            star.setImageResource(R.drawable.ic_star__3_);
            star.setTag("Like");
        }
    }



    @Override
    public int getItemCount() {
        return RepliesHolder.size();
    }

    static class Myviewholder extends RecyclerView.ViewHolder {

        CircleImageView Img;
        ImageView star, delete;
        TextView Username,Date,Question,Answer;

        private FirebaseAuth auth;
        private FirebaseUser user;
        private FirebaseFirestore fstore;

        public Myviewholder(@NonNull View itemView, ArrayList<ReplyModel> replies, RepliesAdapter repliesAdapter){
            super(itemView);
            Img = itemView.findViewById(R.id.replyImg);
            Username = itemView.findViewById(R.id.replyUsername);
            Date = itemView.findViewById(R.id.replyDate);
            Question = itemView.findViewById(R.id.replyQuestion);
            Answer = itemView.findViewById(R.id.reply);
            star = itemView.findViewById(R.id.replyStarBtn);
            delete = itemView.findViewById(R.id.deleteReplyBtn);

            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            user = auth.getCurrentUser();

            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentReference userRef = fstore.collection("Users").document(user.getUid());
                    int position = getAbsoluteAdapterPosition();
                    if (star.getTag().equals("Like"))
                    {
                        star.setTag("Liked");
                        star.setImageResource(R.drawable.ic_star__2_);
                        fstore.collection("Users").document(replies.get(position).getUid()).update("reputation", FieldValue.increment(1));
                        userRef.collection("Replies").document(replies.get(position).getReplyId()).update("liked", true);
                    }
                    else
                    {
                        star.setImageResource(R.drawable.ic_star__3_);
                        star.setTag("Like");
                        fstore.collection("Users").document(replies.get(position).getUid()).update("reputation", FieldValue.increment(-1));
                        userRef.collection("Replies").document(replies.get(position).getReplyId()).update("liked", false);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentReference userRef = fstore.collection("Users").document(user.getUid());
                    int position = getAbsoluteAdapterPosition();
                    userRef.collection("Replies").document(replies.get(position).getReplyId()).delete();
                    replies.remove(position);
                    repliesAdapter.notifyItemRemoved(position);
                }
            });

        }
    }

    static class Myviewholder2 extends RecyclerView.ViewHolder{
        TextView lastItem,txt1,txt2;

        public Myviewholder2(@NonNull View itemView) {
            super(itemView);
            lastItem=itemView.findViewById(R.id.lastItem);
            txt1=itemView.findViewById(R.id.txt);
            txt2=itemView.findViewById(R.id.txt2);
        }
    }
}
