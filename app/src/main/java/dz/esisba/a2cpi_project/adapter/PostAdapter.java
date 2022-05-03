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

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.models.PostModel;
import dz.esisba.a2cpi_project.navigation_fragments.ProfileFragment;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myviewholder> {

    ArrayList<PostModel> PostsHolder;
    private Context context;

    public PostAdapter(ArrayList<PostModel> postsHolder) {
        PostsHolder = postsHolder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post,parent,false);
        return new myviewholder(view);
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
        holder.Date.setText(PostsHolder.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return PostsHolder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView Question,Details,Name,Username,Likes,Answers,Date;

        public myviewholder (@NonNull View itemView){
            super(itemView);
            img = itemView.findViewById(R.id.img);
            Question = itemView.findViewById(R.id.question);
            Name = itemView.findViewById(R.id.name);
            Username = itemView.findViewById(R.id.usernamep);
            Details = itemView.findViewById(R.id.details);
            Likes = itemView.findViewById(R.id.likes);
            Answers = itemView.findViewById(R.id.answers);
            Date = itemView.findViewById(R.id.postDate);

        }
    }
}
