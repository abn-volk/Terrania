package net.volangvang.terrania.play.server;

import net.volangvang.terrania.play.data.GameID;
import net.volangvang.terrania.play.data.GameRequest;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServer implements Server {

    private IGameServer server;
    private String id = "";
    private Question currentQuestion;
    private Status currentStatus;
    private UserAnswer currentAnswer;

    public WebServer() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://139.59.116.119:5000/api/games/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        server = retrofit.create(IGameServer.class);
    }

    @Override
    public Single<GameID> newGame(String type, String continent, String language, int count) {
        Single<GameID> call = server.newGame(new GameRequest(type, language, count, continent));
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return call;
    }

    @Override
    public Single<Question> getQuestion(String gameID) {
        Single<Question> call = server.getQuestion(gameID);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, Question>() {
                    @Override
                    public Question apply(@NonNull Throwable throwable) throws Exception {
                        if (throwable instanceof HttpException) {
                            return new Question("", null);
                        }
                        else return new Question(null, null);
                    }
                });
        return call;
    }

    @Override
    public Single<UserAnswer> answerQuestion(String gameID, UserAnswer answer) {
        Single<UserAnswer> call = server.answerQuestion(gameID, answer);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return call;
    }


}
