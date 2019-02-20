package com.isluji.travial.model;

import java.util.ArrayList;

public class Trivia {

    private String title;
    private int difficulty;
    private int passingScore;

    private ArrayList<TriviaQuestion> questions;

    public int getPassingScore() {
        return passingScore;
    }

    // ---------------------------------------------

    public int getMaxScore() {
        int maxScore = 0;

        for (TriviaQuestion q: questions) {
            maxScore += q.getScore();
        }

        return maxScore;
    }
}
