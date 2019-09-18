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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;


public class MediaPlaybackService extends Service {
    public static final String CHANNEL_ID = "MusicServiceChannel";
    private MediaPlayer mMediaPlayer = null;
    private final Binder mBinder = new MediaPlaybackServiceBinder();
    private ArrayList<Song> mListSong;
    private int mPosition;
    private ICallbackService mCallbackService;
    private int mStatusLoop = 0;
    private int mShuffle = 0;
    private AllSongsProvider mAllSongsProvider;
    private SharedPreferences mPreferences;
    private String sharePrefFile = "SongSharedPreferences";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel musicServiceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "AllSongsProvider Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            musicServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(musicServiceChannel);
        }
        mAllSongsProvider = new AllSongsProvider(getApplicationContext());
        mPreferences = getSharedPreferences(sharePrefFile, MODE_PRIVATE);
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
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle(getNameSong())
                .setContentText(getArtist())
                .setLargeIcon(bitmap == null ? BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_song) : bitmap)
                .setPriority(2)
                .addAction(R.drawable.ic_skip_previous_black_24dp, "previous", previousPendingIntent)
                .addAction(isPlaying() ? R.drawable.ic_pause_circle_filled_orange_24dp : R.drawable.ic_play_circle_filled_orange_24dp, "play", playPendingIntent)
                .addAction(R.drawable.ic_skip_next_black_24dp, "next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
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
        return mListSong.get(mPosition).getNameSong();
    }

    public String getArtist() {
        return mListSong.get(mPosition).getSinger();
    }

    public String getAlbumID() {
        return mListSong.get(mPosition).getAlbumID();
    }

//    public Bitmap getBitmapImage(){
//        return mListSong.get(mPosition).getBmImageSong();
//    }

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
        mCallbackService.onSelect();
    }

    public void pause() {
        mMediaPlayer.pause();
        showNotification();
        mCallbackService.onSelect();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH);
        }
    }

    public void stop() {
        mMediaPlayer.stop();
        showNotification();
        mCallbackService.onSelect();
    }

    private void preparePlay() {
        Uri uri = Uri.parse(mListSong.get(mPosition).getPathSong());
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        }

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mMediaPlayer.start();
        showNotification();
        mCallbackService.onSelect();

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mStatusLoop == 0) {
                    nextSong();
                } else if (mStatusLoop == 1) {
                    nextSong();
                } else {
                    playSong(mListSong, mPosition);
                }
            }
        });

//        SharedPreferences.Editor prefEditor = mPreferences.edit();
//        prefEditor.putInt("ID_Song",mListSong.get(mPosition).getId());
//        prefEditor.apply();
    }

    public void playSong(final ArrayList<Song> listSong, final int position) {
        this.mListSong = listSong;
        this.mPosition = position;
        preparePlay();
    }

    public void nextSong() {
        if (isMusicPlay()) {
            if (mShuffle == 0) {
                if (mPosition == mListSong.size() - 1) {
                    mPosition = 0;
                } else {
                    mPosition += 1;
                }
            } else {
                Random rd = new Random();
                mPosition = rd.nextInt(mListSong.size());
            }
            preparePlay();
        }
    }

    public void previousSong() {
        if (isMusicPlay()) {
            if (mShuffle == 0) {
                if (mPosition == 0) {
                    mPosition = mListSong.size() - 1;
                } else {
                    mPosition -= 1;
                }
            } else {
                Random rd = new Random();
                mPosition = rd.nextInt(mListSong.size());
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
        mCallbackService.onSelect();
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
        mCallbackService.onSelect();

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

    void onChangeStatus(ICallbackService callbackService) {
        this.mCallbackService = callbackService;
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // class
    public class MediaPlaybackServiceBinder extends Binder {
        public MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }

    }

    //interface
    interface ICallbackService {
        void onSelect();
    }
}