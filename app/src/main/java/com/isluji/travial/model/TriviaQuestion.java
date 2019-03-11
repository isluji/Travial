package com.isluji.travial.model;

import java.util.ArrayList;

public class TriviaQuestion {

    private String statement;
    private int score;

    private ArrayList<TriviaAnswer> answers;

    public TriviaQuestion(String statement, int score) {
        this.statement = statement;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    // -----------------------------------

    public TriviaAnswer getCorrectAnswer() {
        TriviaAnswer correctAnswer = null;

        for (TriviaAnswer a: answers) {
            if (a.isCorrect()) {
                correctAnswer = a;
            }
        }

        return correctAnswer;
    }
}
