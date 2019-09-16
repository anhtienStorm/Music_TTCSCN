package com.example.activitymusic;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AllSongsProvider {
    Context mContext;

    public AllSongsProvider(Context context) {
        this.mContext = context;
    }

    public ArrayList<Song> getListSong() {
        ArrayList<Song> listSong = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            musicCursor = contentResolver.query(musicUri, null, null, null, null, null);
        }

        if (musicCursor != null && musicCursor.moveToFirst()) {
            int i = 0;
            int indexTitleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int indexDataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int indexArtistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int indexAlbumIDColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            do {
                String title = musicCursor.getString(indexTitleColumn);
                String data = musicCursor.getString(indexDataColumn);
                String artist = musicCursor.getString(indexArtistColumn);
                String albumID = musicCursor.getString(indexAlbumIDColumn);
//                Bitmap bmImage = getAlbumArt(data);
//                if (bmImage == null) {
//                    bmImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_default_song);
//                }
                Song song = new Song(i, title, data, artist, albumID);
                listSong.add(song);
                i++;
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return listSong;
    }

//    public byte[] getAlbumArt(String path) {
//        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//        mediaMetadataRetriever.setDataSource(path);
//        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
////        if (data != null)
////            return BitmapFactory.decodeByteArray(data, 0, data.length);
////        else
////            return null;
//        return data;
//    }

    public Bitmap getBitmapAlbumArt(String album_id)
    {
        Bitmap bm = null;
        try
        {
            Uri uri = getUriAlbumArt(album_id);

            ParcelFileDescriptor pfd = mContext.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    public Uri getUriAlbumArt(String album_id){
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(album_id));
        return uri;
    }

}
