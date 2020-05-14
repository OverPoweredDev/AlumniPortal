package com.omkarp.alumniportal.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omkarp.alumniportal.R;
import com.omkarp.alumniportal.models.ModelChat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static int MSG_TYPE_LEFT = 100;
    private static int MSG_TYPE_RIGHT = 200;
    private Context context;
    private List<ModelChat> chatList;

    private FirebaseUser user;

    public AdapterChat(Context context, List<ModelChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimeStamp();

        //convert time to dd/mm/yyyy hh:mm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy", calendar).toString();

        //set data
        holder.messageTV.setText(message);
        holder.timeStampTV.setText(dateTime);
        if (position == chatList.size() - 1) {
            if (chatList.get(position).isSeen()) {
                holder.isSeenTV.setText("");
            }
        } else {
            holder.isSeenTV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView messageTV, timeStampTV, isSeenTV;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            messageTV = itemView.findViewById(R.id.messageTV);
            timeStampTV = itemView.findViewById(R.id.timeTV);
            isSeenTV = itemView.findViewById(R.id.isSeenTV);
        }
    }
}
