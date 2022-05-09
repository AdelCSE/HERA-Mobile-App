package dz.esisba.a2cpi_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.models.PostModel;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.myviewholder> {

    ArrayList<PostModel> AnswerHolder;

    public AnswersAdapter(ArrayList<PostModel> answersHolder) {
        AnswerHolder = answersHolder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profile_answer,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.Question.setText(AnswerHolder.get(position).getQuestion());
        holder.Username.setText(AnswerHolder.get(position).getUsername());
        holder.Details.setText(AnswerHolder.get(position).getBody());
        holder.Likes.setText(Integer.toString(AnswerHolder.get(position).getLikesCount()));
        holder.Date.setText(AnswerHolder.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return AnswerHolder.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder {

        TextView Question,Details,Username,Likes,Date;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            Question = itemView.findViewById(R.id.questionpa);
            Username = itemView.findViewById(R.id.usernamea);
            Details = itemView.findViewById(R.id.detailsa);
            Likes = itemView.findViewById(R.id.likesa);
            Date = itemView.findViewById(R.id.postDatea);
        }
    }
}
