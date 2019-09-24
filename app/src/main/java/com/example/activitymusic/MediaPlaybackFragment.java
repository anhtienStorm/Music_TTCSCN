package com.example.activitymusic;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class MediaPlaybackFragment extends Fragment implements ActivityMusic.ICallbackFragmentServiceConnection {

    ConstraintLayout layoutMediaPlaybackFragment;
    ImageView btImgLike, btImgPrevious, btImgPlay, btImgNext, btImgDislike, btImgLoop, btImgShuffle, imgSongSmall, imgSong, btImgListSong;
    SeekBar seekBarSong;
    TextView tvNameSong, tvArtist, tvTotalTimeSong, tvTimeSong;
    MediaPlaybackService mMediaPlaybackService;
    boolean mCheckService = false;
    AllSongsProvider mAllSongsProvider;
//    ActivityMusic mActivityMusic;

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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mCheckService = false;
        }
    };

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mActivityMusic = (ActivityMusic) getContext();
//        mActivityMusic.registerClientFragment(this);
//    }


    @Override
    public void onStart() {
        super.onStart();
        Intent it = new Intent(getContext(), MediaPlaybackService.class);
        getActivity().bindService(it, mServiceConnection, 0);
        if (mCheckService) {
            update();
            mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
                @Override
                public void onSelect() {
                    update();
                }
            });
        }
        mAllSongsProvider = new AllSongsProvider(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_playback_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        initView(view);
        //update();
//        mMediaPlaybackService.onChangeStatus(new MediaPlaybackService.ICallbackService() {
//            @Override
//            public void onSelect() {
//                update();
//            }
//        });

        btImgPlay.setOnClickListener(new Button.OnClickListener() {
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
        layoutMediaPlaybackFragment = view.findViewById(R.id.media_playback_fragment);
    }

    @Override
    public void service(MediaPlaybackService service) {
        mMediaPlaybackService = service;
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
//            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//            Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mMediaPlaybackService.getAlbumID()));
//            Glide.with(this).load(uri).error(R.drawable.ic_default_error_song).into(imgSong);
//            Glide.with(this).load(uri).error(R.drawable.icon_default_song).into(imgSongSmall);

            if (mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()) == null) {
                imgSongSmall.setImageResource(R.drawable.icon_default_song);
                imgSong.setImageResource(R.drawable.icon_default_song);
            } else {
                imgSong.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
                imgSongSmall.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
            }

//            if (String.valueOf(uri).equals("content://media/external/audio/albumart/16")){
//                layoutMediaPlaybackFragment.setBackgroundResource(R.drawable.ic_default_error_song);
//                imgSongSmall.setImageResource(R.drawable.ic_default_error_song);
//            } else {
//                Bitmap imgBitmap = mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID());
//                Drawable imgDrawable = new BitmapDrawable(imgBitmap);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    layoutMediaPlaybackFragment.setBackground(imgDrawable);
//                }
//                imgSongSmall.setImageURI(uri);
//            }

            tvNameSong.setText(mMediaPlaybackService.getNameSong());
            tvArtist.setText(mMediaPlaybackService.getArtist());
            tvTotalTimeSong.setText(mMediaPlaybackService.getTotalTime());
            seekBarSong.setMax(mMediaPlaybackService.getDuration());
            updateTimeSong();
            if (mMediaPlaybackService.isPlaying()) {
                btImgPlay.setImageResource(R.drawable.ic_pause_circle_filled_orange_24dp);
            } else {
                btImgPlay.setImageResource(R.drawable.ic_play_circle_filled_orange_24dp);
            }
        }
        int loop = mMediaPlaybackService.getmStatusLoop();
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
}
