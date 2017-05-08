package net.volangvang.terrania.play;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import net.volangvang.terrania.R;
import net.volangvang.terrania.play.data.Answer;
import net.volangvang.terrania.play.data.GameID;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;
import net.volangvang.terrania.play.server.Server;
import net.volangvang.terrania.play.server.WebServer;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
            Server svr = new WebServer();
            fragment = new ServerFragment();
            fragment.setServer(svr);
            fragment.setMode(mode);
            fragment.setContinent(continent);
            server = fragment.getServer();
            getSupportFragmentManager().beginTransaction().add(fragment, TAG).commit();
            String language = (Locale.getDefault().getLanguage().equals("vi"))? "vn" : "en";
            server.newGame(mode, continent, language, count).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<GameID>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull GameID gameID) {
                        id = gameID.getHashed_id();
                        fragment.setGameID(id);
                        firstQuestion();
                    }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                    Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
                    Log.wtf("Terrania", e);
                    finish();
                }
            });
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

    public Single<Integer> answer(final int index) {
        // Returns the index of the right answer or -1 if an error has occurred.
        return server.answerQuestion(id, new UserAnswer(index)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<Answer>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Answer answer) throws Exception {
                        if (answer.getCorrect_answer() == index) {
                            fragment.bumpScore();
                            if (googleApiClient != null && googleApiClient.isConnected()) {
                                Games.Achievements.increment(googleApiClient, getString(R.string.achievement_a_thousand_miles), 1);
                            }
                        } else {
                            if (googleApiClient != null && googleApiClient.isConnected()) {
                                // Baby steps - first wrong answer
                                Games.Achievements.unlock(googleApiClient, getString(R.string.achievement_baby_steps));
                            }
                        }
                    }
                })
                .map(new Function<Answer, Integer>() {
                    @Override
                    public Integer apply(@io.reactivex.annotations.NonNull Answer answer) throws Exception {
                        return answer.getCorrect_answer();
                    }
                });
    }

    private void firstQuestion() {
        server.getQuestion(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Question>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Question question) {
                        if (question.getQuestion() == null)
                            finish();
                        else displayQuestion(question);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.wtf("Terrania", e);
                    }
                });
    }

    public Single<Question> nextQuestion() {
        return server.getQuestion(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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

    public void displayQuestion(Question question) {

        if (question.getQuestion() == null)
            Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
        else {
            if (question.getQuestion().getData() == null) {
                // Completed
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
            } else {
                // Success
                String question1 = question.getQuestion().getData();
                String choice0 = question.getAnswers().get(0).getData();
                String choice1 = question.getAnswers().get(1).getData();
                String choice2 = question.getAnswers().get(2).getData();
                String choice3 = question.getAnswers().get(3).getData();
                Fragment frag;
                switch (mode) {
                    case "country2flag":
                        frag = Country2FlagFragment.newInstance(question1, choice0, choice1, choice2, choice3, true);
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_holder, frag).commit();
                        break;
                    case "flag2country":
                        frag = Flag2CountryFragment.newInstance(question1, choice0, choice1, choice2, choice3, true);
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_holder, frag).commit();
                        break;
                    case "country2capital":
                        frag = TextQuestionFragment.newInstance(question1, choice0, choice1, choice2, choice3, mode);
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_holder, frag).commit();
                        break;
                    case "capital2country":
                        frag = TextQuestionFragment.newInstance(question1, choice0, choice1, choice2, choice3, mode);
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_holder, frag).commit();
                        break;
                }
            }
        }
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
