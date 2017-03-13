package net.volangvang.terrania;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.play_view_pager)
    ViewPager viewPager;
    private String[] gameModes = {};
    private String[] gameModeValues = {};
    private String[] gameModeDescriptions = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gameModes = getResources().getStringArray(R.array.game_modes);
        gameModeValues = getResources().getStringArray(R.array.game_values);
        gameModeDescriptions = getResources().getStringArray(R.array.desc_game_modes);
        viewPager.setAdapter(new GamePagerAdapter(getSupportFragmentManager()));
    }

    private class GamePagerAdapter extends FragmentPagerAdapter {

        public GamePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GameModeFragment.newInstance(gameModes[position], gameModeValues[position], gameModeDescriptions[position]);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }



}
