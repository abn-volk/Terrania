package net.volangvang.terrania.play;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class QuestionFragment extends Fragment {
    protected boolean answered = false;
    protected int answer = 1;
    protected int correctAnswer = 2;

    protected void answer (int answer, int correctAnswer) {
        this.answered = true;
        this.answer = answer;
        this.correctAnswer = correctAnswer;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("answered", answered);
        outState.putInt("answer", answer);
        outState.putInt("correctAnswer", correctAnswer);
    }
}
