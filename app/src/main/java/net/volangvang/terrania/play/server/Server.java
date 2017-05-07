package net.volangvang.terrania.play.server;

import android.support.v4.util.Pair;

import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;

public interface Server {
    Pair<Status, String> newGame(String type, String continent, String language, int count);
    Pair<Status, Question> getQuestion(String gameID);
    Pair<Status, UserAnswer> answerQuestion(String gameID, UserAnswer answer);

    enum Status {
        OK,
        ERROR,
        COMPLETED
    }
}
