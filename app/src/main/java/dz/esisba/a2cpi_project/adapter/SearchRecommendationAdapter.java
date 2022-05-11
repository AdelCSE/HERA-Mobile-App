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
import dz.esisba.a2cpi_project.interfaces.PostsOnItemClickListner;
import dz.esisba.a2cpi_project.interfaces.QuestionsOnItemClickListner;
import dz.esisba.a2cpi_project.models.PostModel;

public class SearchRecommendationAdapter extends RecyclerView.Adapter<SearchRecommendationAdapter.ViewHolder1> {
    private ArrayList<PostModel> QuestionsHolder;
    private Context context;
    private QuestionsOnItemClickListner mListner;

    public SearchRecommendationAdapter(ArrayList<PostModel> questionsHolder, QuestionsOnItemClickListner mListner) {
        QuestionsHolder = questionsHolder;
        this.mListner = mListner;
    }

    @NonNull
    @Override
    public SearchRecommendationAdapter.ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_question,parent,false);
        return new SearchRecommendationAdapter.ViewHolder1(view , mListner);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecommendationAdapter.ViewHolder1 holder, int position) {
        Glide.with(context).load(QuestionsHolder.get(position).getPublisherPic()).into(holder.img);
        holder.Question.setText(QuestionsHolder.get(position).getQuestion());
        holder.Username.setText("@"+QuestionsHolder.get(position).getUsername());
        holder.Date.setText(QuestionsHolder.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return QuestionsHolder.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        ImageView AnswerBtn;
        ImageView img;
        TextView Question,Username,Date;
        public ViewHolder1(@NonNull View itemView,QuestionsOnItemClickListner listner) {
            super(itemView);
            img = itemView.findViewById(R.id.imgRQ);
            Question = itemView.findViewById(R.id.questionRQ);
            Username = itemView.findViewById(R.id.usernameRQ);
            Date = itemView.findViewById(R.id.dateRQ);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onAnswerClick(position);
                        }
                    }
                }
            });
        }
    }
}
