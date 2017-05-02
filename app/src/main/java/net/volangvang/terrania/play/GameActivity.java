package net.volangvang.terrania.play;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import net.volangvang.terrania.R;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;
import net.volangvang.terrania.play.server.LocalServer;
import net.volangvang.terrania.play.server.Server;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_COUNT = "count";
    public static final String EXTRA_CONTINENT = "continent";
    public static final String TAG = "server_fragment";
    @BindView(R.id.fragment_holder)
    FrameLayout holder;
    private String mode;
    private ServerFragment fragment;
    private Server server;
    private String id = "";
    private String continent;
    private boolean completed = false;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();
        googleApiClient.connect();

        fragment = (ServerFragment) getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            Intent intent = getIntent();
            mode = intent.getStringExtra(EXTRA_MODE);
            int count = intent.getIntExtra(EXTRA_COUNT, 0);
            continent = intent.getStringExtra(EXTRA_CONTINENT);
            if (mode == null || continent == null) {
                Toast.makeText(getApplicationContext(), "Invalid extras", Toast.LENGTH_SHORT).show();
                finish();
            }
            Server svr = new LocalServer(getApplicationContext());
            fragment = new ServerFragment();
            fragment.setServer(svr);
            fragment.setMode(mode);
            fragment.setContinent(continent);
            server = fragment.getServer();
            getSupportFragmentManager().beginTransaction().add(fragment, TAG).commit();
            String language = (Locale.getDefault().getLanguage().equals("vi"))? "vn" : "en";
            Pair<Server.Status, String> response = server.newGame(mode, continent, language, count);
            if (response.first == Server.Status.OK) {
                id = response.second;
                fragment.setGameID(id);
                nextQuestion();
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else {
            id = fragment.getGameID();
            server = fragment.getServer();
            mode = fragment.getMode();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public int answer(int index) {
        // Returns the index of the right answer or -1 if an error has occurred.
        Pair<Server.Status, UserAnswer> response = server.answerQuestion(new UserAnswer(index));
        if (response.first == Server.Status.OK) {
            if (response.second.getAnswer() == index) {
                fragment.bumpScore();
                if (googleApiClient != null && googleApiClient.isConnected()) {
                    Games.Achievements.increment(googleApiClient, getString(R.string.achievement_a_thousand_miles), 1);
                }
            }
            else {
                if (googleApiClient != null && googleApiClient.isConnected()) {
                    // Baby steps - first wrong answer
                    Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_baby_steps));
                }
            }
            return response.second.getAnswer();
        }
        else {
            return -1;
        }
    }

    public int nextQuestion() {
        Pair<Server.Status, Question> response = server.getQuestion(id);
        if (response.first == Server.Status.ERROR) {
            Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
            return -1;
        }
        else if (response.first == Server.Status.COMPLETED) {
            completed = true;
            if (googleApiClient != null && googleApiClient.isConnected()) {
                // Unlock First timer - Play the first game
                Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_first_timer));
                if (fragment.getScore() == 195 && fragment.getMode().contains("flag")) {
                    // Vexillologist - perfect score on flags
                    Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_vexillologist));
                }
                Games.Achievements.increment(googleApiClient, getString(R.string.achievement_frequent_flyer), 1);
                Games.Achievements.increment(googleApiClient, getString(R.string.achievement_obsessed), 1);
            }
            int score = fragment.getScore();
            int leaderboardId = -1;
            switch (fragment.getMode()) {
                case "country2flag":
                    switch (fragment.getContinent()) {
                        case "Africa":
                            leaderboardId = R.string.leaderboard_country_to_flag_africa;
                            break;
                        case "America":
                            leaderboardId = R.string.leaderboard_country_to_flag_america;
                            break;
                        case "Asia":
                            leaderboardId = R.string.leaderboard_country_to_flag_asia;
                            break;
                        case "Europe":
                            leaderboardId = R.string.leaderboard_country_to_flag_europe;
                            break;
                        case "Oceania":
                            leaderboardId = R.string.leaderboard_country_to_flag_oceania;
                            break;
                        case "World":
                            leaderboardId = R.string.leaderboard_country_to_flag_world;
                            break;
                    }
                    break;
                case "flag2country":
                    switch (fragment.getContinent()) {
                        case "Africa":
                            leaderboardId = R.string.leaderboard_flag_to_country_africa;
                            break;
                        case "America":
                            leaderboardId = R.string.leaderboard_flag_to_country_america;
                            break;
                        case "Asia":
                            leaderboardId = R.string.leaderboard_flag_to_country_asia;
                            break;
                        case "Europe":
                            leaderboardId = R.string.leaderboard_flag_to_country_europe;
                            break;
                        case "Oceania":
                            leaderboardId = R.string.leaderboard_flag_to_country_oceania;
                            break;
                        case "World":
                            leaderboardId = R.string.leaderboard_flag_to_country_world;
                            break;
                    }
                    break;
                case "country2capital":
                    switch (fragment.getContinent()) {
                        case "Africa":
                            leaderboardId = R.string.leaderboard_country_to_capital_africa;
                            break;
                        case "America":
                            leaderboardId = R.string.leaderboard_country_to_capital_america;
                            break;
                        case "Asia":
                            leaderboardId = R.string.leaderboard_country_to_capital_asia;
                            break;
                        case "Europe":
                            leaderboardId = R.string.leaderboard_country_to_capital_europe;
                            break;
                        case "Oceania":
                            leaderboardId = R.string.leaderboard_country_to_capital_oceania;
                            break;
                        case "World":
                            leaderboardId = R.string.leaderboard_country_to_capital_world;
                            break;
                    }
                    break;
                case "capital2country":
                    switch (fragment.getContinent()) {
                        case "Africa":
                            leaderboardId = R.string.leaderboard_capital_to_country_africa;
                            break;
                        case "America":
                            leaderboardId = R.string.leaderboard_capital_to_country_america;
                            break;
                        case "Asia":
                            leaderboardId = R.string.leaderboard_capital_to_country_asia;
                            break;
                        case "Europe":
                            leaderboardId = R.string.leaderboard_capital_to_country_europe;
                            break;
                        case "Oceania":
                            leaderboardId = R.string.leaderboard_capital_to_country_oceania;
                            break;
                        case "World":
                            leaderboardId = R.string.leaderboard_capital_to_country_world;
                            break;
                    }
                    break;
            }
            if (leaderboardId != -1) {
                Games.Leaderboards.submitScore(googleApiClient, getString(leaderboardId), score);
            }
            Fragment f = ResultFragment.newInstance(score);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_holder, f).commit();
            return -2;
        }
        else {
            String question = response.second.getQuestion();
            String choice0 = response.second.getAnswers().get(0).getChoice();
            String choice1 = response.second.getAnswers().get(1).getChoice();
            String choice2 = response.second.getAnswers().get(2).getChoice();
            String choice3 = response.second.getAnswers().get(3).getChoice();
            Fragment frag;
            switch (mode) {
                case "country2flag":
                    frag = Country2FlagFragment.newInstance(question, choice0, choice1, choice2, choice3, true);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_holder, frag).commit();
                    break;
                case "flag2country":
                    frag = Flag2CountryFragment.newInstance(question, choice0, choice1, choice2, choice3, true);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_holder, frag).commit();
                    break;
                case "country2capital":
                    frag = TextQuestionFragment.newInstance(question, choice0, choice1, choice2, choice3, mode);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_holder, frag).commit();
                    break;
                case "capital2country":
                    frag = TextQuestionFragment.newInstance(question, choice0, choice1, choice2, choice3, mode);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_holder, frag).commit();
                    break;
            }
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        if (!completed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.msg_quit_game_prompt)
                    .setMessage(R.string.msg_quit_game_detail)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            GameActivity.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null);
            builder.create().show();
        }
        else super.onBackPressed();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
