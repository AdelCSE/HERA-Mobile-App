package dz.esisba.a2cpi_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.interfaces.SearchOnItemClick;
import dz.esisba.a2cpi_project.models.RequestModel;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.myviewholder>{

    ArrayList<RequestModel> RequestsHolder;
    Context context;
    SearchOnItemClick rListner;

    public RequestAdapter(ArrayList<RequestModel> requestsHolder,SearchOnItemClick rlistner) {
        RequestsHolder = requestsHolder;
        rListner = rlistner;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_request,parent,false);
        context = parent.getContext();
        return new myviewholder(view,rListner);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Glide.with(context).load(RequestsHolder.get(position).getProfilePictureUrl()).into(holder.Img);
        holder.Username.setText("@"+RequestsHolder.get(position).getUsername());
        holder.Date.setText(RequestsHolder.get(position).ConvertDate());
        holder.Question.setText(RequestsHolder.get(position).getQuestion());
    }

    @Override
    public int getItemCount() {
        return RequestsHolder.size();
    }

    static class myviewholder extends RecyclerView.ViewHolder {

        CircleImageView Img;
        TextView Username,Date,Question;

        public myviewholder (@NonNull View itemView , SearchOnItemClick listner){
            super(itemView);
            Img = itemView.findViewById(R.id.imgRequest);
            Username = itemView.findViewById(R.id.usernameR);
            Date = itemView.findViewById(R.id.dateR);
            Question = itemView.findViewById(R.id.questionR);

            itemView.findViewById(R.id.answerR).setOnClickListener(new View.OnClickListener() {
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

            itemView.findViewById(R.id.dismissR).setOnClickListener(new View.OnClickListener() {
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

        }
    }
}
