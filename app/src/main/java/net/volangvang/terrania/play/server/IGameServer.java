package net.volangvang.terrania.play.server;

import net.volangvang.terrania.play.data.GameID;
import net.volangvang.terrania.play.data.GameRequest;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IGameServer {
    @POST("new")
    Single<GameID> newGame(@Body GameRequest request);

    @GET("{id}/question")
    Single<Question> getQuestion(@Path("id")String hashedId);

    @POST("{id}/answer")
    Single<UserAnswer> answerQuestion(@Path("id") String hashedId, @Body UserAnswer answer);
}
