package com.example.activitymusic;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityMusic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MediaPlaybackService mMediaPlaybackService;
    ImageView imgPlay;
    TextView tvNameSong, tvArtist;
    ImageView imgMainSong;
    Fragment mSelectedFragment, mAllSongsFragment, mFravoriteSongsFragment, mMediaPlaybackFragment;
    Boolean mCheckService = false;
    AllSongsProvider mAllSongsProvider;
    SharedPreferences mSharedPreferences;
    String sharePrefFile = "SongSharedPreferences";
    ICallbackFragmentServiceConnection mCallbackFragmentConnection;
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MediaPlaybackServiceBinder mediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackServiceBinder) iBinder;
            mMediaPlaybackService = mediaPlaybackServiceBinder.getService();
            update();
            mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
                @Override
                public void onSelect() {
                    update();
                }
            });
            mCheckService = true;

            mCallbackFragmentConnection.service(mMediaPlaybackService);

            mSharedPreferences = getSharedPreferences(sharePrefFile, MODE_PRIVATE);
            if (mSharedPreferences.getString("SONGLIST_ID","no List").equals("AllSong")){
                mMediaPlaybackService.setPlayingSongList(((AllSongsFragment) mAllSongsFragment).getSongList());
                mMediaPlaybackService.setPreviousExitSong(mSharedPreferences.getInt("SONG_ID",0));
                Log.d("abc", String.valueOf(mMediaPlaybackService.getPlayingSongList().size()));
                Log.d("abc", String.valueOf(mMediaPlaybackService.getPlayingSong().getId()));
                mMediaPlaybackService.preparePlay();
                mMediaPlaybackService.pause();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mCheckService = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, String.valueOf(isMyServiceRunning(MediaPlaybackService.class)), Toast.LENGTH_SHORT).show();
        if (isMyServiceRunning(MediaPlaybackService.class)) {
            connectService();
        } else {
            startService();
            connectService();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();   //xin cap quyen doc bo nho
        initView();
        mAllSongsProvider = new AllSongsProvider(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        createFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mAllSongsFragment).commit();

        imgPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.isMusicPlay()) {
                    if (mMediaPlaybackService.isPlaying()) {
                        mMediaPlaybackService.pause();
                    } else if (!mMediaPlaybackService.isPlaying()) {
                        mMediaPlaybackService.play();
                    }
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
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {

        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list_music) {
            mSelectedFragment = mAllSongsFragment;
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_fragment, mSelectedFragment).commit();
        } else if (id == R.id.nav_favorite) {
            mSelectedFragment = mFravoriteSongsFragment;
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_fragment, mSelectedFragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
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
    }

    public void startService() {
        Intent it = new Intent(ActivityMusic.this, MediaPlaybackService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(it);
        }
    }

    public void connectService() {
        Intent it = new Intent(ActivityMusic.this, MediaPlaybackService.class);
        bindService(it, mServiceConnection, 0);
    }

    public void initView() {
        imgPlay = findViewById(R.id.btMainPlay);
        tvNameSong = findViewById(R.id.tvMainNameSong);
        tvNameSong.setSelected(true);
        tvArtist = findViewById(R.id.tvMainArtist);
        imgMainSong = findViewById(R.id.imgMainSong);
    }

    public void update() {
        if (mMediaPlaybackService.isMusicPlay()) {
//            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//            Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mMediaPlaybackService.getAlbumID()));
//            Glide.with(this).load(uri).error(R.drawable.icon_default_song).into(imgMainSong);

            if (mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()) == null) {
                imgMainSong.setImageResource(R.drawable.icon_default_song);
            } else {
                imgMainSong.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
            }

            tvNameSong.setText(mMediaPlaybackService.getNameSong());
            tvArtist.setText(mMediaPlaybackService.getArtist());
            if (mMediaPlaybackService.isPlaying()) {
                imgPlay.setImageResource(R.drawable.ic_pause_black_24dp);
            } else {
                imgPlay.setImageResource(R.drawable.ic_play_black_24dp);
            }
        }
    }

    public void registerClientFragment(Fragment fragment) {
        this.mCallbackFragmentConnection = (ICallbackFragmentServiceConnection) fragment;
    }


    // cap quyen doc bo nho
    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(ActivityMusic.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(ActivityMusic.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ActivityMusic.this, "Quyền đọc file: được phép", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ActivityMusic.this, "Quyền đọc file: không được phép", Toast.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onClickLayoutPlayMusic(View view) {
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_fragment, mMediaPlaybackFragment).commit();
        findViewById(R.id.layoutPlayMusic).setVisibility(View.GONE);
    }

    // interface
    interface ICallbackFragmentServiceConnection {
        void service(MediaPlaybackService service);
    }
}
