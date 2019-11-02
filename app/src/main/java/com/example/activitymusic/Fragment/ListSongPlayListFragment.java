package com.example.activitymusic.Fragment;

import android.Manifest;
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
import com.example.activitymusic.Adapter.PlayListAdapter;
import com.example.activitymusic.Adapter.SongOnlineListAdapter;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;
import com.example.activitymusic.Server.interfaceRefreshLayout;

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
    private ArrayList<PlayList> mPlayListSong = new ArrayList<>();
    private TextView mNamePlayList, mAmount, mRepeat;
    private ImageView mIconplaylist, mBackgroundplaylist;
    private RecyclerView mRecyclerViewListSongPlayList;
    private SongOnlineListAdapter mSongOnlineListAdapter;
    private static String CHANNEL_ID = "Notification Download";
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_song_playlist, container, false);
        inintView(view);
        getData();
        update();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel musicServiceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification Download",
                    NotificationManager.IMPORTANCE_LOW
            );
            musicServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            musicServiceChannel.enableVibration(false);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(musicServiceChannel);
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
                for (int i = 0; i < lists.size(); i++) {

                    if (lists.get(i).getNAMEPLAYLIST().equals(mPlayList.getNAMEPLAYLIST())) {
                        mPlayListSong.add(lists.get(i));
                    }
                }
                mSongOnlineListAdapter = new SongOnlineListAdapter(mPlayListSong, getActivity());

                mRecyclerViewListSongPlayList.setAdapter(mSongOnlineListAdapter);
                mRecyclerViewListSongPlayList.setLayoutManager(new LinearLayoutManager(getContext()));
                mSongOnlineListAdapter.setActionDownloadSong(new SongOnlineListAdapter.actionDownloadSong() {
                    @Override
                    public void onDownloadSong(String Url) {
                        if (isSDCardPresent()) {
                            if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                new DownloadFile(Url).execute(Url);
                            } else {
                                EasyPermissions.requestPermissions(getActivity(), "This app needs access to your file storage so that it can write files.", 300, Manifest.permission.READ_EXTERNAL_STORAGE);
                            }

                        } else {
                            Toast.makeText(getActivity(), "SD Card not found", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onRemovePlayList(String id_Song) {
                        onRemoveSongPlayList(Integer.parseInt(id_Song),mPlayList.getNAMEPLAYLIST());
                    }

                    @Override
                    public void onAddPlayListSongsList(String id_Song) {

                    }

                    @Override
                    public void onAddPlayingListSongsList(String id_Song) {

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

    void onRemoveSongPlayList(int ID_SONG, String NAME_PLAYLIST){
        DataServer dataServer = APIServer.getServer();
        Call<String> callback = dataServer.RemoveSongPlayList(ID_SONG, NAME_PLAYLIST);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                if (result.equals("true")) {
                    Toast.makeText(getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Thất bại", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setmPlayList(PlayList mPlayList) {
        this.mPlayList = mPlayList;
    }


    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;

    private class DownloadFile extends AsyncTask<String, Integer, String> {

        private String mUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getContext(), "Bắt đầu tải xuống ...", Toast.LENGTH_SHORT).show();
            notificationManager = NotificationManagerCompat.from(getContext());
            builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
            builder.setContentTitle("Music Download")
                    .setContentText("Download in progress")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                    .setPriority(NotificationCompat.PRIORITY_LOW);
        }

        public DownloadFile(String mUrl) {
            this.mUrl = mUrl;
        }

        @Override
        protected String doInBackground(String... urlParams) {
            int count;
            try {

                URL url = new URL(mUrl);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();

                String nameSong = mUrl.substring(44);
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream("/sdcard/" + nameSong);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.d("Looi", e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

            builder.setProgress(100, progress[0], false);
            notificationManager.notify(2, builder.build());
        }

        @Override
        protected void onPostExecute(String message) {
            notificationManager.cancel(2);
//          builder.setContentText("Download complete") ;
//           notificationManager.notify(3, builder.build());
//            Log.d(TAG, "onPostExecute: ok ");

        }
    }

}
