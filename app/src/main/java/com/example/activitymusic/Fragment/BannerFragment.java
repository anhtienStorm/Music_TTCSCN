package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.activitymusic.Adapter.BannerAdapter;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerFragment extends Fragment {

    View mView;
    ViewPager mViewPager;
    CircleIndicator mCircleIndicator;
    BannerAdapter mBannerAdapter;
    Runnable mRunnable;
    Handler mHandler;
    int mCurrentItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.banner_fragment, container, false);
        initview();
        getData();
        return mView;
    }

    void initview(){
        mViewPager = mView.findViewById(R.id.viewpager_banner);
        mCircleIndicator = mView.findViewById(R.id.indicator_banner);
    }

    private void getData(){
        DataServer dataServer = APIServer.getServer();
        Call<List<SongOnline>> callback = dataServer.getDataSongOnline();
        callback.enqueue(new Callback<List<SongOnline>>() {
            @Override
            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
                ArrayList<SongOnline> songOnlineList = (ArrayList<SongOnline>) response.body();
                mBannerAdapter = new BannerAdapter(getActivity(),songOnlineList);
                mViewPager.setAdapter(mBannerAdapter);
                mCircleIndicator.setViewPager(mViewPager);
                mHandler = new Handler();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mCurrentItem = mViewPager.getCurrentItem();
                        mCurrentItem++;
                        if (mCurrentItem >= mViewPager.getAdapter().getCount()){
                            mCurrentItem = 0;
                        }
                        mViewPager.setCurrentItem(mCurrentItem,true);
                        mHandler.postDelayed(mRunnable,5000);
                    }
                };
                mHandler.postDelayed(mRunnable,5000);
            }

            @Override
            public void onFailure(Call<List<SongOnline>> call, Throwable t) {

            }
        });
    }
}
