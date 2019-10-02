package com.example.activitymusic;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.SongViewHolder> implements Filterable {

    private List<Song> mListSong = new ArrayList<>();
    private List<Song> mListFullSong = new ArrayList<>();
    private Context mContext;
    private ISongListAdapterClickListener listenner;
    private String mTypeSongList;
    private MediaPlaybackService mMediaPlaybackService;
    private Song mPlayingSong;


    public ListSongAdapter(ArrayList<Song> listSong, Context context) {
        this.mListSong = listSong;
        this.mContext = context;
    }

    public void updateList(List<Song> songs) {
        mListSong = songs;
        mListFullSong = new ArrayList<>(mListSong);
        notifyDataSetChanged();
    }

    public void setTypeSongList(String typeSongList) {
        this.mTypeSongList = typeSongList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recyclerview_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = mListSong.get(position);
        if (mMediaPlaybackService != null) {
            mPlayingSong = mMediaPlaybackService.getPlayingSong();
            if (mPlayingSong != null) {
                if (song.getId() == mPlayingSong.getId()) {
                    holder.tvTitleSong.setTypeface(null, Typeface.BOLD);
                    holder.tvStt.setBackgroundResource(R.drawable.ic_equalizer_black_24dp);
                    holder.tvStt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0);
                } else {
                    holder.tvTitleSong.setTypeface(null, Typeface.NORMAL);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.tvStt.setBackground(null);
                    }
                    holder.tvStt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
            }
        }
        holder.bind(song, position);

    }

    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    public void setOnClickListenner(ISongListAdapterClickListener listenner) {
        this.listenner = listenner;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Song> filterList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(mListFullSong);
            } else {
                String filterPattern = unAccent(charSequence.toString().toLowerCase().trim());

                for (Song song : mListFullSong) {
                    if (unAccent(song.getNameSong().toLowerCase()).contains(filterPattern)) {
                        filterList.add(song);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mListSong.clear();
            mListSong.addAll((Collection<? extends Song>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
    }

    public void setService(Service service) {
        mMediaPlaybackService = (MediaPlaybackService) service;
    }


    //interface
    interface ISongListAdapterClickListener {
        void onItemClick(int position);
    }

    // class
    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitleSong;
        private TextView tvDuration;
        private TextView tvStt;
        private ImageView btImgMore;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tvStt);
            tvTitleSong = itemView.findViewById(R.id.tvItemNameSong);
            tvDuration = itemView.findViewById(R.id.tvItemDuration);
            btImgMore = itemView.findViewById(R.id.tvOptionsItemRecyclerView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listenner.onItemClick(getAdapterPosition());
        }

        public void bind(final Song song, int position) {
            tvStt.setText((position + 1) + "");
            tvTitleSong.setText(song.getNameSong());
            tvDuration.setText(song.getDuration());
            if (mTypeSongList != null) {
                btImgMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(mContext, btImgMore);
                        if (mTypeSongList.equals("AllSongs")) {
                            popupMenu.inflate(R.menu.item_recyclerview_more_all_song_menu);
                        } else if (mTypeSongList.equals("FavoriteSongs")){
                            popupMenu.inflate(R.menu.item_recyclerview_more_favorite_song_menu);
                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.more_all_song:
                                        addFavoriteSongsList(song.getId());
                                        return true;
                                    case R.id.more_favorite_song:
                                        removeFavoriteSongsList(song.getId());
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
        }
    }

    public void removeFavoriteSongsList(int id){
        ContentValues values = new ContentValues();
        values.put(FavoriteSongsProvider.IS_FAVORITE,0);
        mContext.getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,"ID_PROVIDER = "+id,null);
        Toast.makeText(mContext, "Deteled Sucessfull !", Toast.LENGTH_SHORT).show();
    }

    public void addFavoriteSongsList(int id){
        ContentValues values = new ContentValues();
        values.put(FavoriteSongsProvider.IS_FAVORITE,2);
        mContext.getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,"ID_PROVIDER = "+id,null);
        Toast.makeText(mContext, "Add Sucessfull !", Toast.LENGTH_SHORT).show();
    }
}
