package com.omkarp.alumniportal.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omkarp.alumniportal.ChatActivity;
import com.omkarp.alumniportal.R;
import com.omkarp.alumniportal.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    private Context context;
    private List<ModelUser> userList;

    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String userProfilePic = userList.get(position).getProfilePicture();
        final String userName = userList.get(position).getName();
        final String userUID = userList.get(position).getUid();
        String userEmail = userList.get(position).getEmail();

        holder.nameTV.setText(userName);
        holder.emailTV.setText(userEmail);
        try {
            Picasso.get().load(userProfilePic)
                    .placeholder(R.drawable.ic_user_default)
                    .into(holder.avatarIV);
        } catch (Exception e) {
            //nothing
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userUID", userUID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView nameTV, emailTV;
        ImageView avatarIV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.accountNameTV);
            emailTV = itemView.findViewById(R.id.accountEmailTV);
            avatarIV = itemView.findViewById(R.id.profileIV);
        }
    }

}
