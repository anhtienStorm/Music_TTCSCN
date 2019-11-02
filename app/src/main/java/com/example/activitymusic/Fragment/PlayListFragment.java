package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Adapter.BannerAdapter;
import com.example.activitymusic.Adapter.PlayListAdapter;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;
import com.example.activitymusic.Server.interfaceRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.recyclerview.widget.LinearLayoutManager.*;

public class PlayListFragment extends Fragment {
    View mView;
    private interfaceRefreshLayout mRefreshLayout;
    String TAG = "TienNVh";
    private RecyclerView mRecyclerViewBasePlayList;
    private PlayListAdapter mPlayListAdapter;
    private ListSongPlayListFragment listSongPlayListFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.play_list_fragment, container, false);
        mRecyclerViewBasePlayList = mView.findViewById(R.id.recyclerviewPlaylist);

        getData();

        return mView;
    }


    private void getData() {
        DataServer dataServer = APIServer.getServer();
        Call<List<PlayList>> callback = dataServer.getDataPlayList();
        callback.enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
                ArrayList<PlayList> checkPlaylist = new ArrayList<>();
                final ArrayList<PlayList> playLists = (ArrayList<PlayList>) response.body();

                checkPlaylist.add(playLists.get(0));
                for (int i = 0; i < playLists.size(); i++) {
                    boolean exist= true;
                    for (int j = 0; j < checkPlaylist.size(); j++) {
                        if (checkPlaylist.get(j).getNAMEPLAYLIST().equals(playLists.get(i).getNAMEPLAYLIST()))
                            exist=false;
                    }
                  if(exist)
                        checkPlaylist.add(playLists.get(i));
                }

                mPlayListAdapter = new PlayListAdapter(getActivity(), checkPlaylist);
                mRecyclerViewBasePlayList.setAdapter(mPlayListAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
                mRecyclerViewBasePlayList.setLayoutManager(layoutManager);
                mPlayListAdapter.setConnectHomeOnlineAndAdapter(new PlayListAdapter.connectHomeOnlineAndAdapter() {
                    @Override
                    public void connectAdapter(int index) {
                        listSongPlayListFragment = new ListSongPlayListFragment();
                        listSongPlayListFragment.setmPlayList(playLists.get(index));
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a, listSongPlayListFragment).commit();
                    }
                });
                mRefreshLayout.refreshLayout();
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {

            }
        });
    }

    public void setmRefreshLayout(interfaceRefreshLayout mRefreshLayout) {
        this.mRefreshLayout = mRefreshLayout;
    }
}
