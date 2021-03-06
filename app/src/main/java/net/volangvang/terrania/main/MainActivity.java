package net.volangvang.terrania.main;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.squareup.picasso.Picasso;

import net.volangvang.terrania.AboutFragment;
import net.volangvang.terrania.R;
import net.volangvang.terrania.learn.LearnActivity;
import net.volangvang.terrania.play.PlayActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_play_now) Button btnPlayNow;
    @BindView(R.id.layout_not_signed_in) View layoutNotSignedIn;
    @BindView(R.id.layout_signed_in) View layoutSignedIn;
    @BindView(R.id.text_signed_in) TextView textSignedIn;
    @BindView(R.id.switch_sound) SwitchCompat switchSound;
    @BindView(R.id.switch_offline) SwitchCompat switchOffline;
    TextView prompt;
    TextView textUserName;
    ImageView userImg;
    ImageView userBanner;
    private GoogleApiClient apiClient;
    private SharedPreferences preferences;
    private static int RC_SIGN_IN = 2948;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();
        View header = navigationView.getHeaderView(0);
        prompt = (TextView) header.findViewById(R.id.login_logout_prompt);
        textUserName = (TextView) header.findViewById(R.id.user_name);
        userImg = (ImageView) header.findViewById(R.id.user_image);
        userBanner = (ImageView) header.findViewById(R.id.user_banner);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        btnPlayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignOutClicked();
            }
        });

        switchSound.setChecked(preferences.getBoolean("sound", false));
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean("sound", b).apply();
            }
        });

        switchOffline.setChecked(preferences.getBoolean("offline", false));
        switchOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean("offline", b).apply();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Do not reconnect if the user signed out explicitly or the user is signing in
        if (!signInFlow && !explicitSignOut) {
            apiClient.connect();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_learn) {
            Intent intent = new Intent(this, LearnActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_play) {
            Intent intent = new Intent(this, PlayActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            DialogFragment dialog = new AboutFragment();
            dialog.show(getSupportFragmentManager(), null);
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private boolean resolvingConnectionFailure = false;
    private boolean autoStartSignInFlow = false;
    private boolean signInClicked = false;
    private boolean explicitSignOut = false;
    boolean signInFlow = false; // in the middle of the sign-in flow


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            signInClicked = false;
            resolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                apiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        layoutNotSignedIn.setVisibility(View.GONE);
        layoutSignedIn.setVisibility(View.VISIBLE);
        prompt.setText(getString(R.string.msg_profile_prompt));
        textUserName.setText(Games.Players.getCurrentPlayer(apiClient).getDisplayName());
        textSignedIn.setText(getString(R.string.msg_signed_in, Games.Players.getCurrentPlayer(apiClient).getDisplayName()));
        // We have to use this to load images.
        ImageManager manager = ImageManager.create(this);
        manager.loadImage(userImg, Games.Players.getCurrentPlayer(apiClient).getIconImageUri());
        manager.loadImage(userBanner, Games.Players.getCurrentPlayer(apiClient).getBannerImageLandscapeUri());
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (resolvingConnectionFailure) return;
        if (signInClicked || autoStartSignInFlow) {
            autoStartSignInFlow = false;
            signInClicked = false;
            resolvingConnectionFailure = true;
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                }
                catch (IntentSender.SendIntentException e) {
                    apiClient.connect();
                }
            }
            else {
                resolvingConnectionFailure = false;
                Toast.makeText(this, R.string.msg_sign_in_error, Toast.LENGTH_SHORT).show();
            }
        }
        showSignInButton();
    }

    private void showSignInButton() {
        // Display the sign-in button
        layoutNotSignedIn.setVisibility(View.VISIBLE);
        layoutSignedIn.setVisibility(View.GONE);
        prompt.setText(R.string.msg_not_signed_in);
        textUserName.setText(R.string.app_name);
        Picasso.with(getApplicationContext())
                .load(R.mipmap.ic_launcher)
                .into(userImg);
        Picasso.with(getApplicationContext())
                .load(R.drawable.bg_nav)
                .into(userBanner);
    }


    @OnClick (R.id.btn_sign_in)
    public void onSignInClicked() {
        signInClicked = true;
        apiClient.connect();
    }

    @OnClick (R.id.btn_sign_out)
    public void onSignOutClicked() {
        explicitSignOut = true; // turn off automatic sign-in
        if (apiClient != null && apiClient.isConnected()) {
            Games.signOut(apiClient);
            apiClient.disconnect();
        }
        showSignInButton();
    }

    @OnClick (R.id.btn_achievements)
    public void onAchievementsClicked() {
        startActivityForResult(Games.Achievements.getAchievementsIntent(apiClient),
                5312);
    }

    @OnClick (R.id.btn_leaderboards)
    public void onLeaderboardsClicked() {
        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(apiClient), 2391);
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiClient.disconnect();
    }
}
