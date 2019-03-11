package com.isluji.travial.model;

import java.util.ArrayList;

public class Trivia {

    private String title;
    private int difficulty;
    private int passingScore;
    private PointOfInterest relatedPoi;

    private ArrayList<TriviaQuestion> questions;

    public Trivia(String title, int difficulty, int passingScore) {
        this.title = title;
        this.difficulty = difficulty;
        this.passingScore = passingScore;
    }

    public String getTitle() {
        return title;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public PointOfInterest getRelatedPoi() {
        return relatedPoi;
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
