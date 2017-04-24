package net.volangvang.terrania.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

public class CountryProvider extends ContentProvider {
    private static final String AUTHORITY = "net.volangvang.terrania.data.VowelProvider";
    private static final String BASE_PATH = "countries";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final Uri CONTINENT_URI = Uri.parse("content://" + AUTHORITY + "/" + "continent");
    private static final String ITEM_BASE_PATH = "countries/#";
    private static final String CONTINENT_BASE_PATH = "continent/*";
    public static final int COUNTRIES = 100;
    public static final int COUNTRY = 300;
    public static final int CONTINENT = 500;
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/terrania-country";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, COUNTRIES);
        uriMatcher.addURI(AUTHORITY, ITEM_BASE_PATH, COUNTRY);
        uriMatcher.addURI(AUTHORITY, CONTINENT_BASE_PATH, CONTINENT);
    }

    private CountryDatabase database;

    public CountryProvider() {
    }

    @Override
    public boolean onCreate() {
        database = new CountryDatabase(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        if (uriMatcher.match(uri) == COUNTRIES || uriMatcher.match(uri) == COUNTRY) return CONTENT_TYPE;
        return "";
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CountryContract.CountryEntry.TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case 100:
                return builder.query(database.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
            case 300:
                return builder.query(database.getReadableDatabase(), projection, "_ID = "  + uri.getLastPathSegment(), selectionArgs, null, null, sortOrder);
            case 500:
                return builder.query(database.getReadableDatabase(), projection, "region = " + uri.getLastPathSegment(), selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
