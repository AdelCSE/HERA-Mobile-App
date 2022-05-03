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

public class AllPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<PostModel> AllPostsDataHolder;

    public AllPostsAdapter(ArrayList<PostModel> postsDataHolder) {
        AllPostsDataHolder = postsDataHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (AllPostsDataHolder.get(position).getAnswers()!=null){
            return 1;
        }else{
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
            return new ViewHolder1(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_answer, parent, false);
            return  new ViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==1){
            ViewHolder1 viewHolder1 = (ViewHolder1) holder;
            viewHolder1.img.setImageResource(AllPostsDataHolder.get(position).getImage());
            viewHolder1.Question.setText(AllPostsDataHolder.get(position).getQuestion());
            viewHolder1.Name.setText(AllPostsDataHolder.get(position).getName());
            viewHolder1.Username.setText(AllPostsDataHolder.get(position).getUsername());
            viewHolder1.Details.setText(AllPostsDataHolder.get(position).getDetails());
            viewHolder1.Likes.setText(AllPostsDataHolder.get(position).getLikes());
            viewHolder1.Answers.setText(AllPostsDataHolder.get(position).getAnswers());
            viewHolder1.Date.setText(AllPostsDataHolder.get(position).getDate());
        }else{
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.Question.setText(AllPostsDataHolder.get(position).getQuestion());
            viewHolder2.Username.setText(AllPostsDataHolder.get(position).getUsername());
            viewHolder2.Details.setText(AllPostsDataHolder.get(position).getDetails());
            viewHolder2.Likes.setText(AllPostsDataHolder.get(position).getLikes());
            viewHolder2.Date.setText(AllPostsDataHolder.get(position).getDate());
        }
    }

    @Override
    public int getItemCount() {
        return AllPostsDataHolder.size();
    }


    public class ViewHolder1 extends RecyclerView.ViewHolder {
        //Question Type
        ImageView img;
        TextView Question,Details,Name,Username,Likes,Answers,Date;
        public ViewHolder1(@NonNull View itemView) {
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

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        TextView Question,Details,Username,Likes,Date;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            Question = itemView.findViewById(R.id.questiona);
            Username = itemView.findViewById(R.id.usernamea);
            Details = itemView.findViewById(R.id.detailsa);
            Likes = itemView.findViewById(R.id.likesa);
            Date = itemView.findViewById(R.id.postDatea);
        }
    }
}
