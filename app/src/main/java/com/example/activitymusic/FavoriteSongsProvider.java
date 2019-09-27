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
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class FavoriteSongsProvider extends ContentProvider {
    static final String AUTHORITY = "com.android.example.provider.FavoriteSongs";
    static final String CONTENT_PATH = "backupdata";
    static final String URL = "content://" + AUTHORITY + "/" + CONTENT_PATH;
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String TITLE = "title";
    static final String DATA = "data";
    static final String ALBUM_ID = "albumID";
    static final String ARTIST = "artist";
    static final String DURATION = "duration";
    static final String FAVORITE = "favorite";
    static final String COUNT = "count";

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
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME_1);

        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                qb.setProjectionMap(STUDENTS_PROJECTION_MAP);
                break;

            case URI_ONE_ITEM_CODE:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == ""){
            sortOrder = TITLE;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case URI_ALL_ITEMS_CODE:
                return "vnd.android.cursor.dir/vnd.example.backupdata";
            case URI_ONE_ITEM_CODE:
                return "vnd.android.cursor.item/vnd.example.backupdata";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
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
        int count = 0;
        switch (uriMatcher.match(uri)){
            case URI_ALL_ITEMS_CODE:
                count = db.delete(TABLE_NAME_1, selection, selectionArgs);
                break;

            case URI_ONE_ITEM_CODE:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TABLE_NAME_1, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                count = db.update(TABLE_NAME_1, values, selection, selectionArgs);
                break;

            case URI_ONE_ITEM_CODE:
                count = db.update(TABLE_NAME_1, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
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
