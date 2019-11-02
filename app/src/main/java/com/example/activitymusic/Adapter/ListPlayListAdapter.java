package com.example.activitymusic.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Fragment.ListPlayListFragment;
import com.example.activitymusic.Fragment.ListSongPlayListFragment;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.R;

import java.util.ArrayList;

public class ListPlayListAdapter extends RecyclerView.Adapter<ListPlayListAdapter.ListPlayListViewHolder> {
    private Context mContext;
    private String mStatus;
    private onShowPlayList onShowPlayList;
    private ArrayList<PlayList> mPlayListCurrent = new ArrayList<>();
    int mAmount[];
    String TAG = "TienNVh";

    public ListPlayListAdapter(Context mContext, ArrayList<PlayList> mPlayList, String mStatus) {
        this.mContext = mContext;
        this.mStatus = mStatus;

        mAmount = new int[1000];// cho phép tối đa 1000 playlist
        mPlayListCurrent.add(mPlayList.get(0));
        for (int i = 0; i < mPlayList.size(); i++) {
            int count = 0;
            for (int j = 0; j < mPlayListCurrent.size(); j++) {
                if (mPlayListCurrent.get(j).getNAMEPLAYLIST().equals(mPlayList.get(i).getNAMEPLAYLIST()))
                    count++;
            }
            if (count == 0) {
                mPlayListCurrent.add(mPlayList.get(i));
                mAmount[mPlayListCurrent.size() - 1] = 1;
            } else {
                mAmount[mPlayListCurrent.size() - 1] = count + 1;
            }
        }
        Log.d(TAG, "ListPlayListAdapter: " + mAmount[0]);

    }

    @NonNull
    @Override
    public ListPlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListPlayListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.items_list_playlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListPlayListViewHolder holder, int position) {
        final PlayList playList = mPlayListCurrent.get(position);
        holder.mAmountPlayList.setText(mAmount[position] + " bài hát");
        holder.mNamePlayList.setText(playList.getNAMEPLAYLIST());
        Glide.with(mContext).load(playList.getIMAGE()).into(holder.mImagePlayList);
        if(!mStatus.equals("view"))
            holder.mMore.setVisibility(View.GONE);
        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowPlayList.onShow(playList);
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
    }
}
