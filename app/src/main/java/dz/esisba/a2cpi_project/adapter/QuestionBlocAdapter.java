package dz.esisba.a2cpi_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.interfaces.OnItemClickListner;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.models.PostModel;

public class QuestionBlocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<PostModel> AllPostsDataHolder;
    private Context context;
    private OnItemClickListner aListner;
    static int i =0;

    public QuestionBlocAdapter(ArrayList<PostModel> postsDataHolder , OnItemClickListner listner) {
        AllPostsDataHolder = postsDataHolder;
        aListner = listner;
    }

    @Override
    public int getItemViewType(int position) {
        if (AllPostsDataHolder.get(position).getAnswersCount()!= -1 && AllPostsDataHolder.get(position).getLikesCount()!= -1){
            return 1;
        }else if (AllPostsDataHolder.get(position).getAnswersCount()!= -1 && AllPostsDataHolder.get(1).getLikesCount()==-1){
            return 2;
        }else{
            return 3;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1){
            context = parent.getContext();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
            return new ViewHolder1(view,aListner);
        }else if (viewType == 2) {
            context = parent.getContext();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_text, parent, false);
            return  new ViewHolder3(view);
        }else{
            context = parent.getContext();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_answer, parent, false);
            return  new ViewHolder2(view,aListner);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==1){
            ViewHolder1 viewHolder1 = (ViewHolder1) holder;
            Glide.with(context).load(AllPostsDataHolder.get(position).getPublisherPic()).into(viewHolder1.img);
            viewHolder1.Question.setText(AllPostsDataHolder.get(position).getQuestion());
            viewHolder1.Name.setText(AllPostsDataHolder.get(position).getAskedBy());
            viewHolder1.Username.setText("@"+AllPostsDataHolder.get(position).getUsername());
            viewHolder1.Details.setText(AllPostsDataHolder.get(position).getBody());
            viewHolder1.Likes.setText(Integer.toString(AllPostsDataHolder.get(position).getLikesCount()));
            viewHolder1.Answers.setText(Integer.toString(AllPostsDataHolder.get(position).getAnswersCount()));
            viewHolder1.Date.setText(AllPostsDataHolder.get(position).getDate());

        }else if (holder.getItemViewType()==2){
            ViewHolder3 viewHolder3 = (ViewHolder3) holder;

            if (AllPostsDataHolder.get(0).getAnswersCount()==0) {
                viewHolder3.customText.setText("Be the first who answers this question!");
            }else {
                viewHolder3.customText.setText(AllPostsDataHolder.get(0).getAnswersCount() + " answers");
            }
        }else{
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.Question.setText(AllPostsDataHolder.get(position).getQuestion());
            viewHolder2.Username.setText("@"+AllPostsDataHolder.get(position).getUsername());
            viewHolder2.Details.setText(AllPostsDataHolder.get(position).getBody());
            viewHolder2.Likes.setText(Integer.toString(AllPostsDataHolder.get(position).getLikesCount()));
            viewHolder2.Date.setText(AllPostsDataHolder.get(position).getDate());
        }
    }

    @Override
    public int getItemCount() {
        return AllPostsDataHolder.size();
    }


    public class ViewHolder1 extends RecyclerView.ViewHolder {

        //Question Type
        ImageButton answerBtn,shareBtn;
        ImageView img;
        TextView Question,Details,Name,Username,Likes,Answers,Date;
        public ViewHolder1(@NonNull View itemView , OnItemClickListner listner) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            Question = itemView.findViewById(R.id.question);
            Name = itemView.findViewById(R.id.name);
            Username = itemView.findViewById(R.id.usernamep);
            Details = itemView.findViewById(R.id.details);
            Likes = itemView.findViewById(R.id.likes);
            Answers = itemView.findViewById(R.id.answers);
            Date = itemView.findViewById(R.id.postDate);

            itemView.findViewById(R.id.answerBtn).setOnClickListener(new View.OnClickListener() {
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
            itemView.findViewById(R.id.questionShareBtn).setOnClickListener(new View.OnClickListener() {
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
            itemView.findViewById(R.id.questionMenuBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onMenuClick(position,view);
                        }
                    }
                }
            });
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        ImageButton answerShareBtn;
        TextView Question,Details,Username,Likes,Date;

        public ViewHolder2(@NonNull View itemView, OnItemClickListner listner) {
            super(itemView);
            Question = itemView.findViewById(R.id.questiona);
            Username = itemView.findViewById(R.id.usernamea);
            Details = itemView.findViewById(R.id.detailsa);
            Likes = itemView.findViewById(R.id.likesa);
            Date = itemView.findViewById(R.id.postDatea);

            itemView.findViewById(R.id.answerShareBtn).setOnClickListener(new View.OnClickListener() {
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
            itemView.findViewById(R.id.answerMenuBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onMenuClick(position,view);
                        }
                    }
                }
            });
        }
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder {
        TextView customText;

        public ViewHolder3(@NonNull View itemView) {
            super(itemView);
            customText = itemView.findViewById(R.id.customText);
            customText.setTextSize(16f);
        }
    }
}
