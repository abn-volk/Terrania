package net.volangvang.terrania.play.server;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import net.volangvang.terrania.data.CountryContract;
import net.volangvang.terrania.data.CountryProvider;
import net.volangvang.terrania.play.data.Answer;
import net.volangvang.terrania.play.data.GameID;
import net.volangvang.terrania.play.data.Item;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.reactivex.Single;

public class LocalServer implements Server{
    private final Context context;
    private final List<Integer> range;

    public LocalServer(Context context) {
        this.context = context;
        this.range = new ArrayList<>(195);
        for (int i=0; i<195; i++) {
            range.add(i);
        }
    }

    List<Question> questions;
    List<Integer> answers;
    private String id;
    private int current;

    @Override
    public Single<GameID> newGame(String type, String continent, String language, int count) {
        Log.d("Terrania", "Opened local server with arguments: " + type + continent + language + Integer.toString(count));
        id = UUID.randomUUID().toString();
        current = -1;
        questions = new ArrayList<>(count);
        answers = new ArrayList<>(count);

        String nameColumn = ("vi".equals(language))? CountryContract.CountryEntry.COLUMN_NAME_VI : CountryContract.CountryEntry.COLUMN_NAME;
        String capitalColumn = ("vi".equals(language))? CountryContract.CountryEntry.COLUMN_CAPITAL_VI : CountryContract.CountryEntry.COLUMN_CAPITAL;
        String countryCodeColumn = CountryContract.CountryEntry.COLUMN_COUNTRY_CODE;
        String questionColumn = null;
        String answerColumn = null;
        String choiceType = null;
        String[] projection = null;
        int maxCount = 195;
        switch (type) {
            case "country2flag":
                projection = new String[] {nameColumn, countryCodeColumn};
                questionColumn = nameColumn;
                answerColumn = countryCodeColumn;
                choiceType = "encoded_image";
                break;
            case "flag2country":
                projection = new String[] {nameColumn, countryCodeColumn};
                questionColumn = countryCodeColumn;
                answerColumn = nameColumn;
                choiceType = "encoded_image";
                break;
            case "country2capital":
                projection = new String[] {nameColumn, capitalColumn};
                questionColumn = nameColumn;
                answerColumn = capitalColumn;
                choiceType = "text";
                break;
            case "capital2country":
                projection = new String[] {nameColumn, capitalColumn};
                questionColumn = capitalColumn;
                answerColumn = nameColumn;
                choiceType = "text";
                break;
        }
        if (questionColumn == null) {
            return Single.error(new Exception("No questionColumn."));
        }

        switch (continent) {
            case "Africa":
                maxCount = 54;
                break;
            case "America":
                maxCount = 35;
                break;
            case "Asia":
                maxCount = 48;
                break;
            case "Europe":
                maxCount = 44;
                break;
            case "Oceania":
                maxCount = 14;
                break;
        }
        if (count > maxCount) count = maxCount;
        Cursor cursor;
        if ("World".equals(continent)) {
            cursor = context.getContentResolver().query(CountryProvider.CONTENT_URI,
                    projection, null, null, null);
        }
        else {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(CountryProvider.CONTINENT_URI, continent),
                    projection, null, null, null);
        }

        List<Integer> indices = new ArrayList<>(range.subList(0, maxCount));
        Collections.shuffle(indices);
        // Create questions
        for (int i=0; i<count; i++) {
            cursor.moveToPosition(indices.get(i));
            String question = cursor.getString(cursor.getColumnIndex(questionColumn));
            Item rightAnswer = new Item(cursor.getString(cursor.getColumnIndex(answerColumn)), choiceType);
            List<Integer> choiceIndices = getOtherChoices(0, maxCount, indices.get(i));
            List<Item> choices = new ArrayList<>(4);
            for (int ind : choiceIndices) {
                cursor.moveToPosition(ind);
                String choice = cursor.getString(cursor.getColumnIndex(answerColumn));
                Item answer = new Item(choice, choiceType);
                choices.add(answer);
            }
            int rightPos = getRandomPosition();
            choices.add(rightPos, rightAnswer);
            Question q = new Question(new Item(question, "hello"), choices, type);
            questions.add(q);
            answers.add(rightPos);
        }
        cursor.close();

        return Single.just(new GameID(id));
    }

    @Override
    public Single<Question> getQuestion(String gameID) {
        current ++;
        if (!id.equals(gameID) || questions == null)
            return Single.just(new Question(null, null, null));
        if (current == questions.size())
            return Single.just(new Question(new Item(null, null), null, null));
        return Single.just(questions.get(current));
    }

    @Override
    public Single<Answer> answerQuestion(String gameID, UserAnswer userAnswer) {
        if (answers == null)
            return Single.error(new Exception("No answers can be found."));
        else return Single.just(new Answer(answers.get(current), userAnswer.getAnswer()));

    }

    private static List<Integer> getOtherChoices (int start, int end, int current) {
        List<Integer> result = new ArrayList<>(3);
        Random random = new Random();
        int i=0;
        while (i<3) {
            int x = start + random.nextInt(end - start);
            if ((!result.contains(x)) && x != current) {
                result.add(x);
                i++;
            }
        }
        return result;
    }

    private static int getRandomPosition() {
        Random r =  new Random();
        return r.nextInt(4);
    }

}
