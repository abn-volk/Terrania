package net.volangvang.terrania.play.server;

import net.volangvang.terrania.play.data.Answer;
import net.volangvang.terrania.play.data.GameID;
import net.volangvang.terrania.play.data.GameRequest;
import net.volangvang.terrania.play.data.Item;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServer implements Server {

    private IGameServer server;

    public WebServer() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.followRedirects(false);
        OkHttpClient httpClient = builder.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://139.59.116.119:5000/api/games/")
                .client(httpClient)
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
        Single<Question> call = server.getQuestion(gameID).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Response<Question>, Question>() {
                    @Override
                    public Question apply(@NonNull Response<Question> response) throws Exception {
                        if (response.code() == 303)
                            return new Question(new Item(null, null), null);
                        else if (response.code() == 200) {
                            return response.body();
                        }
                        return new Question(null, null);
                    }
                })
                .onErrorReturn(new Function<Throwable, Question>() {
                    @Override
                    public Question apply(@NonNull Throwable throwable) throws Exception {
                        return new Question(null, null);
                    }
                });
        return call;
    }

    @Override
    public Single<Answer> answerQuestion(String gameID, UserAnswer answer) {
        Single<Answer> call = server.answerQuestion(gameID, answer);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return call;
    }


}
