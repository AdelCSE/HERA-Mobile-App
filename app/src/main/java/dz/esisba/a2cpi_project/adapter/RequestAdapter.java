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
import dz.esisba.a2cpi_project.interfaces.SearchOnItemClick;
import dz.esisba.a2cpi_project.models.RequestModel;

public class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<RequestModel> RequestsHolder;
    Context context;
    SearchOnItemClick rListner;

    public RequestAdapter(ArrayList<RequestModel> requestsHolder,SearchOnItemClick rlistner) {
        RequestsHolder = requestsHolder;
        rListner = rlistner;
    }

    @Override
    public int getItemViewType(int position) {
        if (RequestsHolder.get(position).getUsername()!=null){
            return 1;
        }else{
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==1){
            context = parent.getContext();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_request,parent,false);
            return new myviewholder(view,rListner);

        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_item_text,parent,false);
            return new myviewholder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==1){
            myviewholder myviewholder1 = (myviewholder) holder;

            Glide.with(context).load(RequestsHolder.get(position).getProfilePictureUrl()).into(myviewholder1.Img);
            myviewholder1.Username.setText("@"+RequestsHolder.get(position).getUsername());
            myviewholder1.Date.setText(RequestsHolder.get(position).ConvertDate());
            myviewholder1.Question.setText(RequestsHolder.get(position).getQuestion());
        }

        else{
            myviewholder2 myviewholder = (myviewholder2) holder;
            myviewholder.lastItem.setText("That's it");
            myviewholder.txt1.setText("Try to answer these requests to increase");
            myviewholder.txt2.setText("your reputation!");
        }
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

    static class myviewholder2 extends RecyclerView.ViewHolder{
        TextView lastItem,txt1,txt2;

        public myviewholder2(@NonNull View itemView) {
            super(itemView);
            lastItem=itemView.findViewById(R.id.lastItem);
            txt1=itemView.findViewById(R.id.txt);
            txt2=itemView.findViewById(R.id.txt2);
        }
    }
}
