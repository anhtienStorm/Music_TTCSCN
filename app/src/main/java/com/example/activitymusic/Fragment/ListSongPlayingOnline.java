package com.example.activitymusic.Fragment;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Adapter.SongOnlineListAdapter;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;
import com.example.activitymusic.Service.MediaPlaybackService;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListSongPlayingOnline extends Fragment {

    private SongOnlineListAdapter mSongOnlineListAdapter;
    private RecyclerView mRecyclerViewListSong;
    private MediaPlaybackService mediaPlaybackService;
    private String NAME_PLAYLIST;
    private ProgressBar progressBar;
    private String mStatus;
    protected MainActivityMusic getMusicactivity() {
        if (getActivity() instanceof MainActivityMusic) {
            return (MainActivityMusic) getActivity();
        }
        return null;
    }

    public ListSongPlayingOnline(String NAME_PLAYLIST, String status) {
        this.NAME_PLAYLIST = NAME_PLAYLIST;
        this.mStatus=status;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_playing_online_fragment, container, false);
        mRecyclerViewListSong = view.findViewById(R.id.RecyclerViewListPlayingOnline);
        progressBar=view.findViewById(R.id.ProgressBarlistonline);
        getData();

        if (getMusicactivity().mMediaPlaybackService != null) {
            mediaPlaybackService = getMusicactivity().mMediaPlaybackService;
        }
        if(!mStatus.equals("view")){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
        }
        return view;
    }

    void getData() {
        DataServer dataServer = APIServer.getServer();
        Call<List<SongOnline>> callback = dataServer.getDataPlayListSong(NAME_PLAYLIST);
        callback.enqueue(new Callback<List<SongOnline>>() {
            @Override
            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
                final ArrayList<SongOnline> lists = (ArrayList<SongOnline>) response.body();
                mSongOnlineListAdapter = new SongOnlineListAdapter(lists, getActivity(),mStatus);
                mRecyclerViewListSong.setAdapter(mSongOnlineListAdapter);
                mRecyclerViewListSong.setLayoutManager(new LinearLayoutManager(getContext()));
                mSongOnlineListAdapter.setActionDownloadSong(new SongOnlineListAdapter.actionDownloadSong() {
                    @Override
                    public void onDownloadSong(String Url) {
                        mediaPlaybackService.onDownloadSongOnline(Url, getActivity());
                    }

                    @Override
                    public void onRemovePlayList(String id_Song) {
                        // not action
                    }

                    @Override
                    public void onAddPlayingListSongsList(String id_Song) {
                        Toast.makeText(getContext(), "Đang thêm vào danh sách phát", Toast.LENGTH_SHORT).show();
                        DataServer dataServer = APIServer.getServer();
                        Call<String> callback = dataServer.InsertPlayList(Integer.parseInt(id_Song), "Danh sách phát");
                        mediaPlaybackService.onRemoveSongPlayList(callback, getActivity());
                    }

                    @Override
                    public void onAddPlayListSongsList(String id_Song , String status) {
                        Log.d("TienNvh", "onAddPlayListSongsList: "+status);
                        if(status.equals("view")) {
                            ListPlayListFragment listPlayListFragment = new ListPlayListFragment(id_Song);
                            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.sub_fragment_a, listPlayListFragment).commit();
                        }else {
                            DataServer dataServer = APIServer.getServer();
                            Call<String> callback = dataServer.InsertPlayList(Integer.parseInt(id_Song) ,mStatus);
                            onActionPlayList(callback);
                            getFragmentManager().popBackStack();
                        }

                    }
                });
              progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<SongOnline>> call, Throwable t) {
                Toast.makeText(getActivity(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void onActionPlayList(Call<String> callback) {// có thể vứt lên service
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                if (result.equals("true")) {
                    if(getContext()!=null)
                        Toast.makeText(getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                    getData();
                } else
                if(getContext()!=null)
                    Toast.makeText(getContext(), "Thất bại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(getContext()!=null)
                    Toast.makeText(getActivity(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
