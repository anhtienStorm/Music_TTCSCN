package com.example.activitymusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;

import java.util.ArrayList;

public class SearchOnlineAdapter extends RecyclerView.Adapter<SearchOnlineAdapter.SearchOnlineViewHolder> {

    Context mContext;
    public ArrayList<SongOnline> mSearchOnlineList;
    ISearchOnlineAdapterClickListener listenner;

    public SearchOnlineAdapter(Context context) {
        mContext = context;
    }

    public void createSearchOnlineList() {
        mSearchOnlineList = new ArrayList<>();
    }

    public void updateSearchOnlineList(ArrayList<SongOnline> list) {
        mSearchOnlineList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchOnlineViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recyclerview_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchOnlineViewHolder holder, int position) {
        holder.bind(mSearchOnlineList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mSearchOnlineList == null) {
            return 0;
        }
        return mSearchOnlineList.size();
    }

    public void setOnClickListenner(ISearchOnlineAdapterClickListener listenner) {
        this.listenner = listenner;
    }

    // class
    class SearchOnlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitleSong;
        private TextView tvArtist;
        private TextView tvStt;
        private ImageView btImgMore;

        public SearchOnlineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tvStt);
            tvTitleSong = itemView.findViewById(R.id.tvItemNameSong);
            tvArtist = itemView.findViewById(R.id.tvItemDuration);
            btImgMore = itemView.findViewById(R.id.tvOptionsItemRecyclerView);
            itemView.setOnClickListener(this);
            btImgMore.setVisibility(View.GONE);
        }

        void bind(SongOnline song, int position) {
            tvStt.setText((position + 1) + "");
            tvTitleSong.setText(song.getNAMESONG());
            tvArtist.setText(song.getSINGER());
        }

        @Override
        public void onClick(View view) {
            listenner.onItemClick(getAdapterPosition());
        }
    }

    // interface
    public interface ISearchOnlineAdapterClickListener {
        void onItemClick(int position);
    }
}
