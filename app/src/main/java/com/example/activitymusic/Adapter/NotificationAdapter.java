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

import com.bumptech.glide.Glide;
import com.example.activitymusic.Model.Notification;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context mContext;
    private List<Notification> mListNotification;
    private  onRefreshLayout onRefreshLayout;
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
    public void onBindViewHolder(@NonNull final NotificationViewHolder holder, int position) {
        final Notification notification=mListNotification.get(position);
        holder.mContent.setText(notification.getCONTENT());
        holder.mTitle.setText(notification.getTITLE());
        holder.mDate.setText(notification.getDATE());
        DataServer dataServer = APIServer.getServer();
        Call<List<SongOnline>> callback = dataServer.getDataSongOnline();
        callback.enqueue(new Callback<List<SongOnline>>() {
            @Override
            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
                ArrayList<SongOnline> songOnlineList = (ArrayList<SongOnline>) response.body();
                for(int i=0;i <songOnlineList.size();i++){
                    if(Integer.parseInt(notification.getIDSONG())==Integer.parseInt(songOnlineList.get(i).getID())){
                        Glide.with(mContext).load(songOnlineList.get(i).getIMAGE()).into(holder.mImageView);
                        onRefreshLayout.onRefresh();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SongOnline>> call, Throwable t) {

            }
        });

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

    public void setOnRefreshLayout(NotificationAdapter.onRefreshLayout onRefreshLayout) {
        this.onRefreshLayout = onRefreshLayout;
    }

    public  interface  onRefreshLayout{
        void onRefresh();

    }

}
