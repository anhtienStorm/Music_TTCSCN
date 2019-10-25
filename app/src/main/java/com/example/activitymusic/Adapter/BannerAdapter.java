package com.example.activitymusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {
    private onClickSongOnline mOnClickSongOnline;
    Context mContext;
    ArrayList<SongOnline> mSongOnlineList = new ArrayList<>();

    public BannerAdapter(Context context, ArrayList<SongOnline> list){
        mContext = context;
        mSongOnlineList = list;
    }

    public void setmOnClickSongOnline(onClickSongOnline mOnClickSongOnline) {
        this.mOnClickSongOnline = mOnClickSongOnline;
    }

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
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.row_baner,null);

        ImageView imgBackgroundBanner = view.findViewById(R.id.img_background_banner);
        ImageView imgIconBanner = view.findViewById(R.id.img_icon_banner);
        TextView textViewTitleBanner = view.findViewById(R.id.tv_title_banner);
        TextView textViewContentBanner = view.findViewById(R.id.tv_content_banner);

        Glide.with(mContext).load(mSongOnlineList.get(position).getIMAGE()).into(imgBackgroundBanner);
        Glide.with(mContext).load(mSongOnlineList.get(position).getIMAGE()).into(imgIconBanner);
        textViewTitleBanner.setText(mSongOnlineList.get(position).getNAMESONG());
        textViewContentBanner.setText(mSongOnlineList.get(position).getSINGER());
        imgBackgroundBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickSongOnline.onClick(position);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
     public  interface  onClickSongOnline{
        void onClick(int position );
    }


}
