package dz.esisba.a2cpi_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.interfaces.AnswersOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.myviewholder> {

    ArrayList<PostModel> AnswerHolder;
    AnswersOnItemClickListner aListner;
    private Context context;

    public AnswersAdapter(ArrayList<PostModel> answersHolder) {
        AnswerHolder = answersHolder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profile_answer,parent,false);
        context = parent.getContext();
        return new myviewholder(view, aListner);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Glide.with(context).load(AnswerHolder.get(position).getPublisherPic()).into(holder.img);
        holder.Question.setText(AnswerHolder.get(position).getQuestion());
        holder.Username.setText(AnswerHolder.get(position).getUsername());
        holder.Details.setText(AnswerHolder.get(position).getBody());
        holder.Likes.setText(Integer.toString(AnswerHolder.get(position).getLikesCount())+" Likes");
        holder.Date.setText(AnswerHolder.get(position).ConvertDate());
    }

    @Override
    public int getItemCount() {
        return AnswerHolder.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder {

        TextView Question,Details,Username,Likes,Date;
        CircleImageView img;

        public myviewholder(@NonNull View itemView  , AnswersOnItemClickListner listner) {
            super(itemView);
            img = itemView.findViewById(R.id.imgpa);
            Question = itemView.findViewById(R.id.questionpa);
            Username = itemView.findViewById(R.id.usernamepa);
            Details = itemView.findViewById(R.id.detailspa);
            Likes = itemView.findViewById(R.id.likespa);
            Date = itemView.findViewById(R.id.postDatepa);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onShareClick(position);
                        }
                    }
                }
            });

            itemView.findViewById(R.id.answerMenuBtnpa).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
