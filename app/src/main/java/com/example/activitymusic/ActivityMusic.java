package com.example.activitymusic;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.IBinder;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Toast;

public class ActivityMusic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MediaPlaybackService mMediaPlaybackService;
    Fragment mSelectedFragment, mAllSongsFragment, mFravoriteSongsFragment, mMediaPlaybackFragment;
    Boolean mCheckService = false;
    IServiceConnectListenner mServiceConnectListenner;
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlaybackService.MediaPlaybackServiceBinder mediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackServiceBinder) iBinder;
            mMediaPlaybackService = mediaPlaybackServiceBinder.getService();
            mCheckService = true;
            mServiceConnectListenner.onConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mCheckService = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();   //xin cap quyen doc bo nho
        createFragment();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            //mAllSongsFragment = new AllSongsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a,mAllSongsFragment).commit();
        } else {
            getSupportFragmentManager().popBackStack();
            //mAllSongsFragment = new AllSongsFragment();
            //mMediaPlaybackFragment = new MediaPlaybackFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a, mAllSongsFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_b, mMediaPlaybackFragment).commit();
        }
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
        if (id == R.id.nav_list_music) {
//            mAllSongsFragment = new AllSongsFragment();
            mSelectedFragment = mAllSongsFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a, mSelectedFragment).commit();
        } else if (id == R.id.nav_favorite) {
//            mFravoriteSongsFragment = new FavoriteSongsFragment();
            mSelectedFragment = mFravoriteSongsFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a, mSelectedFragment).commit();
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
        //mMediaPlaybackFragment = new MediaPlaybackFragment();
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.sub_fragment_a, mMediaPlaybackFragment).commit();
        findViewById(R.id.layoutPlayMusic).setVisibility(View.GONE);
    }

    public void setServiceConnectListenner(IServiceConnectListenner serviceConnectListenner){
        this.mServiceConnectListenner = serviceConnectListenner;
    };

    interface IServiceConnectListenner {
        void onConnect();
    }
}
