package dz.esisba.a2cpi_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.models.PostModel;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myviewholder> {

    ArrayList<PostModel> PostsHolder;

    public PostAdapter(ArrayList<PostModel> postsHolder) {
        PostsHolder = postsHolder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.img.setImageResource(PostsHolder.get(position).getImage());
        holder.Question.setText(PostsHolder.get(position).getQuestion());
        holder.Name.setText(PostsHolder.get(position).getName());
        holder.Username.setText(PostsHolder.get(position).getUsername());
        holder.Details.setText(PostsHolder.get(position).getDetails());
        holder.Likes.setText(PostsHolder.get(position).getLikes());
        holder.Answers.setText(PostsHolder.get(position).getAnswers());
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
