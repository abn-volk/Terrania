package net.volangvang.terrania.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class CountryDatabase extends SQLiteAssetHelper {
    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;

    public CountryDatabase(Context context, String name, String storageDirectory, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, storageDirectory, factory, version);
        setForcedUpgrade();
    }

    public CountryDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


}
