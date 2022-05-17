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
import dz.esisba.a2cpi_project.interfaces.SearchOnItemClick;
import dz.esisba.a2cpi_project.models.PostModel;

public class SearchRecommendationAdapter extends RecyclerView.Adapter<SearchRecommendationAdapter.ViewHolder1> {
    private ArrayList<PostModel> QuestionsHolder;
    private Context context;
    private SearchOnItemClick mListner;

    public SearchRecommendationAdapter(ArrayList<PostModel> questionsHolder, SearchOnItemClick mListner) {
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
        holder.Date.setText(QuestionsHolder.get(position).ConvertDate());
        holder.AnswersCount.setText(Integer.toString(QuestionsHolder.get(position).getAnswersCount()));
    }

    @Override
    public int getItemCount() {
        return QuestionsHolder.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        ImageView AnswerBtn;
        ImageView img;
        TextView Question,Username,Date,AnswersCount;
        public ViewHolder1(@NonNull View itemView,SearchOnItemClick listner) {
            super(itemView);
            img = itemView.findViewById(R.id.imgRQ);
            Question = itemView.findViewById(R.id.questionRQ);
            Username = itemView.findViewById(R.id.usernameRQ);
            Date = itemView.findViewById(R.id.dateRQ);
            AnswersCount = itemView.findViewById(R.id.TagSearchAnswers);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onItemClick(position);
                        }
                    }
                }
            });
            itemView.findViewById(R.id.answerBtnRQ).setOnClickListener(new View.OnClickListener() {
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
