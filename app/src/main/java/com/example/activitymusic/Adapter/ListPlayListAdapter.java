package com.example.activitymusic.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Fragment.ListPlayListFragment;
import com.example.activitymusic.Fragment.ListSongPlayListFragment;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPlayListAdapter extends RecyclerView.Adapter<ListPlayListAdapter.ListPlayListViewHolder> {
    private Context mContext;
    private String mStatus;
    private onShowPlayList onShowPlayList;
    private ArrayList<PlayList> mPlayListCurrent = new ArrayList<>();

    String TAG = "TienNVh";

    public ListPlayListAdapter(Context mContext, ArrayList<PlayList> mPlayList, String mStatus) {
        this.mContext = mContext;
        this.mStatus = mStatus;


        mPlayListCurrent.add(mPlayList.get(0));
        for (int i = 0; i < mPlayList.size(); i++) {
            int count = 0;
            for (int j = 0; j < mPlayListCurrent.size(); j++) {
                if (mPlayListCurrent.get(j).getNAMEPLAYLIST().equals(mPlayList.get(i).getNAMEPLAYLIST()))
                    count++;
            }
            if (count == 0) {
                mPlayListCurrent.add(mPlayList.get(i));

            } else {

            }
        }
    }

    void onCountSongPlayList(@NonNull final ListPlayListViewHolder holder, String namPlayList) {
        DataServer dataServer = APIServer.getServer();
        Call<Integer> callback = dataServer.CountSongPlayList(namPlayList);
        callback.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                holder.mAmountPlayList.setText(response.body() + " bài hát");
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(mContext, "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ListPlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListPlayListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.items_list_playlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListPlayListViewHolder holder, int position) {
        final PlayList playList = mPlayListCurrent.get(position);
        //holder.mAmountPlayList.setText(mAmount[position] + " bài hát");
        onCountSongPlayList(holder, playList.getNAMEPLAYLIST());
        holder.mNamePlayList.setText(playList.getNAMEPLAYLIST());
        Glide.with(mContext).load(playList.getIMAGE()).into(holder.mImagePlayList);
        if (!mStatus.equals("view"))
            holder.mMore.setVisibility(View.GONE);
        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStatus.equals("view"))
                    onShowPlayList.onShow(playList);
                else {
                    onShowPlayList.onAddSongPlayList(playList);
                }
            }
        });

        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.mMore);
                popupMenu.inflate(R.menu.more_playlist);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.rename_playlist:
                                onShowPlayList.onRenamePlaylist(playList);
                                return true;
                            case R.id.add_song:
                                onShowPlayList.onAddSongPlayList(playList);
                                return true;
                            case R.id.delete_playlist:
                                onShowPlayList.onDeletePlaylist(playList);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayListCurrent.size();
    }


    public class ListPlayListViewHolder extends RecyclerView.ViewHolder {
        TextView mNamePlayList, mAmountPlayList;
        ImageView mImagePlayList, mMore;
        ConstraintLayout mConstraintLayout;

        public ListPlayListViewHolder(@NonNull View itemView) {
            super(itemView);
            mNamePlayList = itemView.findViewById(R.id.name_playlist);
            mImagePlayList = itemView.findViewById(R.id.img_playlis);
            mAmountPlayList = itemView.findViewById(R.id.Amountsong);
            mMore = itemView.findViewById(R.id.more_playlist);
            mConstraintLayout = itemView.findViewById(R.id.ConstraintLayoutItemsPlayList);
        }
    }

    public void setOnShowPlayList(ListPlayListAdapter.onShowPlayList onShowPlayList) {
        this.onShowPlayList = onShowPlayList;
    }

    public interface onShowPlayList {
        void onShow(PlayList current);
        void onAddSongPlayList(PlayList current);
        void onRenamePlaylist(PlayList current);
        void onDeletePlaylist(PlayList current);

    }
}
