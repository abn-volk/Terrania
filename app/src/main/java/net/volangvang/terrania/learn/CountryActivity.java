package net.volangvang.terrania.learn;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.volangvang.terrania.R;
import net.volangvang.terrania.data.CountryContract;
import net.volangvang.terrania.data.CountryProvider;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar_flag) ImageView flag;
    @BindView(R.id.info_capital) TextView infoCapital;
    @BindView(R.id.info_area) TextView infoArea;
    @BindView(R.id.info_population) TextView infoPopulation;
    @BindView(R.id.info_coastline) TextView infoCoastline;
    @BindView(R.id.info_currency) TextView infoCurrency;
    @BindView(R.id.info_dialling_prefix) TextView infoDiallingPrefix;
    @BindView(R.id.info_birth_rate) TextView infoBirthRate;
    @BindView(R.id.info_death_rate) TextView infoDeathRate;
    @BindView(R.id.info_life_expectancy) TextView infoLifeExpectancy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        int id = getIntent().getIntExtra("id", 1);
        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(CountryProvider.CONTENT_URI, Integer.toString(id)),
                null, null, null, null);
        if (cursor == null || cursor.getCount() < 1) {
            finish();
        }
        else {
            cursor.moveToFirst();
            int countryColumn = cursor.getColumnIndex(Locale.getDefault().getLanguage().equals("vi") ?
                    CountryContract.CountryEntry.COLUMN_NAME_VI : CountryContract.CountryEntry.COLUMN_NAME);
            int capitalColumn = cursor.getColumnIndex(Locale.getDefault().getLanguage().equals("vi") ?
                    CountryContract.CountryEntry.COLUMN_CAPITAL_VI : CountryContract.CountryEntry.COLUMN_CAPITAL);
            setTitle(cursor.getString(countryColumn));
            String countryCode = cursor.getString(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_COUNTRY_CODE));
            String capital = cursor.getString(capitalColumn);
            final float latitude = cursor.getFloat(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_LATITUDE));
            final float longitude = cursor.getFloat(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_LONGITUDE));
            int population = cursor.getInt(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_POPULATION));
            int area = cursor.getInt(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_AREA));
            int coastline = cursor.getInt(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_COASTLINE));
            String currency = cursor.getString(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_CURRENCY));
            int diallingPrefix = cursor.getInt(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_DIALLING_PREFIX));
            float birthRate = cursor.getFloat(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_BIRTH_RATE));
            float deathRate = cursor.getFloat(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_DEATH_RATE));
            float lifeExpectancy = cursor.getFloat(cursor.getColumnIndex(CountryContract.CountryEntry.COLUMN_LIFE_EXPECTANCY));
            cursor.close();
            Picasso.with(this)
                    .load(getResources().getIdentifier("country_" + countryCode.toLowerCase(), "drawable", getPackageName()))
                    .into(flag);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(String.format(Locale.US, "geo:%f,%f?z=8", latitude, longitude));
                    intent.setData(uri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    else {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        Uri uri1 = Uri.parse(String.format(Locale.US, "http://maps.google.com/?q=%f,%f", latitude, longitude));
                        intent1.setData(uri1);
                        if (intent1.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        else Toast.makeText(getApplicationContext(), R.string.no_maps_or_browser, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            infoCapital.setText(getString(R.string.info_capital, capital));
            infoArea.setText(getString(R.string.info_area, area));
            infoPopulation.setText(getString(R.string.info_population, population));
            infoCoastline.setText(getString(R.string.info_coastline, coastline));
            infoCurrency.setText(getString(R.string.info_currency, currency));
            infoDiallingPrefix.setText(getString(R.string.info_dialling_prefix, diallingPrefix));
            infoBirthRate.setText(getString(R.string.info_birth_rate, birthRate));
            infoDeathRate.setText(getString(R.string.info_death_rate, deathRate));
            infoLifeExpectancy.setText(getString(R.string.info_life_expectancy, lifeExpectancy));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
