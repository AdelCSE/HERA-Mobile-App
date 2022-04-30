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
import dz.esisba.a2cpi_project.models.NotificationModel;
import dz.esisba.a2cpi_project.models.PostModel;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.myviewholder>{

    ArrayList<NotificationModel> NotificationsHolder;

    public NotificationAdapter(ArrayList<NotificationModel> notificationsHolder) {
        NotificationsHolder = notificationsHolder;
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_notifications,parent,false);
        return new NotificationAdapter.myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.img.setImageResource(NotificationsHolder.get(position).getImage());
        holder.Username.setText(NotificationsHolder.get(position).getUsername());
        holder.Date.setText(NotificationsHolder.get(position).getDate());
        holder.NotificationText.setText(NotificationsHolder.get(position).getNotificationText());

    }

    @Override
    public int getItemCount() {
        return NotificationsHolder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView Username,Date,NotificationText;

        public myviewholder (@NonNull View itemView){
            super(itemView);
            img = itemView.findViewById(R.id.notifImg);
            Username = itemView.findViewById(R.id.notifUsername);
            Date = itemView.findViewById(R.id.notifDate);
            NotificationText = itemView.findViewById(R.id.notifText);
        }
    }
}
