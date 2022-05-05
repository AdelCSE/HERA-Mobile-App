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
import dz.esisba.a2cpi_project.models.UserSearchModel;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.UsersViewHolder> {

    Context context;
    ArrayList<UserSearchModel> userSearchModelArrayList;
    private final OnItemClickListener mOnItemClickListener;

    public SearchAdapter(Context context, ArrayList<UserSearchModel> userSearchModelArrayList, OnItemClickListener mOnItemClickListener) {
        this.context = context;
        this.userSearchModelArrayList = userSearchModelArrayList;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cardview_search_user,parent,false);
        return new UsersViewHolder(v,mOnItemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        UserSearchModel user = userSearchModelArrayList.get(position);

        holder.Name.setText(user.getName());
        holder.Username.setText("@"+user.getUsername());
        Glide.with(this.context).load(user.getProfilePictureUrl()).into(holder.profilePictureUrl);

    }

    @Override
    public int getItemCount() {
        return userSearchModelArrayList.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Name;
        TextView Username;
        ImageView profilePictureUrl;
        OnItemClickListener onItemClickListener;

        public UsersViewHolder(View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            Name = itemView.findViewById(R.id.usertext);
            Username =  itemView.findViewById(R.id.usernametext);
            profilePictureUrl =  itemView.findViewById(R.id.userimage);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getBindingAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);

    }
}

