package com.isluji.travial.model;

import java.util.ArrayList;

public class TriviaQuestion {

    private String statement;
    private int score;

    private ArrayList<TriviaAnswer> answers;

    public TriviaQuestion(String statement, int score) {
        this.answers = new ArrayList<>();
        this.statement = statement;
        this.score = score;
    }

    public String getStatement() {
        return statement;
    }

    public int getScore() {
        return score;
    }

    // -----------------------------------

    public void addAnswer(TriviaAnswer answer) {
        answers.add(answer);
    }

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
