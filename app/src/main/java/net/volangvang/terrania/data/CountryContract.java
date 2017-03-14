package net.volangvang.terrania.data;

import android.provider.BaseColumns;

import static android.provider.BaseColumns._ID;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_AREA;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_BIRTH_RATE;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_CAPITAL;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_COASTLINE;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_COUNTRY_CODE;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_CURRENCY;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_CURRENCY_CODE;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_DEATH_RATE;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_DIALLING_PREFIX;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_LATITUDE;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_LIFE_EXPECTANCY;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_LONGITUDE;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_NAME;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_POPULATION;
import static net.volangvang.terrania.data.CountryContract.CountryEntry.COLUMN_REGION;

public final class CountryContract {
    private CountryContract(){};
    public static abstract class CountryEntry implements BaseColumns {
        public static final String TABLE_NAME = "countries";
        public static final String COLUMN_NAME = "country";
        public static final String COLUMN_REGION = "region";
        public static final String COLUMN_COUNTRY_CODE = "country_code";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_CAPITAL = "capital";
        public static final String COLUMN_POPULATION = "population";
        public static final String COLUMN_AREA = "area";
        public static final String COLUMN_COASTLINE = "coastline";
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_CURRENCY_CODE = "currency_code";
        public static final String COLUMN_DIALLING_PREFIX = "dialling_prefix";
        public static final String COLUMN_BIRTH_RATE = "birth_rate";
        public static final String COLUMN_DEATH_RATE = "death_rate";
        public static final String COLUMN_LIFE_EXPECTANCY = "life_expectancy";
    }

    public static final String[] PROJECTION = {_ID, COLUMN_NAME, COLUMN_REGION, COLUMN_COUNTRY_CODE,
    COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_CAPITAL, COLUMN_POPULATION, COLUMN_AREA,
    COLUMN_COASTLINE, COLUMN_CURRENCY, COLUMN_CURRENCY_CODE, COLUMN_DIALLING_PREFIX,
    COLUMN_BIRTH_RATE, COLUMN_DEATH_RATE, COLUMN_LIFE_EXPECTANCY};
    public static final String[] PROJECTION_LITE = {_ID, COLUMN_NAME, COLUMN_COUNTRY_CODE};
}
