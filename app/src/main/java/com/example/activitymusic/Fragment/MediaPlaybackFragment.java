package com.example.activitymusic.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.activitymusic.Provider.FavoriteSongsProvider;
import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Service.MediaPlaybackService;
import com.example.activitymusic.R;
import com.example.activitymusic.Receiver.TimerReceiver;

import java.text.SimpleDateFormat;

public class MediaPlaybackFragment extends Fragment {

    ImageView btImgLike, btImgPrevious, btImgPlay, btImgNext, btImgDislike, btImgLoop, btImgShuffle, imgSongSmall, imgSong, btImgListSong, btImgOptions;
    SeekBar seekBarSong;
    TextView tvNameSong, tvArtist, tvTotalTimeSong, tvTimeSong;
    MediaPlaybackService mMediaPlaybackService;
    boolean mCheckService = false;
    private TextView textViewContentTimer, textViewCurrentTimeTimer, textViewTotalTimeTimer;
    private SeekBar seekBarTimer;

    @Override
    public void onResume() {
        super.onResume();

        if (getMusicActivity().mMediaPlaybackService != null) {
            mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
            mCheckService = true;
        }

        getMusicActivity().setServiceConnectListenner2(new MainActivityMusic.IServiceConnectListenner2() {
            @Override
            public void onConnect() {
                mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
                mCheckService = true;

                if (!mMediaPlaybackService.isMusicPlay()) {
                    if (mMediaPlaybackService.getSharedPreferences().contains("SONG_LIST")) {
                        mMediaPlaybackService.loadData();
                        updateSaveSong();
                    }
                }
            }
        });

        if (mCheckService) {

            update();
            if (!mMediaPlaybackService.isMusicPlay()) {
                if (mMediaPlaybackService.getSharedPreferences().contains("SONG_LIST")) {
                    mMediaPlaybackService.loadData();
                    updateSaveSong();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_playback_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().findViewById(R.id.layoutPlayMusic).setVisibility(View.GONE);

        initView(view);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view.findViewById(R.id.btImgListSong).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.btImgListSong).setVisibility(View.VISIBLE);
        }

        if (mCheckService) {
            update();
        }

        btImgPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.isMusicPlay()) {
                    if (mMediaPlaybackService.isPlaying()) {
                        mMediaPlaybackService.pause();
                    } else if (!mMediaPlaybackService.isPlaying()) {
                        mMediaPlaybackService.play();
                    }
                } else {
                    mMediaPlaybackService.preparePlay();
                }
            }
        });

        btImgNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.isMusicPlay()) {
                    mMediaPlaybackService.nextSong();
                }
            }
        });

        btImgPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.isMusicPlay()) {
                    mMediaPlaybackService.previousSong();
                }
            }
        });

        btImgLoop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlaybackService.loopSong();
            }
        });

        btImgShuffle.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlaybackService.shuffleSong();
            }
        });

        btImgLike.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.loadFavoriteStatus(mMediaPlaybackService.getId()) == 2) {
                    setDefaultFavoriteStatus(mMediaPlaybackService.getId());
                    btImgLike.setImageResource(R.drawable.ic_like);
                    btImgDislike.setImageResource(R.drawable.ic_dislike);
                    Toast.makeText(getActivity(), "Unliked Song", Toast.LENGTH_SHORT).show();
                } else {
                    likeSong(mMediaPlaybackService.getId());
                    btImgLike.setImageResource(R.drawable.ic_liked_black_24dp);
                    btImgDislike.setImageResource(R.drawable.ic_dislike);
                }
            }
        });

        btImgDislike.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlaybackService.loadFavoriteStatus(mMediaPlaybackService.getId()) == 1) {
                    setDefaultFavoriteStatus(mMediaPlaybackService.getId());
                    btImgLike.setImageResource(R.drawable.ic_like);
                    btImgDislike.setImageResource(R.drawable.ic_dislike);
                    Toast.makeText(getActivity(), "Undisliked Song", Toast.LENGTH_SHORT).show();
                } else {
                    dislikeSong(mMediaPlaybackService.getId());
                    btImgDislike.setImageResource(R.drawable.ic_disliked_black_24dp);
                    btImgLike.setImageResource(R.drawable.ic_like);
                }
            }
        });

        btImgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        seekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlaybackService.setSeekTo(seekBar.getProgress());
            }
        });

        btImgListSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    protected MainActivityMusic getMusicActivity() {
        if (getActivity() instanceof MainActivityMusic) {
            return (MainActivityMusic) getActivity();
        }
        return null;
    }

    void initView(View view) {
        btImgLike = view.findViewById(R.id.btImgLike);
        btImgPrevious = view.findViewById(R.id.btImgPrevious);
        btImgPlay = view.findViewById(R.id.btIngPlay);
        btImgNext = view.findViewById(R.id.btImgNext);
        btImgDislike = view.findViewById(R.id.btImgDislike);
        btImgLoop = view.findViewById(R.id.btImgLoop);
        btImgShuffle = view.findViewById(R.id.btImgShuffle);
        seekBarSong = view.findViewById(R.id.seekBarSong);
        tvNameSong = view.findViewById(R.id.tvNameSong);
        tvNameSong.setSelected(true);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvTimeSong = view.findViewById(R.id.tvTime);
        tvTotalTimeSong = view.findViewById(R.id.tvTotalTime);
        imgSongSmall = view.findViewById(R.id.imgSongSmall);
        btImgListSong = view.findViewById(R.id.btImgListSong);
        imgSong = view.findViewById(R.id.imgSong);
        btImgOptions = view.findViewById(R.id.btImgOptionsMediaPlayback);
    }

    public void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat formatTimeSong = new SimpleDateFormat("mm:ss");
                tvTimeSong.setText(formatTimeSong.format(mMediaPlaybackService.getCurrentDuration()));
                seekBarSong.setProgress(mMediaPlaybackService.getCurrentDuration());
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    public void update() {
        if (mMediaPlaybackService.isMusicPlay()) {

            tvTotalTimeSong.setText(mMediaPlaybackService.getTotalTime());
            seekBarSong.setMax(mMediaPlaybackService.getDuration());
            updateTimeSong();
            if (mMediaPlaybackService.isPlaying()) {
                btImgPlay.setImageResource(R.drawable.ic_pause_circle_filled_orange_24dp);
                tvNameSong.setText(mMediaPlaybackService.getNameSong());
                tvArtist.setText(mMediaPlaybackService.getArtist());
                if (mMediaPlaybackService.mIsPlayOnline){
                    Glide.with(getContext()).load(mMediaPlaybackService.getPlayingSongOnline().getIMAGE()).error(R.drawable.icon_default_song).into(imgSong);
                    Glide.with(getContext()).load(mMediaPlaybackService.getPlayingSongOnline().getIMAGE()).error(R.drawable.icon_default_song).into(imgSongSmall);
                } else {
                    if (loadImageFromPath(mMediaPlaybackService.getPathSong()) == null) {
                        imgSongSmall.setImageResource(R.drawable.icon_default_song);
                        imgSong.setImageResource(R.drawable.icon_default_song);
                    } else {
                        imgSong.setImageBitmap(loadImageFromPath(mMediaPlaybackService.getPathSong()));
                        imgSongSmall.setImageBitmap(loadImageFromPath(mMediaPlaybackService.getPathSong()));
                    }

                    if (mMediaPlaybackService.loadFavoriteStatus(mMediaPlaybackService.getId()) == 2) {
                        btImgLike.setImageResource(R.drawable.ic_liked_black_24dp);
                        btImgDislike.setImageResource(R.drawable.ic_dislike);
                    } else if (mMediaPlaybackService.loadFavoriteStatus(mMediaPlaybackService.getId()) == 1) {
                        btImgDislike.setImageResource(R.drawable.ic_disliked_black_24dp);
                        btImgLike.setImageResource(R.drawable.ic_like);
                    } else {
                        btImgLike.setImageResource(R.drawable.ic_like);
                        btImgDislike.setImageResource(R.drawable.ic_dislike);
                    }
                }
            } else {
                btImgPlay.setImageResource(R.drawable.ic_play_circle_filled_orange_24dp);
            }
        }
        int loop = mMediaPlaybackService.getmLoopStatus();
        int shuffle = mMediaPlaybackService.getmShuffle();
        if (loop == 0) {
            btImgLoop.setImageResource(R.drawable.ic_repeat_white_24dp);
        } else if (loop == 1) {
            btImgLoop.setImageResource(R.drawable.ic_repeat_orange_24dp);
        } else {
            btImgLoop.setImageResource(R.drawable.ic_repeat_one_orange_24dp);
        }
        if (shuffle == 0) {
            btImgShuffle.setImageResource(R.drawable.ic_shuffle_white_24dp);
        } else {
            btImgShuffle.setImageResource
                    (R.drawable.ic_shuffle_orange_24dp);
        }
    }

    public void updateSaveSong() {
        if (mMediaPlaybackService.mIsPlayOnline){
            Glide.with(getContext()).load(mMediaPlaybackService.getPlayingSongOnline().getIMAGE()).error(R.drawable.icon_default_song).into(imgSong);
            Glide.with(getContext()).load(mMediaPlaybackService.getPlayingSongOnline().getIMAGE()).error(R.drawable.icon_default_song).into(imgSongSmall);
        } else {
            if (loadImageFromPath(mMediaPlaybackService.getPathSong()) == null) {
                imgSongSmall.setImageResource(R.drawable.icon_default_song);
                imgSong.setImageResource(R.drawable.icon_default_song);
            } else {
                imgSong.setImageBitmap(loadImageFromPath(mMediaPlaybackService.getPathSong()));
                imgSongSmall.setImageBitmap(loadImageFromPath(mMediaPlaybackService.getPathSong()));
            }

            if (mMediaPlaybackService.loadFavoriteStatus(mMediaPlaybackService.getId()) == 2) {
                btImgLike.setImageResource(R.drawable.ic_liked_black_24dp);
                btImgDislike.setImageResource(R.drawable.ic_dislike);
            } else if (mMediaPlaybackService.loadFavoriteStatus(mMediaPlaybackService.getId()) == 1) {
                btImgDislike.setImageResource(R.drawable.ic_disliked_black_24dp);
                btImgLike.setImageResource(R.drawable.ic_like);
            } else {
                btImgLike.setImageResource(R.drawable.ic_like);
                btImgDislike.setImageResource(R.drawable.ic_dislike);
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

    public void likeSong(int id) {
        ContentValues values = new ContentValues();
        values.put(FavoriteSongsProvider.IS_FAVORITE, 2);
        getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, "ID_PROVIDER = " + id, null);
        Toast.makeText(getActivity(), "Liked Song", Toast.LENGTH_SHORT).show();
    }

    public void dislikeSong(int id) {
        ContentValues values = new ContentValues();
        values.put(FavoriteSongsProvider.IS_FAVORITE, 1);
        getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, "ID_PROVIDER = " + id, null);
        Toast.makeText(getActivity(), "Disliked Song", Toast.LENGTH_SHORT).show();
    }

    public void setDefaultFavoriteStatus(int id) {
        ContentValues values = new ContentValues();
        values.put(FavoriteSongsProvider.IS_FAVORITE, 0);
        getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, "ID_PROVIDER = " + id, null);
    }

    public void showDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.switch_off_timer, null);
        seekBarTimer = view.findViewById(R.id.seekbar_timer);
        textViewContentTimer = view.findViewById(R.id.content_timer);
        textViewCurrentTimeTimer = view.findViewById(R.id.current_time_timer);
        textViewTotalTimeTimer = view.findViewById(R.id.total_time_timer);

        seekBarTimer.setMax(120 * 60 * 1000);
        if (mMediaPlaybackService.getCurrentTimeTimer()==0){
            textViewContentTimer.setText("");
        } else {
            textViewContentTimer.setText("Ứng dụng sẽ tự động tắt nhạc sau "+mMediaPlaybackService.getCurrentTimeTimer() / 1000 / 60+" phút");
        }

        final AlertDialog dialogTimer = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Bắt đầu hẹn giờ",null)
                .show();

        final Button positiveButton = dialogTimer.getButton(AlertDialog.BUTTON_POSITIVE);
        if (mMediaPlaybackService.getCurrentTimeTimer()==0){
            positiveButton.setEnabled(false);
        } else {
            positiveButton.setText("Kết thúc hẹn giờ");
            positiveButton.setEnabled(true);
        }

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveButton.getText().equals("Bắt đầu hẹn giờ")){
                    mMediaPlaybackService.setCurrentTimeTimer(seekBarTimer.getProgress());
                    dialogTimer.dismiss();
                    startAlarm();
                } else {
                    mMediaPlaybackService.setCurrentTimeTimer(0);
                    dialogTimer.dismiss();
                }
            }
        });

        seekBarTimer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewCurrentTimeTimer.setText(getCurrentTime(progress));
                if (progress==0){
                    dialogTimer.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialogTimer.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void startAlarm(){
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),1,intent,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+mMediaPlaybackService.getCurrentTimeTimer() ,pendingIntent);
        }
    }

    String getCurrentTime(int miliSecond){
        int minute = miliSecond/1000/60;
        int second = (miliSecond/1000)%60;
        if (second==0){
            return minute+" phút";
        } else {
            return minute+" phút "+second+" giây";
        }
    }
}
