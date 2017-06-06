package net.volangvang.terrania.play.server;

import net.volangvang.terrania.play.data.Answer;
import net.volangvang.terrania.play.data.GameID;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;

import io.reactivex.Single;

public interface Server {
    Single<GameID> newGame(String type, String continent, String language, int count);
    Single<Question> getQuestion(String gameID);
    Single<Answer> answerQuestion(String gameID, UserAnswer answer);
}
