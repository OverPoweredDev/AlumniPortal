package com.omkarp.alumniportal.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omkarp.alumniportal.R;
import com.omkarp.alumniportal.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    private Context context;
    private List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }


    @NonNull
    @Override
    public AdapterPosts.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPosts.MyHolder holder, int position) {
        //get data
        String username = postList.get(position).getUsername();
        String timeStamp = postList.get(position).getTimeStamp();
        String title = postList.get(position).getTitle();
        String description = postList.get(position).getDescription();
        String postImage = postList.get(position).getPostImage();
        String postProfilePic = postList.get(position).getUserProfilePicture();
        int voteCount = postList.get(position).getVoteCount();

        //convert time to dd/mm/yyyy hh:mm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.postUsernameTV.setText(username);
        holder.postTimeTV.setText(dateTime);
        holder.postTitleTV.setText(title);
        holder.postDescTV.setText(description);
        holder.voteCountTV.setText(Integer.toString(voteCount));
        try {
            Picasso.get().load(postProfilePic)
                    .placeholder(R.drawable.ic_user_default)
                    .into(holder.postProfileIV);
            Picasso.get().load(postImage)
                    .into(holder.postIV);
        } catch (Exception e) {
            //nothing
        }

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView postUsernameTV, postTimeTV, postTitleTV, postDescTV, voteCountTV;
        ImageView postIV, postProfileIV;
        ImageButton upvoteBTN, downvoteBTN;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            postUsernameTV = itemView.findViewById(R.id.postUsernameTV);
            postTimeTV = itemView.findViewById(R.id.postTimeTV);
            postTitleTV = itemView.findViewById(R.id.postTitleTV);
            postDescTV = itemView.findViewById(R.id.postDescTV);
            voteCountTV = itemView.findViewById(R.id.voteCountTV);
            postIV = itemView.findViewById(R.id.postIV);
            postProfileIV = itemView.findViewById(R.id.postProfileIV);
            upvoteBTN = itemView.findViewById(R.id.upvoteBTN);
            downvoteBTN = itemView.findViewById(R.id.downvoteBTN);
        }
    }
}
