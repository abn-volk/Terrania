package net.volangvang.terrania.play.data;

public class Answer {
    private int correct_answer;
    private int recorded_answer;
    private int current_score;

    public int getCorrect_answer() {
        return correct_answer;
    }

    public int getRecorded_answer() {
        return recorded_answer;
    }

    public int getCurrent_score() {
        return current_score;
    }

    public Answer(int correct_answer, int recorded_answer) {
        this.correct_answer = correct_answer;
        this.recorded_answer = recorded_answer;
        this.current_score = 0;
    }
}
