package com.example.activitymusic.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.activitymusic.Model.SongOnline;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<SongOnline> mSongOnlineList = new ArrayList<>();

    @Override
    public int getCount() {
        return mSongOnlineList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
