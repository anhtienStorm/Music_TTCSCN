package com.example.activitymusic;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MediaPlaybackFragment extends Fragment {

    ImageView btImgLike, btImgPrevious, btImgPlay, btImgNext, btImgDislike, btImgLoop, btImgShuffle, imgSongSmall, imgSong, btImgListSong;
    SeekBar seekBarSong;
    TextView tvNameSong, tvArtist, tvTotalTimeSong, tvTimeSong;
    MediaPlaybackService mMediaPlaybackService;
    boolean mCheckService = false;
    AllSongsProvider mAllSongsProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllSongsProvider = new AllSongsProvider(getContext());

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getMusicActivity().mMediaPlaybackService != null) {
            mMediaPlaybackService = getMusicActivity().mMediaPlaybackService;
            mCheckService = true;
        }

        getMusicActivity().setServiceConnectListenner2(new ActivityMusic.IServiceConnectListenner2() {
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
                if (loadFavoriteStatus(mMediaPlaybackService.getId()) == 2) {
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
                if (loadFavoriteStatus(mMediaPlaybackService.getId()) == 1) {
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

    protected ActivityMusic getMusicActivity() {
        if (getActivity() instanceof ActivityMusic) {
            return (ActivityMusic) getActivity();
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
            if (mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()) == null) {
                imgSongSmall.setImageResource(R.drawable.icon_default_song);
                imgSong.setImageResource(R.drawable.icon_default_song);
            } else {
                imgSong.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
                imgSongSmall.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
            }

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
        if (loadFavoriteStatus(mMediaPlaybackService.getId()) == 2) {
            btImgLike.setImageResource(R.drawable.ic_liked_black_24dp);
            btImgDislike.setImageResource(R.drawable.ic_dislike);
        } else if (loadFavoriteStatus(mMediaPlaybackService.getId()) == 1) {
            btImgDislike.setImageResource(R.drawable.ic_disliked_black_24dp);
            btImgLike.setImageResource(R.drawable.ic_like);
        } else {
            btImgLike.setImageResource(R.drawable.ic_like);
            btImgDislike.setImageResource(R.drawable.ic_dislike);
        }
    }

    public void updateSaveSong() {
        if (mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()) == null) {
            imgSongSmall.setImageResource(R.drawable.icon_default_song);
            imgSong.setImageResource(R.drawable.icon_default_song);
        } else {
            imgSong.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
            imgSongSmall.setImageBitmap(mAllSongsProvider.getBitmapAlbumArt(mMediaPlaybackService.getAlbumID()));
        }

        tvNameSong.setText(mMediaPlaybackService.getNameSong());
        tvArtist.setText(mMediaPlaybackService.getArtist());
        if (loadFavoriteStatus(mMediaPlaybackService.getId()) == 2) {
            btImgLike.setImageResource(R.drawable.ic_liked_black_24dp);
            btImgDislike.setImageResource(R.drawable.ic_dislike);
        } else if (loadFavoriteStatus(mMediaPlaybackService.getId()) == 1) {
            btImgDislike.setImageResource(R.drawable.ic_disliked_black_24dp);
            btImgLike.setImageResource(R.drawable.ic_like);
        } else {
            btImgLike.setImageResource(R.drawable.ic_like);
            btImgDislike.setImageResource(R.drawable.ic_dislike);
        }
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

    public int loadFavoriteStatus(int id) {
        int isFavorite = 0;
        Cursor c = getActivity().getContentResolver().query(FavoriteSongsProvider.CONTENT_URI, null, FavoriteSongsProvider.ID_PROVIDER + " = " + id, null, null);
        if (c.moveToFirst()) {
            do {
                isFavorite = Integer.parseInt(c.getString(c.getColumnIndex(FavoriteSongsProvider.IS_FAVORITE)));
            } while (c.moveToNext());
        }
        return isFavorite;
    }
}
