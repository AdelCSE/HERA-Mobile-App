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
import dz.esisba.a2cpi_project.models.ReplyModel;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.myviewholder>{

    ArrayList<ReplyModel> RepliesHolder;
    Context context;

    public RepliesAdapter(ArrayList<ReplyModel> repliesHolder) {
        RepliesHolder = repliesHolder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_reply,parent,false);
        context = parent.getContext();
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Glide.with(context).load(RepliesHolder.get(position).getProfilePictureUrl()).into(holder.Img);
        holder.Username.setText("@"+RepliesHolder.get(position).getUsername());
        holder.Date.setText(RepliesHolder.get(position).ConvertDate());
        holder.Question.setText(RepliesHolder.get(position).getQuestion());
        holder.Answer.setText(RepliesHolder.get(position).getReply());
    }

    @Override
    public int getItemCount() {
        return RepliesHolder.size();
    }

    static class myviewholder extends RecyclerView.ViewHolder {

        CircleImageView Img;
        TextView Username,Date,Question,Answer;

        public myviewholder (@NonNull View itemView){
            super(itemView);
            Img = itemView.findViewById(R.id.replyImg);
            Username = itemView.findViewById(R.id.replyUsername);
            Date = itemView.findViewById(R.id.replyDate);
            Question = itemView.findViewById(R.id.replyQuestion);
            Answer = itemView.findViewById(R.id.reply);
        }
    }
}
