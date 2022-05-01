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
import dz.esisba.a2cpi_project.models.RequestModel;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.myviewholder>{

    ArrayList<RequestModel> RequestsHolder;

    public RequestAdapter(ArrayList<RequestModel> requestsHolder) {
        RequestsHolder = requestsHolder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_request,parent,false);
        return new RequestAdapter.myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.Img.setImageResource(RequestsHolder.get(position).getImage());
        holder.Username.setText(RequestsHolder.get(position).getUsername());
        holder.Date.setText(RequestsHolder.get(position).getDate());
        holder.Question.setText(RequestsHolder.get(position).getQuestion());
    }

    @Override
    public int getItemCount() {
        return RequestsHolder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        ImageView Img;
        TextView Username,Date,Question;

        public myviewholder (@NonNull View itemView){
            super(itemView);

            Img = itemView.findViewById(R.id.imgR);
            Username = itemView.findViewById(R.id.usernameR);
            Date = itemView.findViewById(R.id.dateR);
            Question = itemView.findViewById(R.id.questionR);

        }
    }
}
