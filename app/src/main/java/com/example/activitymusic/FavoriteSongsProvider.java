package com.example.activitymusic;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.HashMap;

public class FavoriteSongsProvider extends ContentProvider {
    static final String AUTHORITY = "com.android.example.FavoriteSongsProvider";
    static final String CONTENT_PATH = "backupdata";
    static final String URL = "content://" + AUTHORITY + "/" + CONTENT_PATH;
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String _TITLE = "_title";
    static final String _DATA = "_data";
    static final String _ALBUM_ID = "_albumID";
    static final String _ARTIST = "_artist";
    static final String _FAVORITE = "_favorite";
    static final String _COUNT = "_count";

    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

    static final int URI_ALL_ITEMS_CODE = 1;
    static final int URI_ONE_ITEM_CODE = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, URI_ALL_ITEMS_CODE);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", URI_ONE_ITEM_CODE);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "FavoriteSongsDatabase";
    static final String TABLE_NAME_1 = "InfomationSongs";
    static final String TABLE_NAME_2 = "InfomationFavoriteSongs";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE_INFORMATION_SONG =
            " CREATE TABLE " + TABLE_NAME_1 +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " title TEXT," +
                    " data TEXT," +
                    " artist TEXT," +
                    " albumid TEXT," +
                    " duration TEXT);";
    static final String CREATE_DB_TABLE_INFORMATION_FAVORITE_SONG =
            " CREATE TABLE " + TABLE_NAME_2 +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " favorite INTEGER," +
                    " count INTEGER);";


    @Override
    public boolean onCreate() {
        Context context = getContext();
        FavoriteSongsDatabase dbFavoriteSong = new FavoriteSongsDatabase(context);
        db = dbFavoriteSong.getWritableDatabase();
        return db == null ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(TABLE_NAME_1, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


    private static class FavoriteSongsDatabase extends SQLiteOpenHelper {
        public FavoriteSongsDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE_INFORMATION_SONG);
            db.execSQL(CREATE_DB_TABLE_INFORMATION_FAVORITE_SONG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
            onCreate(db);
        }
    }
}
