package com.example.activitymusic.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Adapter.PlayListAdapter;
import com.example.activitymusic.Adapter.SongOnlineListAdapter;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;
import com.example.activitymusic.Server.interfaceRefreshLayout;
import com.example.activitymusic.Service.MediaPlaybackService;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListSongPlayListFragment extends Fragment {

    private PlayList mPlayList;
    private ArrayList<SongOnline> mPlayListSong = new ArrayList<>();
    private TextView mNamePlayList, mAmount, mRepeat;
    private ImageView mIconplaylist, mBackgroundplaylist;
    private RecyclerView mRecyclerViewListSongPlayList;
    private SongOnlineListAdapter mSongOnlineListAdapter;
    private ProgressBar mProgressBar;
    private MediaPlaybackService mediaPlaybackService;
    protected MainActivityMusic getMusicactivity() {
        if (getActivity() instanceof MainActivityMusic) {
            return (MainActivityMusic) getActivity();
        }
        return null;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_song_playlist, container, false);
        inintView(view);
        getData();
        update();
        if (getMusicactivity().mMediaPlaybackService != null) {
            mediaPlaybackService = getMusicactivity().mMediaPlaybackService;
        }
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return view;
    }

    void inintView(View view) {
        mNamePlayList = view.findViewById(R.id.namePlayListSong);
        mAmount = view.findViewById(R.id.amount);
        mRepeat = view.findViewById(R.id.repeat);
        mIconplaylist = view.findViewById(R.id.iconplaylist);
        mRecyclerViewListSongPlayList = view.findViewById(R.id.recyclerviewlistsongplaylist);
        mBackgroundplaylist = view.findViewById(R.id.backgroundplaylist);
        mProgressBar = view.findViewById(R.id.ProgressBarPlayList);
    }

    void update() {
        mNamePlayList.setText(mPlayList.getNAMEPLAYLIST());
        onCountSongPlayList(mPlayList.getNAMEPLAYLIST());
        Glide.with(getContext()).load(mPlayList.getIMAGE()).into(mBackgroundplaylist);
        Glide.with(getContext()).load(mPlayList.getIMAGE()).into(mIconplaylist);

    }

    void getData() {
        DataServer dataServer = APIServer.getServer();
        Call<List<PlayList>> callback = dataServer.getDataPlayList();
        callback.enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
                ArrayList<PlayList> lists = (ArrayList<PlayList>) response.body();
                if(mPlayListSong.size()==0)
                for (int i = 0; i < lists.size(); i++) {

                    if (lists.get(i).getNAMEPLAYLIST().equals(mPlayList.getNAMEPLAYLIST())) {
                        mPlayListSong.add(new SongOnline(lists.get(i).getID() ,lists.get(i).getNAMESONG() , lists.get(i).getSINGER() ,
                                lists.get(i).getIMAGE(), lists.get(i).getLINKSONG(),lists.get(i).getVIEW() ,lists.get(i).getIDTL()));
                    }
                }
                mSongOnlineListAdapter = new SongOnlineListAdapter(mPlayListSong, getActivity(), "view");

                mRecyclerViewListSongPlayList.setAdapter(mSongOnlineListAdapter);
                mRecyclerViewListSongPlayList.setLayoutManager(new LinearLayoutManager(getContext()));
                mSongOnlineListAdapter.setActionDownloadSong(new SongOnlineListAdapter.actionDownloadSong() {
                    @Override
                    public void onDownloadSong(String Url) {
                        mediaPlaybackService.onDownloadSongOnline(Url, getActivity());
                    }

                    @Override
                    public void onRemovePlayList(String id_Song) {
                        DataServer dataServer = APIServer.getServer();
                        Call<String> callback = dataServer.RemoveSongPlayList(Integer.parseInt(id_Song), mPlayList.getNAMEPLAYLIST());
                        mediaPlaybackService.onRemoveSongPlayList(callback, getActivity());
                    }

                    @Override
                    public void onAddPlayingListSongsList(String id_Song) {// add playlist
                        Toast.makeText(getContext(), "Đang thêm vào danh sách phát", Toast.LENGTH_SHORT).show();
                        DataServer dataServer = APIServer.getServer();
                        Call<String> callback = dataServer.InsertPlayList(Integer.parseInt(id_Song), "Danh sách phát");
                        mediaPlaybackService.onRemoveSongPlayList(callback, getActivity());
                        Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAddPlayListSongsList(String id_Song, String status) {
                        ListPlayListFragment listPlayListFragment = new ListPlayListFragment(id_Song);
                        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.sub_fragment_a, listPlayListFragment).commit();
                      }
                });
                mProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {
                Toast.makeText(getActivity(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setmPlayList(PlayList mPlayList) {
        this.mPlayList = mPlayList;
    }
   void onCountSongPlayList(String namPlayList){
       DataServer dataServer = APIServer.getServer();
       Call<Integer> callback = dataServer.CountSongPlayList(namPlayList);
       callback.enqueue(new Callback<Integer>() {
           @Override
           public void onResponse(Call<Integer> call, Response<Integer> response) {
              mAmount.setText(response.body() + " bài hát");
           }

           @Override
           public void onFailure(Call<Integer> call, Throwable t) {
               Toast.makeText(getContext(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
           }
       });
   }

}
