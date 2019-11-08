package com.example.activitymusic.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Fragment.AllSongsFragment;
import com.example.activitymusic.Fragment.BaseSongListFragment;
import com.example.activitymusic.Fragment.FavoriteSongsFragment;
import com.example.activitymusic.Fragment.HomeOnlineFragment;
import com.example.activitymusic.Fragment.ListPlayListFragment;
import com.example.activitymusic.Fragment.MediaPlaybackFragment;
import com.example.activitymusic.Fragment.NotificationFragment;
import com.example.activitymusic.R;
import com.example.activitymusic.Service.JobSchedulerService;
import com.example.activitymusic.Service.MediaPlaybackService;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivityMusic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public MediaPlaybackService mMediaPlaybackService;
    public Fragment mSelectedFragment, mAllSongsFragment, mFravoriteSongsFragment, mMediaPlaybackFragment, mHomeOnlineFragment , mNotificationFragment, mListPlayList;
    ImageView imgPlay;
    TextView tvNameSong, tvArtist;
    ImageView imgMainSong;
    IServiceConnectListenner1 mServiceConnectListenner1;
    IServiceConnectListenner2 mServiceConnectListenner2;
    String mNameFragmentSelect;


    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MediaPlaybackServiceBinder mediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackServiceBinder) iBinder;
            mMediaPlaybackService = mediaPlaybackServiceBinder.getService();
            mServiceConnectListenner1.onConnect();
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mServiceConnectListenner2.onConnect();
            }
            update();
            mMediaPlaybackService.listenChangeStatus(new MediaPlaybackService.IServiceCallback() {
                @Override
                public void onUpdate() {
                    update();
                }

            });
            if (!mMediaPlaybackService.isMusicPlay()) {
                if (mMediaPlaybackService.getSharedPreferences().contains("SONG_LIST")) {
                    mMediaPlaybackService.loadData();
                    updateSaveSong();
                } else {
                    findViewById(R.id.layoutPlayMusic).setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (isMyServiceRunning(MediaPlaybackService.class)) {
            connectService();
        } else {
            startService();
            connectService();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();   //xin cap quyen doc bo nho
        createFragment();
        initView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            mNameFragmentSelect = savedInstanceState.getString("name_fragment_select");

            if (mNameFragmentSelect != null) {
                switch (mNameFragmentSelect) {
                    case "FavoriteSong":
                        mSelectedFragment = mFravoriteSongsFragment;
                        navigationView.setCheckedItem(R.id.nav_favorite);
                        setTitle("Music Offline");
                    case "AllSong":
                        mSelectedFragment = mAllSongsFragment;
                        navigationView.setCheckedItem(R.id.nav_list_music);
                        setTitle("Music Offline");
                    case "HomeOnline":
                        mSelectedFragment = mHomeOnlineFragment;
                        navigationView.setCheckedItem(R.id.nav_home);
                        setTitle("Music Online");
                    case "Notification":
                        mSelectedFragment = mNotificationFragment;
                        navigationView.setCheckedItem(R.id.nav_notification);
                        setTitle("Notification");
                    case "PlayList":
                        mSelectedFragment = mListPlayList;
                        navigationView.setCheckedItem(R.id.nav_playlist);
                        setTitle("PlayList");

                }
            } else {

                mSelectedFragment = mAllSongsFragment;
                navigationView.setCheckedItem(R.id.nav_list_music);
                setTitle("Music Offline");
            }
        } else {
            mSelectedFragment = mAllSongsFragment;
            navigationView.setCheckedItem(R.id.nav_list_music);
            setTitle("Music Offline");
        }

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a, mSelectedFragment).commit();
        } else {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_b, mMediaPlaybackFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a, mSelectedFragment).commit();
        }


        ComponentName componentName = new ComponentName(this, JobSchedulerService.class);
        JobInfo info = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("Tiennvh", "Job scheduled");
        } else {
            Log.d("Tiennvh", "Job scheduling failed");
        }

        imgPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.isMusicPlay()) {
                    if (mMediaPlaybackService.isPlaying()) {
                        mMediaPlaybackService.pause();
                    } else {
                        mMediaPlaybackService.play();
                    }
                } else {
                    mMediaPlaybackService.preparePlay();
                }
            }
        });



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_list_music:
                mSelectedFragment = mAllSongsFragment;
                mNameFragmentSelect = "AllSong";
                setTitle("Music Offline");

                break;
            case R.id.nav_favorite:
                mSelectedFragment = mFravoriteSongsFragment;
                mNameFragmentSelect = "FavoriteSong";
                setTitle("Music Offline");
                break;
            case R.id.nav_home:
                mSelectedFragment = mHomeOnlineFragment;
                mNameFragmentSelect = "HomeOnline";
                setTitle("Music Online");
                break;
            case R.id.nav_notification:
                mSelectedFragment = mNotificationFragment;
                mNameFragmentSelect = "Notification";
                setTitle("Music Notification");
                break;
            case R.id.nav_playlist:
                mSelectedFragment = mListPlayList;
                mNameFragmentSelect = "PlayList";
                setTitle("Music PlayList");
                break;

        }

        getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a, mSelectedFragment).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //method
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void createFragment() {
        mAllSongsFragment = new AllSongsFragment();
        mFravoriteSongsFragment = new FavoriteSongsFragment();
        mMediaPlaybackFragment = new MediaPlaybackFragment();
        mHomeOnlineFragment = new HomeOnlineFragment();
        mNotificationFragment = new NotificationFragment();
        mListPlayList = new ListPlayListFragment("view");
    }

    public void initView() {
        imgPlay = findViewById(R.id.btMainPlay);
        tvNameSong = findViewById(R.id.tvMainNameSong);
        tvNameSong.setSelected(true);
        tvArtist = findViewById(R.id.tvMainArtist);
        imgMainSong = findViewById(R.id.imgMainSong);
    }

    public void startService() {
        Intent it = new Intent(MainActivityMusic.this, MediaPlaybackService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(it);
        }
    }

    public void connectService() {
        Intent it = new Intent(this, MediaPlaybackService.class);
        bindService(it, mServiceConnection, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    // cap quyen doc bo nho
    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivityMusic.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(MainActivityMusic.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivityMusic.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(MainActivityMusic.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 || requestCode == 2) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivityMusic.this, "Quyền đọc file: được phép", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivityMusic.this, "Quyền đọc file: không được phép", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onClickLayoutPlayMusic(View view) {
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.sub_fragment_a, mMediaPlaybackFragment).commit();
        findViewById(R.id.layoutPlayMusic).setVisibility(View.GONE);
    }

    public void setServiceConnectListenner1(IServiceConnectListenner1 serviceConnectListenner) {
        this.mServiceConnectListenner1 = serviceConnectListenner;
    }

    public void setServiceConnectListenner2(IServiceConnectListenner2 serviceConnectListenner) {
        this.mServiceConnectListenner2 = serviceConnectListenner;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name_fragment_select", mNameFragmentSelect);
    }

    public void update() {
        updateUI();
        if (mMediaPlaybackFragment.getView() != null) {
            ((MediaPlaybackFragment) mMediaPlaybackFragment).update();
        }
    }

    void updateUI() {
        if (mMediaPlaybackService.isMusicPlay()) {
            if (mAllSongsFragment.getView() != null || mFravoriteSongsFragment.getView() != null) {
                ((BaseSongListFragment) mSelectedFragment).mSongListAdapter.notifyDataSetChanged();
            }

            if (mMediaPlaybackService.isPlaying()) {
                imgPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                tvNameSong.setText(mMediaPlaybackService.getNameSong());
                tvArtist.setText(mMediaPlaybackService.getArtist());
                if (mMediaPlaybackService.mIsPlayOnline) {
                    Glide.with(this).load(mMediaPlaybackService.getPlayingSongOnline().getIMAGE()).error(R.drawable.icon_default_song).into(imgMainSong);
                } else {
                    if (loadImageFromPath(mMediaPlaybackService.getPathSong()) == null) {
                        imgMainSong.setImageResource(R.drawable.icon_default_song);
                    } else {
                        imgMainSong.setImageBitmap(loadImageFromPath(mMediaPlaybackService.getPathSong()));
                    }
                }
            } else {
                imgPlay.setImageResource(R.drawable.ic_play_black_24dp);
            }
        }
    }

    void updateSaveSong() {
        if (mAllSongsFragment.getView() != null || mFravoriteSongsFragment.getView() != null) {
            ((BaseSongListFragment) mSelectedFragment).mSongListAdapter.notifyDataSetChanged();
        }

        if (mMediaPlaybackService.mIsPlayOnline) {
            Glide.with(this).load(mMediaPlaybackService.getPlayingSongOnline().getIMAGE()).error(R.drawable.icon_default_song).into(imgMainSong);
        } else {
            if (loadImageFromPath(mMediaPlaybackService.getPathSong()) == null) {
                imgMainSong.setImageResource(R.drawable.icon_default_song);
            } else {
                imgMainSong.setImageBitmap(loadImageFromPath(mMediaPlaybackService.getPathSong()));
            }
        }

        tvNameSong.setText(mMediaPlaybackService.getNameSong());
        tvArtist.setText(mMediaPlaybackService.getArtist());
    }

    public Bitmap loadImageFromPath(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
        return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    //interface
    public interface IServiceConnectListenner1 {
        void onConnect();
    }

    public interface IServiceConnectListenner2 {
        void onConnect();
    }
}