package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.activitymusic.R;
import com.example.activitymusic.Server.interfaceRefreshLayout;

public class HomeOnlineFragment extends Fragment {
    SwipeRefreshLayout mSwipeRefreshLayout;
    BannerFragment bannerFragment;
    connectRefreshLayout connectRefreshLayout;
    ProgressBar mProgressBar;
    PlayListFragment playListFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_online_fragment, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayout);
        mProgressBar = view.findViewById(R.id.ProgressBar);

        bannerFragment = new BannerFragment();
        playListFragment = new PlayListFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.banner_fragment, bannerFragment).commit();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.play_list_fragment, playListFragment).commit();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bannerFragment = new BannerFragment();
                playListFragment = new PlayListFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.banner_fragment, bannerFragment).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.play_list_fragment, playListFragment).commit();
                startRefreshing();
            }
        });
        startRefreshing();

        return view;
    }

    void startRefreshing(){
        bannerFragment.setmRefreshLayout(new interfaceRefreshLayout() {
            @Override
            public void refreshLayout() {
          // có thể bỏ đi
            }
        });
        playListFragment.setmRefreshLayout(new interfaceRefreshLayout() {
            @Override
            public void refreshLayout() {
                mProgressBar.setVisibility(View.GONE);
                stopRefreshing();
            }
        });
    }

    void stopRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setConnectRefreshLayout(HomeOnlineFragment.connectRefreshLayout connectRefreshLayout) {
        this.connectRefreshLayout = connectRefreshLayout;
    }

    interface connectRefreshLayout {
        void refreshLayout();

    }
}
