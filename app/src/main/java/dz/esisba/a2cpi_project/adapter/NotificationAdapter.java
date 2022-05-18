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
import dz.esisba.a2cpi_project.models.NotificationModel;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<NotificationModel> NotificationsHolder;
    Context context;

    public NotificationAdapter(ArrayList<NotificationModel> notificationsHolder) {
        NotificationsHolder = notificationsHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (NotificationsHolder.get(position).getUsername()!=null){
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_notifications,parent,false);
            return new Myviewholder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_text,parent,false);
            return new Myviewholder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==1){
            Myviewholder myviewholder = (Myviewholder) holder;
            Glide.with(context).load(NotificationsHolder.get(position).getImage()).into(myviewholder.Img);
            myviewholder.Username.setText(NotificationsHolder.get(position).getUsername());
            myviewholder.Date.setText(NotificationsHolder.get(position).ConvertDate());
            switch (NotificationsHolder.get(position).getType()) {
                case 0:
                    myviewholder.NotificationText.setText("followed you");
                    break;
                case 1:
                    myviewholder.NotificationText.setText("liked your question");
                    break;
                case 2:
                    myviewholder.NotificationText.setText("liked your answer");
                    break;
            }
        }else{
            Myviewholder2 myviewholder2 = (Myviewholder2) holder;
            myviewholder2.New.setText("New");

        }

    }

    @Override
    public int getItemCount() {
        return NotificationsHolder.size();
    }

    class Myviewholder extends RecyclerView.ViewHolder {

        ImageView Img;
        TextView Username,Date,NotificationText;

        public Myviewholder (@NonNull View itemView){
            super(itemView);
            Img = itemView.findViewById(R.id.notifImg);
            Username = itemView.findViewById(R.id.notifUsername);
            Date = itemView.findViewById(R.id.notifDate);
            NotificationText = itemView.findViewById(R.id.notifText);
        }
    }

    class Myviewholder2 extends RecyclerView.ViewHolder {
        TextView New;

        public Myviewholder2(@NonNull View itemView) {
            super(itemView);
            New = itemView.findViewById(R.id.customText);
        }
    }
}
