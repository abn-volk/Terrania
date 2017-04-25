package net.volangvang.terrania.play;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.volangvang.terrania.R;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;
import net.volangvang.terrania.play.server.LocalServer;
import net.volangvang.terrania.play.server.Server;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {
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
    private boolean completed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        fragment = (ServerFragment) getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            Intent intent = getIntent();
            mode = intent.getStringExtra(EXTRA_MODE);
            int count = intent.getIntExtra(EXTRA_COUNT, 0);
            String continent = intent.getStringExtra(EXTRA_CONTINENT);
            if (mode == null || continent == null) {
                Toast.makeText(getApplicationContext(), "Invalid extras", Toast.LENGTH_SHORT).show();
                finish();
            }
            Server svr = new LocalServer(getApplicationContext());
            fragment = new ServerFragment();
            fragment.setServer(svr);
            fragment.setMode(mode);
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

    public int answer(int index) {
        // Returns the index of the right answer or -1 if an error has occurred.
        Pair<Server.Status, UserAnswer> response = server.answerQuestion(new UserAnswer(index));
        if (response.first == Server.Status.OK) {
            if (response.second.getAnswer() == index)
                fragment.bumpScore();
            return response.second.getAnswer();
        }
        else return -1;
    }

    public int nextQuestion() {
        Pair<Server.Status, Question> response = server.getQuestion(id);
        if (response.first == Server.Status.ERROR) {
            Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
            return -1;
        }
        else if (response.first == Server.Status.COMPLETED) {
            completed = true;
            Fragment f = ResultFragment.newInstance(fragment.getScore());
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
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_holder, frag).commit();
                    break;
                case "flag2country":
                    frag = Flag2CountryFragment.newInstance(question, choice0, choice1, choice2, choice3, true);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_holder, frag).commit();
                    break;
                case "country2capital":
                        frag = TextQuestionFragment.newInstance(question, choice0, choice1, choice2, choice3, mode);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_holder, frag).commit();
                    break;
                case "capital2country":
                    frag = TextQuestionFragment.newInstance(question, choice0, choice1, choice2, choice3, mode);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_holder, frag).commit();
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
}
