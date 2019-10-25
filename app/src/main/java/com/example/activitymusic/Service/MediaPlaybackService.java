package com.example.activitymusic.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.activitymusic.Provider.FavoriteSongsProvider;
import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Model.Song;
import com.example.activitymusic.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;


public class MediaPlaybackService extends Service {
    public static final String CHANNEL_ID = "MusicServiceChannel";
    private MediaPlayer mMediaPlayer = null;
    private final Binder mBinder = new MediaPlaybackServiceBinder();
    private ArrayList<Song> mPlayingSongList;
    private Song mPLayingSong;
    private int mIndexofPlayingSong;
    private IServiceCallback mServiceCallback;
    private int mLoopStatus = 0;
    private int mShuffle = 0;
    private SharedPreferences mSharedPreferences;
    private String sharePrefFile = "SongSharedPreferences";

    private AudioManager mAudioManager;
    private AudioAttributes mAudioAttributes = null;
    private AudioFocusRequest mAudioFocusRequest = null;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AUDIOFOCUS_LOSS_TRANSIENT:
                    pause();
                    break;
                case AUDIOFOCUS_LOSS:
                    pause();
                    break;
            }
        }
    };
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private int currentTimeTimer = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mHeadsetPlugReceiver = new HeadsetPlugReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel musicServiceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "AllSongsProvider Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            musicServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            musicServiceChannel.enableVibration(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(musicServiceChannel);
        }
        mSharedPreferences = getSharedPreferences(sharePrefFile, MODE_PRIVATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);

        this.registerReceiver(mHeadsetPlugReceiver, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
        Intent notificationIntent = new Intent(this, MainActivityMusic.class);
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

        Bitmap bitmap = loadImageFromPath(getPathSong());

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_notification)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(getNameSong())
                .setContentText(getArtist())
                .setLargeIcon(bitmap == null ? BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_song) : bitmap)
                .addAction(R.drawable.ic_skip_previous_black_24dp, "previous", previousPendingIntent)
                .addAction(isPlaying() ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_black_24dp, "play", playPendingIntent)
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
        unregisterReceiver(mHeadsetPlugReceiver);
        getMediaPlayer().stop();
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

    public String getPathSong() {
        return mPLayingSong.getPathSong();
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

    public int getmLoopStatus() {
        return mLoopStatus;
    }

    public int getmShuffle() {
        return mShuffle;
    }

    public int getCurrentTimeTimer() {
        return currentTimeTimer;
    }

    public void setCurrentTimeTimer(int time) {
        currentTimeTimer = time;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public boolean isPlaying() {
        if (mMediaPlayer.isPlaying())
            return true;
        else
            return false;
    }

    public void play() {
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mAudioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(mAudioAttributes)
                    .setOnAudioFocusChangeListener(mAudioFocusChangeListener)
                    .build();
            int focusRequest = mAudioManager.requestAudioFocus(mAudioFocusRequest);
            if (focusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mMediaPlayer.start();
                showNotification();
                mServiceCallback.onUpdate();
            }
        }
    }

    public void pause() {
        mMediaPlayer.pause();
        showNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH);
        }
        mServiceCallback.onUpdate();
    }

    public void stop() {
        mMediaPlayer.stop();
        showNotification();
        mServiceCallback.onUpdate();
    }

    public void preparePlay() {
        Uri uri = Uri.parse(mPLayingSong.getPathSong());

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        }

        try {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mMediaPlayer == null) {
            showToast("\t\t\t\tSong not exist !\nPlease chose different Song");
            if (mLoopStatus == 0) {
                nextSongNoloop();
            } else if (mLoopStatus == 1) {
                nextSong();
            }
        } else {
            mIndexofPlayingSong = mPlayingSongList.indexOf(mPLayingSong);
            play();

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (mLoopStatus == 0) {
                        nextSongNoloop();
                    } else if (mLoopStatus == 1) {
                        nextSong();
                    } else {
                        playSong(mPlayingSongList, mPLayingSong);
                    }
                }
            });
            saveData();
        }
    }

    public void playSong(final ArrayList<Song> listSong, final Song song) {
        this.mPlayingSongList = listSong;
        this.mPLayingSong = song;
        preparePlay();
    }

    public void playSongOnline(String url) {
        Log.d("Play", "playSongOnline: ");
        new PLaySong().execute(url);
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
            if (getCurrentDuration() > 3000) {
                preparePlay();
            } else {
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
        saveData();
    }

    public void loopSong() {
        if (mLoopStatus == 0) {
            mLoopStatus = 1;
            showToast("Loop List");
        } else if (mLoopStatus == 1) {
            mLoopStatus = 2;
            showToast("Loop One");
        } else if (mLoopStatus == 2) {
            mLoopStatus = 0;
            showToast("No Loop");
        }
        mServiceCallback.onUpdate();
        saveData();
    }

    public String getTotalTime() {
        SimpleDateFormat formatTimeSong = new SimpleDateFormat("mm:ss");
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

    public void listenChangeStatus(IServiceCallback callbackService) {
        this.mServiceCallback = callbackService;
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public int loadFavoriteStatus(int id) {
        int isFavorite = 0;
        Cursor c = getApplicationContext().getContentResolver().query(FavoriteSongsProvider.CONTENT_URI, null, FavoriteSongsProvider.ID_PROVIDER + " = " + id, null, null);
        if (c.moveToFirst()) {
            do {
                isFavorite = Integer.parseInt(c.getString(c.getColumnIndex(FavoriteSongsProvider.IS_FAVORITE)));
            } while (c.moveToNext());
        }
        return isFavorite;
    }

    private void saveData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("SONG_ID", mPLayingSong.getId());
        Gson gson = new Gson();
        String json = gson.toJson(mPlayingSongList);
        editor.putString("SONG_LIST", json);
        editor.putInt("LoopStatus", mLoopStatus);
        editor.putInt("ShuffleStatus", mShuffle);
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
        mLoopStatus = mSharedPreferences.getInt("LoopStatus", 0);
        mShuffle = mSharedPreferences.getInt("ShuffleStatus", 0);
    }

    // class
    public class MediaPlaybackServiceBinder extends Binder {
        public MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    public class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();

            if (intentAction != null) {
                String toastMessage = "null";
                switch (intentAction) {
                    case Intent.ACTION_HEADSET_PLUG:
                        if (isMusicPlay()) {
                            switch (intent.getIntExtra("state", -1)) {
                                case 0:
                                    pause();
                                    toastMessage = "headphone disconnected";
                                    break;
                                case 1:
                                    play();
                                    toastMessage = "headphone connected";
                                    break;
                            }
                        }
                        break;
                }
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //interface
    public interface IServiceCallback {
        void onUpdate();
    }


    class PLaySong extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }

        @Override
        protected void onPostExecute(String baihat) {
            super.onPostExecute(baihat);
            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mMediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mMediaPlayer.setDataSource(baihat);
                mMediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaPlayer.start();


        }
    }
}