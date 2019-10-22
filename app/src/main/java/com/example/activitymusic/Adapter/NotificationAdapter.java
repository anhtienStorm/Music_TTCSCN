package com.example.activitymusic.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Model.Notification;
import com.example.activitymusic.R;

import java.util.List;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context mContext;
    private List<Notification> mListNotification;

    public NotificationAdapter(List<Notification> listNotification,Context context) {
        mListNotification=listNotification;
        Log.d("notification", "onBindViewHolder: "+mListNotification.size());
        mContext=context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(mContext).inflate(R.layout.items_notifcation,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification=mListNotification.get(position);
        holder.mContent.setText(notification.getCONTENT());
        holder.mTitle.setText(notification.getTITLE());
        holder.mDate.setText(notification.getDATE());

        Log.d("notification", "onBindViewHolder: "+notification.getDATE());
    }

    @Override
    public int getItemCount() {
        return mListNotification.size();
    }


    class NotificationViewHolder extends RecyclerView.ViewHolder {
         ImageView mImageView;
         TextView mTitle, mContent, mDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
             mImageView=itemView.findViewById(R.id.imageNotification);
             mTitle =itemView.findViewById(R.id.titleNotification);
             mContent=itemView.findViewById(R.id.contentNotification);
             mDate= itemView.findViewById(R.id.dateNotification);
        }
    }
}
