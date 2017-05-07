package net.volangvang.terrania.play.server;

import android.support.v4.util.Pair;

import net.volangvang.terrania.play.data.GameID;
import net.volangvang.terrania.play.data.GameRequest;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServer implements Server {

    private IGameServer server;
    private String id = "";
    private Question currentQuestion;
    private Status currentStatus;
    private UserAnswer currentAnswer;

    public WebServer() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("url here")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
        server = retrofit.create(IGameServer.class);
    }

    @Override
    public Pair<Status, String> newGame(String type, String continent, String language, int count) {
        id = null;
        Single<GameID> call = server.newGame(new GameRequest(type, language, count, continent));
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<GameID>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull GameID gameID) {
                        id = gameID.getHashedId();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                });
        if (id == null)
            return new Pair<>(Status.ERROR, null);
        else return new Pair<>(Status.OK, id);
    }

    @Override
    public Pair<Status, Question> getQuestion(String gameID) {
        currentQuestion = null;
        currentStatus = Status.ERROR;
        Single<Question> call = server.getQuestion(gameID);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Question>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Question question) {
                        currentQuestion = question;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (e instanceof HttpException && ((HttpException) e).code() == 303) {
                            currentStatus = Status.COMPLETED;
                        }
                    }
                });
        if (currentQuestion == null)
            return new Pair<>(currentStatus, null);
        else return new Pair<>(Status.OK, currentQuestion);
    }

    @Override
    public Pair<Status, UserAnswer> answerQuestion(String gameID, UserAnswer answer) {
        currentAnswer = null;
        Single<UserAnswer> call = server.answerQuestion(gameID, answer);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<UserAnswer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull UserAnswer userAnswer) {
                        currentAnswer = userAnswer;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
        if (currentAnswer == null)
            return new Pair<>(Status.ERROR, null);
        else return new Pair<>(Status.OK, currentAnswer);
    }


}
