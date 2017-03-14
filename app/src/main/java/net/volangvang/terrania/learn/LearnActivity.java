package net.volangvang.terrania.learn;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.volangvang.terrania.R;
import net.volangvang.terrania.data.CountryContract;
import net.volangvang.terrania.data.CountryProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LearnActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {
    @BindView(R.id.country_list) RecyclerView countryList;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private String filter = "";
    private CountryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        countryList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new CountryAdapter(this);
        countryList.setAdapter(adapter);
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(R.string.search);
        item.setIcon(R.drawable.ic_search_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView sv = new SearchView(new android.view.ContextThemeWrapper(this, R.style.AppTheme_AppBarOverlay));
        sv.setQueryHint(getString(R.string.hint_search));
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), CountryProvider.CONTENT_URI,
                CountryContract.PROJECTION_LITE, CountryContract.CountryEntry.COLUMN_NAME + " LIKE ?", new String[] {"%" + filter + "%"}, CountryContract.CountryEntry.COLUMN_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter = newText;
        getSupportLoaderManager().restartLoader(1, null, this);
        return true;
    }
}
