package dz.esisba.a2cpi_project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.interfaces.NotificationOnItemListener;
import dz.esisba.a2cpi_project.models.NotificationModel;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<NotificationModel> NotificationsHolder;
    Context context;
    private final NotificationOnItemListener listener;


    public NotificationAdapter(ArrayList<NotificationModel> notificationsHolder , NotificationOnItemListener l) {
        NotificationsHolder = notificationsHolder;
        listener=l;
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==1){
            Myviewholder myviewholder = (Myviewholder) holder;
            Glide.with(context).load(NotificationsHolder.get(position).getImage()).into(myviewholder.Img);
            myviewholder.Username.setText(NotificationsHolder.get(position).getUsername());
            myviewholder.Date.setText(NotificationsHolder.get(position).ConvertDate());
            if(NotificationsHolder.get(position).isSeen()){
            myviewholder.unseenIcon.setVisibility(View.GONE);
            myviewholder.background.setBackgroundColor(android.R.color.transparent);
            }

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

                case 3:
                    myviewholder.NotificationText.setText("Answered Your Question!");
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

    class Myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener{


        ImageView Img ,removeNotif;
        TextView Username,Date,NotificationText;
        CardView unseenIcon;
        RelativeLayout background;


        public Myviewholder (@NonNull View itemView){
            super(itemView);
            Img = itemView.findViewById(R.id.notifImg);
            Username = itemView.findViewById(R.id.notifUsername);
            Date = itemView.findViewById(R.id.notifDate);
            NotificationText = itemView.findViewById(R.id.notifText);
            removeNotif = itemView.findViewById(R.id.removeNotif);
            unseenIcon= itemView.findViewById(R.id.notification_icon);
            background=itemView.findViewById(R.id.notifbackground);

            removeNotif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRemoveClick(view ,getAdapterPosition());
                }
            });
            itemView.setOnClickListener(view -> listener.onItemClick(view , getAdapterPosition()));
        }

        @Override
        public void onClick(View view) {

        }
    }

    static class Myviewholder2 extends RecyclerView.ViewHolder {
        TextView New;

        public Myviewholder2(@NonNull View itemView) {
            super(itemView);
            New = itemView.findViewById(R.id.customText);
        }
    }
}
