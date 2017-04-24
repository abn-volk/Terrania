package net.volangvang.terrania.play.server;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.util.Log;

import net.volangvang.terrania.data.CountryContract;
import net.volangvang.terrania.data.CountryProvider;
import net.volangvang.terrania.play.data.Answer;
import net.volangvang.terrania.play.data.Question;
import net.volangvang.terrania.play.data.UserAnswer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LocalServer implements Server {
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
    public Pair<Status, String> newGame(String type, String continent, String language, int count) {
        Log.d("Terrania", "Opened local server with arguments: " + type + continent + language + Integer.toString(count));
        id = UUID.randomUUID().toString();
        current = -1;
        questions = new ArrayList<>(count);
        answers = new ArrayList<>(count);

        String nameColumn = ("vn".equals(language))? CountryContract.CountryEntry.COLUMN_NAME_VI : CountryContract.CountryEntry.COLUMN_NAME;
        String capitalColumn = ("vn".equals(language))? CountryContract.CountryEntry.COLUMN_CAPITAL : CountryContract.CountryEntry.COLUMN_CAPITAL_VI;
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
                choiceType = "image";
                break;
            case "flag2country":
                projection = new String[] {nameColumn, countryCodeColumn};
                questionColumn = countryCodeColumn;
                answerColumn = nameColumn;
                choiceType = "image";
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
            Log.d("Terrania", "a column name is null. Check game type arg.");
            return new Pair<>(Status.ERROR, null);
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
            Answer rightAnswer = new Answer(cursor.getString(cursor.getColumnIndex(answerColumn)), choiceType);
            List<Integer> choiceIndices = getOtherChoices(0, maxCount, indices.get(i));
            List<Answer> choices = new ArrayList<>(4);
            for (int ind : choiceIndices) {
                cursor.moveToPosition(ind);
                String choice = cursor.getString(cursor.getColumnIndex(answerColumn));
                Answer answer = new Answer(choice, choiceType);
                choices.add(answer);
            }
            int rightPos = getRandomPosition();
            choices.add(rightPos, rightAnswer);
            Question q = new Question(question, choices);
            questions.add(q);
            answers.add(rightPos);
        }
        cursor.close();

        return new Pair<>(Status.OK, id);
    }

    @Override
    public Pair<Status, Question> getQuestion(String gameID) {
        current ++;
        if (!id.equals(gameID)) {
            Log.d("Terrania", "Game ID mismatch: Expected " + id + ", got " + gameID);
            return new Pair<>(Status.ERROR, null);
        }
        if (questions == null) {
            Log.d("Terrania", "Game not initialised!");
            return new Pair<>(Status.ERROR, null);
        }
        if (current == questions.size()) {
            return new Pair<>(Status.COMPLETED, null);
        }
        return new Pair<>(Status.OK, questions.get(current));
    }

    @Override
    public Pair<Status, UserAnswer> answerQuestion(UserAnswer answer) {
        if (answers == null) {
            Log.d("Terrania", "Game not initialised!");
            return new Pair<>(Status.ERROR, null);
        }
        return new Pair<>(Status.OK, new UserAnswer(answers.get(current)));
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