package com.example.activitymusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;


public class MediaPlaybackService extends Service {
    public static final String CHANNEL_ID = "MusicServiceChannel";
    private MediaPlayer mMediaPlayer = null;
    private final Binder mBinder = new MediaPlaybackServiceBinder();
    private ArrayList<Song> mPlayingSongList;
    private Song mPLayingSong;
    private int mIndexofPlayingSong;
    private IServiceCallback mServiceCallback;
    private int mStatusLoop = 0;
    private int mShuffle = 0;
    private AllSongsProvider mAllSongsProvider;
    private SharedPreferences mSharedPreferences;
    private String sharePrefFile = "SongSharedPreferences";

//    private IServiceCallbackMediaPlaybackFragment mServiceCallbackMediaPlaybackFragment;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel musicServiceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "AllSongsProvider Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            musicServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(musicServiceChannel);
        }
        mAllSongsProvider = new AllSongsProvider(getApplicationContext());
        mSharedPreferences = getSharedPreferences(sharePrefFile, MODE_PRIVATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showToast(intent.getAction());

        if (isMusicPlay()) {
            switch (intent.getAction()) {
                case "Previous":
                    previousSong();
                    break;
                case "Play":
                    if (isPlaying()) {
                        pause();
                    } else {
                        play();
                    }
                    break;
                case "Next":
                    nextSong();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    public void showNotification() {
        RemoteViews subNotificationLayout = new RemoteViews(getPackageName(), R.layout.sub_notification);
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification);

        Intent notificationIntent = new Intent(this, ActivityMusic.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, MediaPlaybackService.class);
        previousIntent.setAction("Previous");
        PendingIntent previousPendingIntent = null;

        Intent playIntent = new Intent(this, MediaPlaybackService.class);
        playIntent.setAction("Play");
        PendingIntent playPendingIntent = null;

        Intent nextIntent = new Intent(this, MediaPlaybackService.class);
        nextIntent.setAction("Next");
        PendingIntent nextPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            previousPendingIntent = PendingIntent.getForegroundService(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            playPendingIntent = PendingIntent.getForegroundService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nextPendingIntent = PendingIntent.getForegroundService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Bitmap bitmap = mAllSongsProvider.getBitmapAlbumArt(getAlbumID());

        notificationLayout.setOnClickPendingIntent(R.id.notify_previous, previousPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notify_play, playPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notify_next, nextPendingIntent);
        notificationLayout.setImageViewBitmap(R.id.notify_img_song, bitmap == null ? BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_song) : bitmap);
        notificationLayout.setTextViewText(R.id.notify_song_name, getNameSong());
        notificationLayout.setTextViewText(R.id.notify_artist, getArtist());
        notificationLayout.setImageViewResource(R.id.notify_play, isPlaying() ? R.drawable.ic_pause_circle_filled_orange_24dp : R.drawable.ic_play_circle_filled_orange_24dp);

        subNotificationLayout.setOnClickPendingIntent(R.id.sub_notify_previous, previousPendingIntent);
        subNotificationLayout.setOnClickPendingIntent(R.id.sub_notify_play, playPendingIntent);
        subNotificationLayout.setOnClickPendingIntent(R.id.sub_notify_next, nextPendingIntent);
        subNotificationLayout.setImageViewBitmap(R.id.sub_notify_img_song, bitmap == null ? BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_song) : bitmap);
        subNotificationLayout.setImageViewResource(R.id.sub_notify_play, isPlaying() ? R.drawable.ic_pause_circle_filled_orange_24dp : R.drawable.ic_play_circle_filled_orange_24dp);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_notification)
//                .setContentTitle(getNameSong())
//                .setContentText(getArtist())
//                .setLargeIcon(bitmap == null ? BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_song) : bitmap)
                .setPriority(2)
//                .addAction(R.drawable.ic_skip_previous_black_24dp, "previous", previousPendingIntent)
//                .addAction(isPlaying() ? R.drawable.ic_pause_circle_filled_orange_24dp : R.drawable.ic_play_circle_filled_orange_24dp, "play", playPendingIntent)
//                .addAction(R.drawable.ic_skip_next_black_24dp, "next", nextPendingIntent)
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setShowActionsInCompactView(0, 1, 2))
                .setCustomContentView(subNotificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // method
    public boolean isMusicPlay() {
        if (mMediaPlayer != null) {
            return true;
        }
        return false;
    }

    public String getNameSong() {
        return mPLayingSong.getNameSong();
    }

    public String getArtist() {
        return mPLayingSong.getSinger();
    }

    public String getAlbumID() {
        return mPLayingSong.getAlbumID();
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public int getId() {
        return mPLayingSong.getId();
    }

    public Song getPlayingSong() {
        return mPLayingSong;
    }

    public int getIndexofPlayingSong() {
        return mIndexofPlayingSong;
    }

    public int getmStatusLoop() {
        return mStatusLoop;
    }

    public int getmShuffle() {
        return mShuffle;
    }

    public boolean isPlaying() {
        if (mMediaPlayer.isPlaying())
            return true;
        else
            return false;
    }

    public void play() {
        mMediaPlayer.start();
        showNotification();
        mServiceCallback.onUpdate();

//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mServiceCallbackMediaPlaybackFragment.onUpdate();
//        }
    }

    public void pause() {
        mMediaPlayer.pause();
        showNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH);
        }
        mServiceCallback.onUpdate();

//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mServiceCallbackMediaPlaybackFragment.onUpdate();
//        }
    }

    public void stop() {
        mMediaPlayer.stop();
        showNotification();
        mServiceCallback.onUpdate();

//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mServiceCallbackMediaPlaybackFragment.onUpdate();
//        }
    }

    public void preparePlay() {
        Uri uri = Uri.parse(mPLayingSong.getPathSong());
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        }

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mMediaPlayer.start();
        showNotification();
        mIndexofPlayingSong = mPlayingSongList.indexOf(mPLayingSong);
        mServiceCallback.onUpdate();

//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mServiceCallbackMediaPlaybackFragment.onUpdate();
//        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mStatusLoop == 0) {
                    nextSongNoloop();
                } else if (mStatusLoop == 1) {
                    nextSong();
                } else {
                    playSong(mPlayingSongList, mPLayingSong);
                }
            }
        });

        saveData();
    }

    public void playSong(final ArrayList<Song> listSong, final Song song) {
        this.mPlayingSongList = listSong;
        this.mPLayingSong = song;
        preparePlay();
    }

    public void nextSong() {
        if (isMusicPlay()) {
            if (mShuffle == 0) {
                if (mIndexofPlayingSong == mPlayingSongList.size() - 1) {
                    mIndexofPlayingSong = 0;
                    mPLayingSong = mPlayingSongList.get(mIndexofPlayingSong);
                } else {
                    mIndexofPlayingSong += 1;
                    mPLayingSong = mPlayingSongList.get(mIndexofPlayingSong);
                }
            } else {
                Random rd = new Random();
                mIndexofPlayingSong = rd.nextInt(mPlayingSongList.size());
                mPLayingSong = mPlayingSongList.get(mIndexofPlayingSong);
            }
            preparePlay();
        }
    }

    public void nextSongNoloop() {
        if (isMusicPlay()) {
            if (mShuffle == 0) {
                if (mIndexofPlayingSong == mPlayingSongList.size() - 1) {
                    stop();
                    playSong(mPlayingSongList, mPLayingSong);
                    preparePlay();
                    pause();
                } else {
                    mIndexofPlayingSong += 1;
                    mPLayingSong = mPlayingSongList.get(mIndexofPlayingSong);
                    preparePlay();
                }
            } else {
                Random rd = new Random();
                mIndexofPlayingSong = rd.nextInt(mPlayingSongList.size());
                mPLayingSong = mPlayingSongList.get(mIndexofPlayingSong);
                preparePlay();
            }
        }
    }

    public void previousSong() {
        if (isMusicPlay()) {
            if (mShuffle == 0) {
                if (mIndexofPlayingSong == 0) {
                    mIndexofPlayingSong = mPlayingSongList.size() - 1;
                    mPLayingSong = mPlayingSongList.get(mIndexofPlayingSong);
                } else {
                    mIndexofPlayingSong -= 1;
                    mPLayingSong = mPlayingSongList.get(mIndexofPlayingSong);
                }
            } else {
                Random rd = new Random();
                mIndexofPlayingSong = rd.nextInt(mPlayingSongList.size());
                mPLayingSong = mPlayingSongList.get(mIndexofPlayingSong);
            }
            preparePlay();
        }
    }

    public void shuffleSong() {
        if (mShuffle == 0) {
            mShuffle = 1;
            showToast("Shuffle On");
        } else {
            mShuffle = 0;
            showToast("Shuffle Off");
        }
        mServiceCallback.onUpdate();

//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
//            mServiceCallbackMediaPlaybackFragment.onUpdate();
//        }
    }

    public void loopSong() {
        if (mStatusLoop == 0) {
            mStatusLoop = 1;
            showToast("Loop List");
        } else if (mStatusLoop == 1) {
            mStatusLoop = 2;
            showToast("Loop One");
        } else if (mStatusLoop == 2) {
            mStatusLoop = 0;
            showToast("No Loop");
        }
        mServiceCallback.onUpdate();

//        int orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
//            mServiceCallbackMediaPlaybackFragment.onUpdate();
//        }
    }

    public String getTotalTime() {
        SimpleDateFormat formatTimeSong = new SimpleDateFormat("mm:ss");
        Log.d("aa", "getTotalTime: ");
        return formatTimeSong.format(mMediaPlayer.getDuration());
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public void setSeekTo(int seekProgress) {
        mMediaPlayer.seekTo(seekProgress);
    }

    public int getCurrentDuration() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void setPreviousExitSong(int id) {
        for (int i = 0; i < mPlayingSongList.size(); i++) {
            if (mPlayingSongList.get(i).getId() == id) {
                mPLayingSong = mPlayingSongList.get(i);
            }
        }
    }

    void listenChangeStatus(IServiceCallback callbackService) {
        this.mServiceCallback = callbackService;
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("SONG_ID", mPLayingSong.getId());
        Gson gson = new Gson();
        String json = gson.toJson(mPlayingSongList);
        editor.putString("SONG_LIST", json);
        editor.apply();
    }

    public void loadData() {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("SONG_LIST", null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        mPlayingSongList = gson.fromJson(json, type);
        if (mPlayingSongList == null) {
            mPlayingSongList = new ArrayList<>();
        }
        setPreviousExitSong(mSharedPreferences.getInt("SONG_ID", 0));
    }

    // class
    public class MediaPlaybackServiceBinder extends Binder {
        public MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    //interface
    interface IServiceCallback {
        void onUpdate();
    }

    interface IServiceCallbackMediaPlaybackFragment {
        void onUpdate();
    }

//    public void mediaPlaybackFragmentListenChangeStatus(IServiceCallbackMediaPlaybackFragment serviceCallbackMediaPlaybackFragment) {
//        this.mServiceCallbackMediaPlaybackFragment = serviceCallbackMediaPlaybackFragment;
//    }
}